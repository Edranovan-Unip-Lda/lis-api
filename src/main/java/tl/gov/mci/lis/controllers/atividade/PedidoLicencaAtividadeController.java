package tl.gov.mci.lis.controllers.atividade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.mappers.VistoriaMapper;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaDto;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaReqDto;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.services.atividade.PedidoLicencaAtividadeService;
import tl.gov.mci.lis.services.vistoria.PedidoVistoriaService;

@RestController
@RequestMapping("/api/v1/pedidos-atividade")
@RequiredArgsConstructor
public class PedidoLicencaAtividadeController {
    private final PedidoLicencaAtividadeService pedidoLicencaAtividadeService;
    private final VistoriaMapper vistoriaMapper;
    private final PedidoVistoriaService pedidoVistoriaService;

    @PostMapping("/{id}/faturas")
    ResponseEntity<FaturaDto> createFatura(@PathVariable Long id, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoLicencaAtividadeService.createFatura(id, obj));
    }

    @PutMapping("/{id}/faturas/{faturaId}")
    ResponseEntity<FaturaDto> updateFatura(@PathVariable Long id, @PathVariable Long faturaId, @RequestBody Fatura obj) {
        return ResponseEntity.ok(pedidoLicencaAtividadeService.updateFatura(id, faturaId, obj));
    }

    @PostMapping("/{id}/pedidos-vistoria")
    ResponseEntity<PedidoVistoriaDto> createPedidoVistoria(
            @PathVariable Long id,
            @RequestBody PedidoVistoriaReqDto incomingObj
    ) {
        return new ResponseEntity<>(
                vistoriaMapper.toDto(pedidoVistoriaService.create(id, vistoriaMapper.toEntity(incomingObj))),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}/pedidos-vistoria/{pedidoId}")
    ResponseEntity<PedidoVistoriaDto> updatePedidoVistoria(
            @PathVariable Long id,
            @PathVariable Long pedidoId,
            @RequestBody PedidoVistoriaReqDto incomingObj
    ) {
        return ResponseEntity.ok(vistoriaMapper.toDto(pedidoVistoriaService.update(pedidoId, id, vistoriaMapper.toEntity(incomingObj))));
    }
}
