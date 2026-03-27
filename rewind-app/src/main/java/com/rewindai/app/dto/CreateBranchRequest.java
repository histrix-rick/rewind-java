package com.rewindai.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建分支请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
public class CreateBranchRequest {

    @NotBlank(message = "分支名称不能为空")
    private String branchName;
}
