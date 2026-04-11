package com.rewindai.common.core.util;

import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.text.csv.CsvWriteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

/**
 * CSV导出工具类
 *
 * @author Rewind.ai Team
 */
@Slf4j
public class CsvExportUtil {

    private static final DateTimeFormatter DEFAULT_FILENAME_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter STANDARD_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Charset GBK_CHARSET = Charset.forName("GBK");

    /**
     * 格式化日期时间为标准格式 (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(STANDARD_DATETIME_FORMAT);
    }

    /**
     * 格式化日期时间为标准格式 (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(STANDARD_DATETIME_FORMAT);
    }

    /**
     * 导出CSV文件 (使用GBK编码解决Excel中文乱码问题)
     *
     * @param filename  文件名（不含.csv后缀）
     * @param headers   CSV表头
     * @param data      数据列表
     * @param converter 数据行转换器
     * @param <T>       数据类型
     * @return ResponseEntity
     */
    public static <T> ResponseEntity<byte[]> export(String filename, String[] headers, List<T> data,
                                                       Function<T, String[]> converter) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteStream, GBK_CHARSET);
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);

            CsvWriteConfig config = CsvWriteConfig.defaultConfig();
            config.setAlwaysDelimitText(true);

            try (CsvWriter csvWriter = new CsvWriter(printWriter, config)) {
                csvWriter.write(headers);
                for (T item : data) {
                    csvWriter.write(converter.apply(item));
                }
            }

            printWriter.flush();
            byte[] bytes = byteStream.toByteArray();

            String fullFilename = filename + "_" + LocalDateTime.now().format(DEFAULT_FILENAME_DATETIME_FORMAT) + ".csv";
            String encodedFilename = URLEncoder.encode(fullFilename, StandardCharsets.UTF_8).replace("+", "%20");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            httpHeaders.setContentDispositionFormData("attachment", encodedFilename);
            httpHeaders.setContentLength(bytes.length);

            return ResponseEntity.ok().headers(httpHeaders).body(bytes);
        } catch (Exception e) {
            log.error("导出CSV失败: filename={}", filename, e);
            throw new RuntimeException("导出失败", e);
        }
    }

    /**
     * 简单的字符串转义
     */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
}
