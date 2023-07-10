package com.revature.Flumblr.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.revature.Flumblr.utils.custom_exceptions.FileNotUploadedException;

@Service
public class S3StorageService {

    private final AmazonS3 amazonS3Client;

    @Value("${name}")
    private String bucketName;

    public S3StorageService(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String uploadFile(MultipartFile file) {
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        File convertedFile = convertMultiPartFileToFile(file);
        String fileUrl = uploadFileToS3Bucket(fileName, convertedFile);
        convertedFile.delete();
        return fileUrl;
    }

    public byte[] downloadFile(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String key = extractS3KeyFromUrl(url);
            S3Object s3Object = amazonS3Client.getObject(bucketName, key);
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            byte[] content = IOUtils.toByteArray(objectInputStream);
            objectInputStream.close();
            return content;
        } catch (IOException e) {
            throw new FileNotUploadedException("Failed to retrieve file from S3: " + e.getMessage());
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId + "-" + originalFileName;
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
            fileOutputStream.write(file.getBytes());
        } catch (IOException e) {
            throw new FileNotUploadedException("Failed to upload file: " + file.getOriginalFilename());
        }
        return convertedFile;
    }

    private String uploadFileToS3Bucket(String fileName, File file) {
        amazonS3Client.putObject(bucketName, fileName, file);
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private String extractS3KeyFromUrl(URL url) {
        return url.getPath().substring(1); // Remove leading slash from URL path
    }

    public void deleteFileFromS3Bucket(String fileUrl) {
    try {
        URL url = new URL(fileUrl);
        String key = extractS3KeyFromUrl(url);
        amazonS3Client.deleteObject(bucketName, key);
    } catch (IOException e) {
        throw new RuntimeException("Failed to delete file from S3: " + e.getMessage());
    }
    }

}
