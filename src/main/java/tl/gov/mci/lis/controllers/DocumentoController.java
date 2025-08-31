package tl.gov.mci.lis.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.configs.minio.MinioService;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.documento.DocumentoDownload;
import tl.gov.mci.lis.repositories.documento.DocumentoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
public class DocumentoController {
    private final MinioService minioService;
    private final DocumentoRepository documentoRepository;

    @PostMapping("/{username}/upload")
    ResponseEntity<?> uploadDocumentos(@PathVariable String username, @RequestParam("files") List<MultipartFile> files) {
        if (!files.isEmpty()) {
            return ResponseEntity.ok(minioService.uploadFiles(username, files));
        } else {
            return ResponseEntity.badRequest().body("O Documento é obrigatório.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadRecibo(
            @PathVariable Long id
    ) {

        Documento documento = documentoRepository
                .findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Documento nao encontrado com ID: " + id);
                });

        InputStreamResource stream = minioService.downloadFileAsStream(documento);

        DocumentoDownload documentoDownload = new DocumentoDownload(stream, documento.getNome(), documento.getTipo());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentoDownload.fileName() + "\"")
                .contentType(MediaType.parseMediaType(
                        documentoDownload.contentType() != null ? documentoDownload.contentType() : "application/octet-stream"))
                .body(new InputStreamResource(documentoDownload.stream()));
    }

}
