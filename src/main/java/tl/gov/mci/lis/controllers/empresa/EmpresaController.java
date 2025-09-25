package tl.gov.mci.lis.controllers.empresa;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteReqsDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteRequestDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaRequestDto;
import tl.gov.mci.lis.dtos.mappers.AplicanteMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.services.empresa.EmpresaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {
    private final EmpresaService empresaService;
    private final EmpresaMapper empresaMapper;
    private final AplicanteMapper aplicanteMapper;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EmpresaDto> createEmpresa(
            @Valid @RequestPart("data") EmpresaRequestDto obj,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        return new ResponseEntity<>(
                empresaMapper.toDto(empresaService.create(
                        empresaMapper.toEntity(obj), files
                )), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<EmpresaDto> updateEmpresa(@RequestBody Empresa obj) {
        return new ResponseEntity<>(empresaMapper.toDto(empresaService.update(obj)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<EmpresaDto> getEmpresa(@PathVariable Long id) {
        return new ResponseEntity<>(empresaMapper.toDto(empresaService.getById(id)), HttpStatus.OK);
    }

    @GetMapping("")
    ResponseEntity<Page<EmpresaDto>> getPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return new ResponseEntity<>(empresaService.getPageByPageAndSize(page, size), HttpStatus.OK);
    }

    @PostMapping("/{empresaId}/aplicantes")
    ResponseEntity<AplicanteDto> saveAplicante(@PathVariable Long empresaId, @RequestBody AplicanteReqsDto obj) {
        return new ResponseEntity<>(
                aplicanteMapper.toDto(empresaService.createAplicante(empresaId, aplicanteMapper.toEntity(obj))),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{empresaId}/aplicantes/{aplicanteId}")
    ResponseEntity<AplicanteDto> submitAplicante(@PathVariable Long empresaId, @PathVariable Long aplicanteId, @Valid @RequestBody AplicanteRequestDto obj) {
        return new ResponseEntity<>(
                aplicanteMapper.toDto(empresaService.submitAplicante(empresaId, aplicanteId, obj)),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/{empresaId}/aplicantes")
    ResponseEntity<Page<AplicanteDto>> getAplicantePage(
            @PathVariable Long empresaId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(empresaService.getAplicantePage(empresaId, page, size));
    }

    @GetMapping("/{empresaId}/aplicantes/{aplicanteId}")
    ResponseEntity<AplicanteDto> getAplicante(@PathVariable Long empresaId, @PathVariable Long aplicanteId) {
        return new ResponseEntity<>(empresaService.getAplicanteById(empresaId, aplicanteId), HttpStatus.OK);
    }

    @DeleteMapping("/{empresaId}/aplicantes/{aplicanteId}")
    ResponseEntity<?> deleteAplicante(@PathVariable Long empresaId, @PathVariable Long aplicanteId) {
        return new ResponseEntity<>(empresaService.deleteAplicante(empresaId, aplicanteId), HttpStatus.OK);
    }

    @GetMapping("/{empresaId}/certificados")
    ResponseEntity<Page<?>> getPageCertificates(
            @PathVariable Long empresaId,
            @RequestParam(value = "categoria") Categoria categoria,
            @RequestParam(value = "type") AplicanteType aplicanteType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(empresaService.getCertificatesPage(empresaId, categoria, aplicanteType, page, size));
    }
}
