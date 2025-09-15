package tl.gov.mci.lis.controllers.aplicante;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.licenca.PedidoLicencaAtividadeDto;
import tl.gov.mci.lis.dtos.licenca.PedidoLicencaAtividadeReqsDto;
import tl.gov.mci.lis.dtos.mappers.AplicanteMapper;
import tl.gov.mci.lis.dtos.mappers.LicencaMapper;
import tl.gov.mci.lis.dtos.mappers.VistoriaMapper;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;
import tl.gov.mci.lis.dtos.vistoria.AutoVistoriaDto;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaDto;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaReqDto;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.documento.DocumentoDownload;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.atividade.PedidoLicencaAtividadeService;
import tl.gov.mci.lis.services.pagamento.FaturaService;
import tl.gov.mci.lis.services.vistoria.AutoVistoriaService;
import tl.gov.mci.lis.services.vistoria.PedidoVistoriaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aplicantes")
@RequiredArgsConstructor
public class AplicanteController {
    private final AplicanteService aplicanteService;
    private final FaturaService faturaService;
    private final LicencaMapper licencaMapper;
    private final PedidoLicencaAtividadeService pedidoLicencaAtividadeService;
    private final PedidoVistoriaService pedidoVistoriaService;
    private final VistoriaMapper vistoriaMapper;
    private final AplicanteMapper aplicanteMapper;
    private final AutoVistoriaService autoVistoriaService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    @GetMapping("")
    ResponseEntity<Page<AplicanteDto>> getPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(aplicanteService.getPage(page, size));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    @GetMapping("/{id}")
    ResponseEntity<AplicanteDto> getAplicante(@PathVariable Long id) {
        return new ResponseEntity<>(aplicanteMapper.toDto(aplicanteService.getById(id)), HttpStatus.OK);
    }

    @PostMapping("/{aplicanteId}/pedidos/cadastro")
    ResponseEntity<PedidoInscricaoCadastroDto> createPedidoInscricaoCadastro(
            @PathVariable Long aplicanteId,
            @RequestBody PedidoInscricaoCadastro obj
    ) {
        return new ResponseEntity<>(aplicanteService.createPedidoInscricaoCadastro(aplicanteId, obj), HttpStatus.CREATED);
    }

    @PostMapping("/{aplicanteId}/pedidos/atividade")
    ResponseEntity<PedidoLicencaAtividadeDto> createPedidoLicencaAtividade(
            @PathVariable Long aplicanteId,
            @RequestBody PedidoLicencaAtividadeReqsDto incomingObj
    ) {
        return new ResponseEntity<>(
                licencaMapper.toDto(pedidoLicencaAtividadeService.create(aplicanteId, licencaMapper.toEntity(incomingObj))),
                HttpStatus.CREATED);
    }

    @GetMapping("/{aplicanteId}/pedidos/atividade")
    ResponseEntity<PedidoLicencaAtividadeDto> getPedidoLicencaAtividade(
            @PathVariable Long aplicanteId
    ) {
        return ResponseEntity.ok(pedidoLicencaAtividadeService.getByAplicanteId(aplicanteId));
    }

    @PutMapping("/{aplicanteId}/pedidos/cadastro/{pedidoId}")
    ResponseEntity<PedidoInscricaoCadastroDto> updatePedidoInscricaoCadastro(
            @PathVariable Long aplicanteId,
            @PathVariable Long pedidoId,
            @RequestBody PedidoInscricaoCadastro obj
    ) throws BadRequestException {
        return ResponseEntity.ok(aplicanteService.updatePedidoInscricaoCadastro(aplicanteId, pedidoId, obj));
    }

    @PutMapping("/{aplicanteId}/pedidos/atividade/{pedidoId}")
    ResponseEntity<PedidoLicencaAtividadeDto> updatePedidoAtividade(
            @PathVariable Long aplicanteId,
            @PathVariable Long pedidoId,
            @RequestBody PedidoLicencaAtividadeReqsDto obj
    ) throws BadRequestException {
        return ResponseEntity.ok(licencaMapper.toDto(pedidoLicencaAtividadeService.updateByIdAndAplicanteId(pedidoId, aplicanteId, licencaMapper.toEntity(obj))));
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
            return ResponseEntity.ok(faturaService.saveRecibo(faturaId, pedidoId, aplicanteId, username, file));
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
