package com.server.ttoon.common.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3Service {
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /*
     * 업로드 할 파일을 S3 버킷에 담고 Url(파일 경로) 받아 오는 메소드.
     * */
    public String saveFile(MultipartFile multipartFile, String dirName) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = dirName + "/" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // S3 에 파일 업로드 및 업로드 된 파일의 URL 가져오기
        amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    // 이미지 수정으로 인해 기존 이미지 삭제 메소드
    public void deleteImage(String fileName, String dirName) {
        String fileKey = dirName + "/" + fileName;

        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileKey));
    }
}
