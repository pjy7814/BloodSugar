package kr.co.sugarmanager.image.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kr.co.sugarmanager.image.dto.ImageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ImageS3Repository implements ImageRepository{
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 업로드
    @Override
    public String uploadS3Service(ImageDTO imageDTO, String fileName) throws IOException {
        StringBuilder path = new StringBuilder();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageDTO.getSize());
        metadata.setContentType(imageDTO.getContentType());

        try (InputStream inputStream = new ByteArrayInputStream(imageDTO.getFile())) {
            path.append(imageDTO.getImageType()).append("/").append(fileName);
            amazonS3.putObject(bucket, path.toString(), inputStream, metadata);

            return path.toString();
        }
    }

    // 파일 URL
    @Override
    public String getFileURL(String path) {
        return amazonS3.getUrl(bucket, path).toString();
    }

    // 파일 이름 생성
    @Override
    public String createFileName(String extension) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb.append(UUID.randomUUID()).append(".").append(extension));
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            amazonS3.deleteObject(bucket, filePath);
        } catch (AmazonServiceException e) {
            log.error("S3 Service Error: {}", e);
        }
    }
}
