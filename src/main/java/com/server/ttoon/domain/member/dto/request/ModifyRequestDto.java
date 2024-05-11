package com.server.ttoon.domain.member.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ModifyRequestDto {

    private String nickName;
    private String oldImageName;
}
