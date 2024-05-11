package com.server.ttoon.domain.member.dto.response;

import com.server.ttoon.domain.member.entity.Provider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDto {

    private String nickName;
    private String imageUrl;
    private String fileName;
    private int point;
    private String email;
    private Provider provider;
}
