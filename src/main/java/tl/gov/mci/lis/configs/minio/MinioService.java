package tl.gov.mci.lis.configs.minio;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.exceptions.FileDownloadUploadException;
import tl.gov.mci.lis.models.documento.Documento;

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


    public Documento uploadFile(String username, MultipartFile file) {
        logger.info("Carregando arquivo: {}", file.getOriginalFilename());

        String original = Objects.requireNonNull(file.getOriginalFilename());
        String path = String.format("%s/%s/%s/%s",
                username,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM")),
                original);

        Path tempFile;
        try {
            String ext = original.contains(".") ? original.substring(original.lastIndexOf(".")) : "";
            String base = original.contains(".") ? original.substring(0, original.lastIndexOf(".")) : original;
            tempFile = Files.createTempFile(base, ext);

            // Copy input stream to temp file
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucketName).build());
            }

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(path)
                            .filename(tempFile.toString())
                            .contentType(file.getContentType())
                            .build()
            );

            // Populate and return your Documento
            Documento doc = new Documento();
            doc.setCaminho(path);
            doc.setNome(original);
            doc.setTipo(file.getContentType());
            doc.setTamanho(file.getSize());

            logger.info("Arquivo carregado: {}, caminho: {}", original, path);
            return doc;
        } catch (Exception e) {
            logger.error("Falha ao fazer upload:  {}: {}", original, e.getMessage(), e);
            throw new FileDownloadUploadException("Falha ao fazer upload: " + original);
        }
    }


    public InputStreamResource downloadFileAsStream(Documento documento) {
        logger.info("DownloadFile method: {}", documento.getNome());
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(documento.getCaminho())
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            logger.error("Erro ao baixar o documento '{}': {}", documento.getNome(), e.getMessage(), e);
            throw new FileDownloadUploadException("Falha ao baixar documento: " + documento.getNome());
        }
    }

    public void archiveFile(Documento documento) {
        logger.info("Arquivando documento: {}", documento.getNome());
        String archivedObjectName = "archived/" + documento.getNome().substring(documento.getNome().lastIndexOf('/') + 1);

        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(archivedObjectName)
                            .source(
                                    CopySource.builder()
                                            .bucket(minioBucketName)
                                            .object(documento.getCaminho())
                                            .build()
                            )
                            .build()
            );

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioBucketName)
                    .object(documento.getCaminho())
                    .build());

        } catch (Exception e) {
            logger.error("Erro ao arquivar o documento '{}': {}", documento.getNome(), e.getMessage(), e);
            throw new FileDownloadUploadException("Falha ao arquivar documento: " + documento.getNome());
        }
        logger.info("Documento arquivado no S3 Object: {}", documento.getNome());
    }
}
