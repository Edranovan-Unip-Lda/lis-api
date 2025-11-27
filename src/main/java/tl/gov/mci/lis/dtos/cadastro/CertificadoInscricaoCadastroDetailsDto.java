package tl.gov.mci.lis.dtos.cadastro;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.dtos.atividade.ClasseAtividadeDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro}
 */
@Value
public class CertificadoInscricaoCadastroDetailsDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    PedidoInscricaoCadastroDto pedidoInscricaoCadastro;
    @NotNull
    String sociedadeComercial;
    @NotNull
    String numeroRegistoComercial;
    EnderecoDto sede;
    @NotNull
    String atividade;
    @NotNull
    String dataValidade;
    @NotNull
    String dataEmissao;
    @NotNull
    String nomeDiretorGeral;

    /**
     * DTO for {@link tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro}
     */
    @Value
    public static class PedidoInscricaoCadastroDto implements Serializable {
        Long id;
        PedidoStatus status;
        TipoPedidoCadastro tipoPedidoCadastro;
        String nomeEmpresa;
        String empresaNif;
        String empresaGerente;
        String empresaNumeroRegistoComercial;
        String empresaEmail;
        String empresaTelefone;
        String empresaTelemovel;
        EnderecoDto empresaSede;
        String categoria;
        TipoEmpresa tipoEmpresa;
        QuantoAtividade quantoAtividade;
        String nomeEstabelecimento;
        EnderecoDto localEstabelecimento;
        TipoEstabelecimento tipoEstabelecimento;
        CaraterizacaoEstabelecimento caraterizacaoEstabelecimento;
        NivelRisco risco;
        TipoAto ato;
        ClasseAtividadeDto classeAtividade;
        String alteracoes;
        String dataEmissaoCertAnterior;
        String observacao;
        AplicanteDto aplicante;
        Double longitude;
        Double latitude;

        /**
         * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
         */
        @Value
        public static class AplicanteDto implements Serializable {
            Long id;
            AplicanteType tipo;
            Categoria categoria;
            String numero;
            AplicanteStatus estado;
            EmpresaDto empresa;
        }
    }
}