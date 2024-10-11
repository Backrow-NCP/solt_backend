package org.backrow.solt.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.NaverConfig;
import org.backrow.solt.dto.file.UploadResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@Log4j2
public class FileStorageServiceImpl implements FileStorageService {

    final AmazonS3 s3;

    public FileStorageServiceImpl(NaverConfig naverConfig) {
        s3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        naverConfig.getEndPoint(), naverConfig.getRegionName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                        naverConfig.getAccessKey(), naverConfig.getSecretKey())))
                .build();
    }

    @Override
    public UploadResultDTO uploadFile(String bucketName, String directoryPath, MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
            Boolean image = false;

        try (InputStream fileIn = file.getInputStream()) {
            String fileName = UUID.randomUUID().toString();
            String fileType = file.getOriginalFilename();
            if(fileType.contains(".jpg") || fileType.contains(".jpeg") || fileType.contains(".png")) {
                image = true;
            }

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());

            PutObjectRequest objectRequest = new PutObjectRequest(
                    bucketName,
                    directoryPath + fileName,
                    fileIn,
                    objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(objectRequest);

            UploadResultDTO uploadResultDTO = UploadResultDTO.builder()
                    .fileName(fileName)
                    .image(image)
                    .build();

            return uploadResultDTO;

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 오류", e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String filename) {
        try {
            s3.deleteObject(bucketName,filename);
        }catch(AmazonS3Exception e){
            e.printStackTrace();
        }catch(SdkClientException e) {
            e.printStackTrace();
        }
    }
}
