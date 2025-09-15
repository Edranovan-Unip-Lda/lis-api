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
                .orElseThrow(() -> new ResourceNotFoundException("Documento nao encontrado com ID: " + id));

        InputStreamResource stream = minioService.downloadFileAsStream(documento);

        DocumentoDownload documentoDownload = new DocumentoDownload(stream, documento.getNome(), documento.getTipo());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentoDownload.fileName() + "\"")
                .contentType(MediaType.parseMediaType(
                        documentoDownload.contentType() != null ? documentoDownload.contentType() : "application/octet-stream"))
                .body(new InputStreamResource(documentoDownload.stream()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteDocumento(@PathVariable Long id) {
        Documento doc = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento nao exite found: " + id));

        // 2) Detach bidirectional links to keep the persistence context consistent
        if (doc.getPedidoInscricaoCadastro() != null) {
            var pedido = doc.getPedidoInscricaoCadastro();
            // If the parent has a mapped collection, remove this child from it
            if (pedido.getDocumentos() != null) {
                pedido.getDocumentos().remove(doc);
            }
            doc.setPedidoInscricaoCadastro(null);
        }
        if (doc.getAutoVistoria() != null) {
            var av = doc.getAutoVistoria();
            if (av.getDocumentos() != null) {
                av.getDocumentos().remove(doc);
            }
            doc.setAutoVistoria(null);
        }
        if (doc.getFatura() != null) {
            // One-to-one via FK on Documento — null out to avoid managed graph holding a ref
            doc.setFatura(null);
        }

        // 3) Delete the row
        documentoRepository.delete(doc);
        return ResponseEntity.ok(id);
    }

}
