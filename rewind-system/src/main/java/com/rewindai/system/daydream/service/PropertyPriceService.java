package com.rewindai.system.daydream.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rewindai.system.aijudge.client.DoubaoApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * 房价估算 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyPriceService {

    private final DoubaoApiClient doubaoApiClient;

    /**
     * 房价估算响应
     */
    public static class PropertyPriceEstimate {
        public BigDecimal averagePricePerSqm;
        public BigDecimal estimatedArea;
        public Integer estimatedUnitCount;
        public String locationDescription;
        public String year;
        public String dataSource;
    }

    /**
     * 估算房价
     */
    public PropertyPriceEstimate estimatePropertyPrice(
            String province, String city, String district,
            String year, BigDecimal totalBudget) {

        log.info("开始房价估算: province={}, city={}, district={}, year={}, budget={}",
                province, city, district, year, totalBudget);

        try {
            // 调用豆包API进行估算
            String systemPrompt = buildEstimateSystemPrompt();
            String userMessage = buildEstimateUserMessage(province, city, district, year, totalBudget);

            String response = doubaoApiClient.chat(systemPrompt, userMessage);
            log.info("房价估算API响应: {}", response);

            // 解析响应
            return parseEstimateResponse(response, province, city, district, year, totalBudget);

        } catch (Exception e) {
            log.error("房价估算失败，使用降级方案", e);
            return fallbackEstimate(province, city, district, year, totalBudget);
        }
    }

    /**
     * 构建估算系统提示词
     */
    private String buildEstimateSystemPrompt() {
        return """
            你是一位专业的房地产历史数据分析师，擅长查询和分析中国各城市各年份的商品住宅平均房价。

            你的任务：
            1. 根据提供的省份、城市、区县和年份，查询该地区该年份的商品住宅平均房价
            2. 根据总预算计算可购买的面积（按建筑面积计算）
            3. 如果有多套倾向，按常见的100平方米每套估算可购买套数

            请以JSON格式返回结果，格式如下：
            {
              "averagePricePerSqm": 每平方米平均价格（元，整数）,
              "estimatedArea": 预估可购买面积（平方米，保留1位小数）,
              "estimatedUnitCount": 预估可购买套数（整数）,
              "locationDescription": "位置描述",
              "year": "年份",
              "dataSource": "数据来源说明"
            }

            注意事项：
            - 如果找不到精确到区县的数据，使用城市级数据
            - 平均房价取该年份该地区的合理中间值
            - 按常见的100平方米每套估算套数
            - 数据来源注明为"基于历史数据估算"
            """;
    }

    /**
     * 构建估算用户消息
     */
    private String buildEstimateUserMessage(String province, String city, String district,
                                             String year, BigDecimal totalBudget) {
        StringBuilder sb = new StringBuilder();
        sb.append("请估算以下地区的房价：\n");
        sb.append("省份：").append(province).append("\n");
        sb.append("城市：").append(city).append("\n");
        if (district != null && !district.isEmpty()) {
            sb.append("区县：").append(district).append("\n");
        }
        sb.append("年份：").append(year).append("\n");
        sb.append("总预算：¥").append(totalBudget).append("\n");
        sb.append("\n请返回JSON格式的估算结果。");
        return sb.toString();
    }

    /**
     * 解析估算响应
     */
    private PropertyPriceEstimate parseEstimateResponse(String response,
                                                          String province, String city, String district,
                                                          String year, BigDecimal totalBudget) {
        PropertyPriceEstimate result = new PropertyPriceEstimate();

        try {
            // 尝试解析JSON
            JSONObject json = JSONUtil.parseObj(response);
            result.averagePricePerSqm = json.getBigDecimal("averagePricePerSqm", BigDecimal.ZERO);
            result.estimatedArea = json.getBigDecimal("estimatedArea", BigDecimal.ZERO);
            result.estimatedUnitCount = json.getInt("estimatedUnitCount", 0);
            result.locationDescription = json.getStr("locationDescription", buildLocationDesc(province, city, district));
            result.year = json.getStr("year", year);
            result.dataSource = json.getStr("dataSource", "基于历史数据估算");
        } catch (Exception e) {
            log.warn("解析房价估算响应失败，使用降级方案", e);
            return fallbackEstimate(province, city, district, year, totalBudget);
        }

        return result;
    }

    /**
     * 降级方案 - 简单估算
     */
    private PropertyPriceEstimate fallbackEstimate(String province, String city, String district,
                                                    String year, BigDecimal totalBudget) {
        PropertyPriceEstimate result = new PropertyPriceEstimate();

        // 基于城市和年份的简单估算
        BigDecimal avgPrice = estimateAveragePrice(city, year);
        BigDecimal area = totalBudget.divide(avgPrice, 1, RoundingMode.HALF_UP);
        Integer unitCount = area.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).intValue();

        result.averagePricePerSqm = avgPrice;
        result.estimatedArea = area;
        result.estimatedUnitCount = unitCount;
        result.locationDescription = buildLocationDesc(province, city, district);
        result.year = year;
        result.dataSource = "基于历史数据估算（降级方案）";

        log.info("使用降级方案估算房价: city={}, year={}, avgPrice={}, area={}",
                city, year, avgPrice, area);

        return result;
    }

    /**
     * 简单估算平均房价（降级用）
     */
    private BigDecimal estimateAveragePrice(String city, String year) {
        // 简单的基准价格（2020年基准）
        Map<String, BigDecimal> cityBasePrices = new HashMap<>();
        cityBasePrices.put("北京", new BigDecimal("60000"));
        cityBasePrices.put("上海", new BigDecimal("55000"));
        cityBasePrices.put("深圳", new BigDecimal("58000"));
        cityBasePrices.put("广州", new BigDecimal("40000"));
        cityBasePrices.put("杭州", new BigDecimal("38000"));
        cityBasePrices.put("南京", new BigDecimal("32000"));
        cityBasePrices.put("成都", new BigDecimal("20000"));
        cityBasePrices.put("武汉", new BigDecimal("22000"));
        cityBasePrices.put("西安", new BigDecimal("18000"));
        cityBasePrices.put("重庆", new BigDecimal("16000"));
        cityBasePrices.put("天津", new BigDecimal("25000"));
        cityBasePrices.put("苏州", new BigDecimal("30000"));
        cityBasePrices.put("厦门", new BigDecimal("45000"));
        cityBasePrices.put("长沙", new BigDecimal("14000"));
        cityBasePrices.put("郑州", new BigDecimal("16000"));
        cityBasePrices.put("昆明", new BigDecimal("15000"));
        cityBasePrices.put("大连", new BigDecimal("18000"));
        cityBasePrices.put("沈阳", new BigDecimal("12000"));
        cityBasePrices.put("哈尔滨", new BigDecimal("10000"));
        cityBasePrices.put("青岛", new BigDecimal("22000"));
        cityBasePrices.put("宁波", new BigDecimal("28000"));
        cityBasePrices.put("无锡", new BigDecimal("20000"));
        cityBasePrices.put("合肥", new BigDecimal("19000"));
        cityBasePrices.put("福州", new BigDecimal("25000"));
        cityBasePrices.put("济南", new BigDecimal("17000"));
        cityBasePrices.put("石家庄", new BigDecimal("15000"));
        cityBasePrices.put("南昌", new BigDecimal("13000"));
        cityBasePrices.put("贵阳", new BigDecimal("11000"));
        cityBasePrices.put("海口", new BigDecimal("18000"));
        cityBasePrices.put("兰州", new BigDecimal("12000"));
        cityBasePrices.put("西宁", new BigDecimal("10000"));
        cityBasePrices.put("银川", new BigDecimal("9000"));
        cityBasePrices.put("呼和浩特", new BigDecimal("11000"));
        cityBasePrices.put("乌鲁木齐", new BigDecimal("9500"));
        cityBasePrices.put("拉萨", new BigDecimal("13000"));
        cityBasePrices.put("深圳", new BigDecimal("58000"));
        cityBasePrices.put("珠海", new BigDecimal("28000"));
        cityBasePrices.put("佛山", new BigDecimal("20000"));
        cityBasePrices.put("东莞", new BigDecimal("22000"));
        cityBasePrices.put("中山", new BigDecimal("15000"));

        // 默认价格
        BigDecimal basePrice = cityBasePrices.getOrDefault(city, new BigDecimal("15000"));

        // 根据年份调整（2020年为基准）
        int targetYear;
        try {
            targetYear = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            targetYear = 2020;
        }

        int yearDiff = targetYear - 2020;
        // 每年约5%的变化
        BigDecimal multiplier = BigDecimal.ONE.add(new BigDecimal("0.05").pow(Math.abs(yearDiff)));
        if (yearDiff < 0) {
            multiplier = BigDecimal.ONE.divide(multiplier, 4, RoundingMode.HALF_UP);
        }

        return basePrice.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 构建位置描述
     */
    private String buildLocationDesc(String province, String city, String district) {
        StringBuilder sb = new StringBuilder();
        if (province != null) {
            sb.append(province);
        }
        if (city != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(city);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(district);
        }
        return sb.toString();
    }
}
