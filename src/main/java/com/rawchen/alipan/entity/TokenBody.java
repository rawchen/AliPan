package com.rawchen.alipan.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author RawChen
 * @date 2025-06-01 16:13
 */
@Data
public class TokenBody {

    @JSONField(name = "grant_type")
    private String grantType;

    @JSONField(name = "refresh_token")
    private String refreshToken;

    @JSONField(name = "code_verifier")
    private String codeVerifier;

}
