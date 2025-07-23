package tl.gov.mci.lis.configs.minio;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.exceptions.FileDownloadUploadException;
import tl.gov.mci.lis.models.documento.Documento;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MinioService {
    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);
    @Value("${minio.bucket}")
    private String minioBucketName;

    private final MinioClient minioClient;

    public String uploadFile(String numeroAplicante, MultipartFile file) {
        logger.info("Carregando documento: {}, {}", numeroAplicante, file.getOriginalFilename());

        String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
        String fileBaseName = originalFileName.contains(".") ?
                originalFileName.substring(0, originalFileName.lastIndexOf('.')) : originalFileName;
        String extension = originalFileName.contains(".") ?
                originalFileName.substring(originalFileName.lastIndexOf('.')) : "";

        String filePath = String.format("%s/%s/%s/%s",
                numeroAplicante,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM")),
                originalFileName
        );

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(fileBaseName, extension);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Verifica se o bucket existe, caso não, cria.
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucketName).build());
            }

            ObjectWriteResponse response = minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(filePath)
                            .filename(tempFile.toString())
                            .contentType(file.getContentType())
                            .build());

            logger.info("Documento '{}' carregado para '{}'", originalFileName, filePath);

            return response.object();
        } catch (Exception e) {
            logger.error("Erro ao fazer upload do arquivo '{}': {}", originalFileName, e.getMessage(), e);
            // Optionally wrap and throw a custom exception with proper response status
            throw new FileDownloadUploadException("Falha ao fazer upload do arquivo: " + originalFileName);
        } finally {
            // Clean up temp file
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ioException) {
                    logger.warn("Não foi possível deletar o arquivo temporário: {}", tempFile, ioException);
                }
            }
        }
    }


    public byte[] download(Documento documento) throws FileDownloadUploadException {
        logger.info("DownloadFile method: {}", documento.getNome());
        GetObjectArgs builder = GetObjectArgs.builder()
                .bucket(minioBucketName)
                .object(documento.getCaminho())
                .build();
        try (InputStream stream = minioClient.getObject(builder)) {
            return stream.readAllBytes();
        } catch (Exception e) {
            logger.error("Erro ao baixar o documento '{}': {}", documento.getNome(), e.getMessage(), e);
            throw new FileDownloadUploadException("Falha ao baixar documento: " + documento.getNome());
        }
    }
}
