package com.server.ttoon.domain.member.service;

import com.server.ttoon.common.response.ApiResponse;
import com.server.ttoon.common.response.status.SuccessStatus;
import com.server.ttoon.domain.member.dto.request.AskRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    public ResponseEntity<ApiResponse<?>> sendEmail(AskRequestDto askRequestDto){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject("[문의사항] " + askRequestDto.getCategory());
        message.setTo("ttoon.contact@gmail.com");
        message.setText(askRequestDto.getReceiver()+"\n"+ "\n" +askRequestDto.getBody());

        javaMailSender.send(message);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus._OK));
    }

}
