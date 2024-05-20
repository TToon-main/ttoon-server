package com.server.ttoon.security.jwt.dto.request;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.Optional;

@Data
public class AuthorizationCodeDto {

    private String authorizationCode;

    private String revokeReason;
}
