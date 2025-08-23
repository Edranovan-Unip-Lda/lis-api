package tl.gov.mci.lis.controllers.vistoria;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.services.vistoria.PedidoVistoriaService;

@RestController
@RequestMapping("/api/v1/pedidos-vistoria")
@RequiredArgsConstructor
public class PedidoVistoriaController {
    private final PedidoVistoriaService pedidoVistoriaService;

    @PostMapping("/{id}/faturas")
    ResponseEntity<FaturaDto> createFatura(@PathVariable Long id, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoVistoriaService.createFatura(id, obj));
    }

    @PutMapping("/{id}/faturas/{faturaId}")
    ResponseEntity<FaturaDto> updateFatura(@PathVariable Long id, @PathVariable Long faturaId, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoVistoriaService.updateFatura(id, faturaId, obj));
    }
}
