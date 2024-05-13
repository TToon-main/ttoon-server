package com.server.ttoon.common.config;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /*
     * 업로드 할 파일을 S3 버킷에 담고 Url(파일 경로) 받아 오는 메소드.
     * */
    public String saveFile(MultipartFile multipartFile, String dirName) throws IOException {
        String fileName = UUID.randomUUID() + multipartFile.getOriginalFilename();
        String customFileName = dirName + "/" + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // S3 에 파일 업로드 및 업로드 된 파일의 URL 가져오기
        amazonS3.putObject(bucket, customFileName, multipartFile.getInputStream(), metadata);
        return customFileName;
    }

    // 이미지 수정으로 인해 기존 이미지 삭제 메소드
    public void deleteImage(String keyName) {

        if(keyName == null){
            return;
        }

        amazonS3.deleteObject(new DeleteObjectRequest(bucket, keyName));
    }

    // image url 가져오는 메소드.
    public String getPresignedURL (String keyName) {

        if(keyName == null){
            return null;
        }

        String preSignedURL = "";
        // presigned URL이 유효하게 동작할 만료기한 설정 (5분)
        Date expiration = new Date();
        Long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5;
        expiration.setTime(expTimeMillis);

        try {
            // presigned URL 발급
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, keyName)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            preSignedURL = url.toString();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return preSignedURL;
    }
}
