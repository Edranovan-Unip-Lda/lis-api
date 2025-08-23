package tl.gov.mci.lis.controllers.atividade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.services.atividade.PedidoLicencaAtividadeService;

@RestController
@RequestMapping("/api/v1/pedidos-atividade")
@RequiredArgsConstructor
public class PedidoLicencaAtividadeController {
    private final PedidoLicencaAtividadeService pedidoLicencaAtividadeService;

    @PostMapping("/{id}/faturas")
    ResponseEntity<FaturaDto> createFatura(@PathVariable Long id, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoLicencaAtividadeService.createFatura(id, obj));
    }

    @PutMapping("/{id}/faturas/{faturaId}")
    ResponseEntity<FaturaDto> updateFatura(@PathVariable Long id, @PathVariable Long faturaId, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoLicencaAtividadeService.updateFatura(id, faturaId, obj));
    }
}
