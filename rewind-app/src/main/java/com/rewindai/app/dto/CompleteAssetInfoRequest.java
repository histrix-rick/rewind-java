package com.rewindai.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 完成资产信息请求 DTO
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteAssetInfoRequest {

    private Boolean updateRelationship;
    private Boolean updateIdentity;
    private Map<String, Object> relationshipUpdates;
    private Map<String, Object> identityUpdates;
}
