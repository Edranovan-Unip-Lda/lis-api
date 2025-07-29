package tl.gov.mci.lis.controllers.cadastro;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoInscricaoCadastroController {
    private final PedidoInscricaoCadastroService pedidoInscricaoCadastroService;

    @PostMapping("/{id}/faturas")
    ResponseEntity<FaturaDto> createFatura(@PathVariable Long id, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoInscricaoCadastroService.createFatura(id, obj));
    }

    @PutMapping("/{id}/faturas/{faturaId}")
    ResponseEntity<FaturaDto> updateFatura(@PathVariable Long id, @PathVariable Long faturaId, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoInscricaoCadastroService.updateFatura(id, faturaId, obj));
    }
}
