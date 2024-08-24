package com.server.ttoon.domain.member.dto.response;

import com.server.ttoon.domain.member.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendInfoDto {
    private String nickName;
    private String profileUrl;
    private Long friendId;
}
