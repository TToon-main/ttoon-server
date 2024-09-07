package com.server.ttoon.domain.member.dto.response;


import com.server.ttoon.domain.member.entity.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDto {
    private String nickName;
    private String profileUrl;
    private Long friendId;
    private Status status;
}
