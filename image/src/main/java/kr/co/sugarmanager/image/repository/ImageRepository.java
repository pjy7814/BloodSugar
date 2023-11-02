package kr.co.sugarmanager.image.repository;

import kr.co.sugarmanager.image.dto.ImageDTO;

import java.io.IOException;

public interface ImageRepository {
    String uploadS3Service(ImageDTO imageDTO, String path) throws IOException;
    String getFileURL(String path);
    String createFileName(String extension);
}
