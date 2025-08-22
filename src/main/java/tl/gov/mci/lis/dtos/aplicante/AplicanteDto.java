package tl.gov.mci.lis.dtos.aplicante;

import lombok.Data;
import tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.licenca.PedidoLicencaAtividadeDto;
import tl.gov.mci.lis.enums.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
 */
@Data
public class AplicanteDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    AplicanteType tipo;
    Categoria categoria;
    String numero;
    AplicanteStatus estado;
    PedidoStatus pedidoStatus;
    FaturaStatus faturaStatus;
    EmpresaDto empresa;
    PedidoInscricaoCadastroDto pedidoInscricaoCadastro;
    PedidoLicencaAtividadeDto pedidoLicencaAtividade;
    List<HistoricoEstadoAplicanteDto> historicoStatus;
    CertificadoInscricaoCadastroDto certificadoInscricaoCadastro;

    public AplicanteDto() {
    }

    public AplicanteDto(Long id, Boolean isDeleted, Instant createdAt, Instant updatedAt, String createdBy, String updatedBy, AplicanteType tipo, Categoria categoria, String numero, AplicanteStatus estado, PedidoStatus pedidoStatus, FaturaStatus faturaStatus) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.tipo = tipo;
        this.categoria = categoria;
        this.numero = numero;
        this.estado = estado;
        this.pedidoStatus = pedidoStatus;
        this.faturaStatus = faturaStatus;
    }

    public AplicanteDto(Long id, Boolean isDeleted, Instant createdAt, Instant updatedAt, String createdBy, String updatedBy, AplicanteType tipo, Categoria categoria, String numero, AplicanteStatus estado, PedidoStatus pedidoStatus, FaturaStatus faturaStatus, Long empresaId, Long pedidoInscricaoCadastroId) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.tipo = tipo;
        this.categoria = categoria;
        this.numero = numero;
        this.estado = estado;
        this.pedidoStatus = pedidoStatus;
        this.faturaStatus = faturaStatus;

        this.empresa = new EmpresaDto(empresaId);
        this.pedidoInscricaoCadastro = new PedidoInscricaoCadastroDto(pedidoInscricaoCadastroId);
    }
}