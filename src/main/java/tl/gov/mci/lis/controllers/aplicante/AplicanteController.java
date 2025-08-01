package tl.gov.mci.lis.controllers.aplicante;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.aplicante.AplicantePageDto;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.documento.DocumentoDownload;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.pagamento.FaturaService;

@RestController
@RequestMapping("/api/v1/aplicantes")
@RequiredArgsConstructor
public class AplicanteController {
    private final AplicanteService aplicanteService;
    private final FaturaService faturaService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("")
    ResponseEntity<Page<AplicantePageDto>> getPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return new ResponseEntity<>(aplicanteService.getPage(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<AplicanteDto> getAplicante(@PathVariable Long id) {
        return new ResponseEntity<>(aplicanteService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/{aplicanteId}/pedidos")
    ResponseEntity<PedidoInscricaoCadastroDto> createPedidoInscricaoCadastro(
            @RequestParam(name = "tipo") AplicanteType tipo,
            @PathVariable Long aplicanteId,
            @RequestBody PedidoInscricaoCadastro obj
    ) throws BadRequestException {
        if (tipo == AplicanteType.CADASTRO) {
            return new ResponseEntity<>(aplicanteService.createPedidoInscricaoCadastro(aplicanteId, obj), HttpStatus.CREATED);
        } else {
            return null;
        }
    }

    @PutMapping("/{aplicanteId}/pedidos/{pedidoId}")
    ResponseEntity<PedidoInscricaoCadastroDto> updatePedidoInscricaoCadastro(
            @RequestParam(name = "tipo") AplicanteType tipo,
            @PathVariable Long aplicanteId,
            @PathVariable Long pedidoId,
            @RequestBody PedidoInscricaoCadastro obj
    ) throws BadRequestException {
        if (tipo == AplicanteType.CADASTRO) {
            return new ResponseEntity<>(aplicanteService.updatePedidoInscricaoCadastro(aplicanteId, pedidoId, obj), HttpStatus.CREATED);
        } else {
            return null;
        }
    }

    @PutMapping("/{aplicanteId}/pedidos/{pedidoId}/faturas/{faturaId}/upload/{username}")
    ResponseEntity<?> uploadFatura(
            @PathVariable Long aplicanteId,
            @PathVariable Long pedidoId,
            @PathVariable Long faturaId,
            @PathVariable String username,
            @RequestParam("file") MultipartFile file
    ) {
        if (file != null) {
            return new ResponseEntity<>(faturaService.saveRecibo(aplicanteId, pedidoId, username, faturaId, file), HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().body("O Recibo é obrigatório.");
    }

    @GetMapping("/{aplicanteId}/pedidos/{pedidoId}/faturas/{faturaId}/recibos/{reciboId}")
    public ResponseEntity<InputStreamResource> downloadRecibo(
            @PathVariable Long aplicanteId,
            @PathVariable Long pedidoId,
            @PathVariable Long faturaId,
            @PathVariable Long reciboId
    ) {

        DocumentoDownload documentoDownload = faturaService.downloadRecibo(reciboId, faturaId, pedidoId, aplicanteId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentoDownload.fileName() + "\"")
                .contentType(MediaType.parseMediaType(
                        documentoDownload.contentType() != null ? documentoDownload.contentType() : "application/octet-stream"))
                .body(new InputStreamResource(documentoDownload.stream()));
    }

    @DeleteMapping("/{aplicanteId}/pedidos/{pedidoId}/faturas/{faturaId}/recibos/{reciboId}")
    public ResponseEntity<DocumentoDto> deleteRecibo(
            @PathVariable Long aplicanteId,
            @PathVariable Long pedidoId,
            @PathVariable Long faturaId,
            @PathVariable Long reciboId
    ) {
        return new ResponseEntity<>(faturaService.deleteRecibo(reciboId, faturaId, pedidoId, aplicanteId), HttpStatus.OK);
    }
}
