package tl.gov.mci.lis.controllers.aplicante;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.AplicantePageDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.services.aplicante.AplicanteService;

@RestController
@RequestMapping("/api/v1/aplicantes")
@RequiredArgsConstructor
public class AplicanteController {
    private final AplicanteService aplicanteService;

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
}
