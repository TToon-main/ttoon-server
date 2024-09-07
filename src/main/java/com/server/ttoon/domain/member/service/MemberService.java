package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.domain.member.dto.request.ModifyRequestDto;
import com.server.ttoon.security.jwt.dto.request.AuthorizationCodeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Optional;

public interface MemberService {

    ResponseEntity<ApiResponse<?>> getAccountInfo(Long memberId);

    ResponseEntity<ApiResponse<?>> modifyProfile(Long memberId, String nickName, String newImage, Boolean isDelete);

    ResponseEntity<ApiResponse<?>> revoke(Long memberId, Optional<AuthorizationCodeDto> appleIdentityTokenDto, String sender) throws IOException;
    ResponseEntity<ApiResponse<?>> addFriend(Long memberId, String nickName);
    ResponseEntity<ApiResponse<?>> acceptInvite(Long friendId);
    ResponseEntity<ApiResponse<?>> deleteFriend(Long friendId);
    ResponseEntity<ApiResponse<?>> getFriends(Long memberId, Pageable pageable);
    ResponseEntity<ApiResponse<?>> getRequestFriends(Long memberId, Pageable pageable);
    ResponseEntity<ApiResponse<?>> getSearchUsers(Long memberId, Pageable pageable,String name);

}
