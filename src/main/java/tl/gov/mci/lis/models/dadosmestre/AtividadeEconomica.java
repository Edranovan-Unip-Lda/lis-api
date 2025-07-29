package tl.gov.mci.lis.models.dadosmestre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.TipoAtividadeEconomica;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.pagamento.Fatura;

import java.util.Set;

@Entity
@Table(name = "lis_dm_atividade_economica")
@Getter
@Setter
public class AtividadeEconomica extends EntityDB {
    private String codigo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    private Categoria tipo;

    @Enumerated(EnumType.STRING)
    private NivelRisco tipoRisco;

    @Enumerated(EnumType.STRING)
    private TipoAtividadeEconomica tipoAtividadeEconomica;

    @OneToMany(mappedBy = "tipoAtividade")
    @JsonIgnoreProperties(value = "tipoAtividade", allowSetters = true)
    private Set<PedidoInscricaoCadastro> listaPedidoInscricaoCadastro;

    @OneToMany(mappedBy = "atividadePrincipal")
    @JsonIgnoreProperties(value = "atividadePrincipal", allowSetters = true)
    private Set<PedidoInscricaoCadastro> listaPedidoInscricaoCadastroAtividadePrincipal;

    @OneToMany(mappedBy = "atividadeDeclarada")
    @JsonIgnoreProperties(value = "atividadeDeclarada", allowSetters = true)
    private Set<Fatura> faturas;

}
