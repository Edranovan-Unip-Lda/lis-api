package tl.gov.mci.lis.controllers.aplicante;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.aplicante.AplicantePageDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.services.aplicante.AplicanteService;

@RestController
@RequestMapping("/api/v1/aplicantes")
@RequiredArgsConstructor
public class AplicanteController {
    private final AplicanteService aplicanteService;

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
}
