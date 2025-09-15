package tl.gov.mci.lis.controllers.empresa;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteReqsDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteRequestDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaRequestDto;
import tl.gov.mci.lis.dtos.mappers.AplicanteMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.services.empresa.EmpresaService;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {
    private final EmpresaService empresaService;
    private final EmpresaMapper empresaMapper;
    private final AplicanteMapper aplicanteMapper;

    @PostMapping("")
    ResponseEntity<EmpresaDto> createEmpresa(@Valid @RequestBody EmpresaRequestDto obj) {
        return new ResponseEntity<>(
                empresaMapper.toDto(empresaService.create(
                        empresaMapper.toEntity(obj)
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
}
