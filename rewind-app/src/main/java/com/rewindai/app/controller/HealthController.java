package com.rewindai.app.controller;

import com.rewindai.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 *
 * @author Rewind.ai Team
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "健康检查", description = "服务健康检查接口")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public Result<String> health() {
        return Result.success("Rewind.ai App Service is running");
    }
}
