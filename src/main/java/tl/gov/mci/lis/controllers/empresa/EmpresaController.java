package tl.gov.mci.lis.controllers.empresa;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.services.empresa.EmpresaService;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {
    private final EmpresaService empresaService;
    private final EmpresaMapper empresaMapper;

    @PostMapping("")
    ResponseEntity<EmpresaDto> createEmpresa(@RequestBody Empresa obj) throws BadRequestException {
        return new ResponseEntity<>(empresaMapper.toDto(empresaService.create(obj)), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    ResponseEntity<EmpresaDto> updateEmpresa(@RequestBody Empresa obj) throws BadRequestException {
        return new ResponseEntity<>(empresaMapper.toDto(empresaService.update(obj)), HttpStatus.OK);
    }

    @GetMapping("{id}")
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
}
