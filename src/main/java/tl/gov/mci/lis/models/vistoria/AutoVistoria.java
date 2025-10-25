package tl.gov.mci.lis.models.vistoria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.user.User;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "lis_atividade_auto_vistoria")
@Getter
@Setter
public class AutoVistoria extends EntityDB {
    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private String numeroProcesso;

    @OneToOne
    @JoinColumn(name = "local_id", referencedColumnName = "id")
    private Endereco local;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "requerente_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "autoVistoria", allowSetters = true)
    private Requerente requerente;

    @OneToMany(mappedBy = "autoVistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "autoVistoria", allowSetters = true)
    private Set<Participante> membrosEquipaVistoria;

    private String nomeAtuante;
    private boolean legislacaoUrbanistica;
    private String tipoLocal;
    private boolean accessoEstrada;
    private Double larguraEstrada;
    private boolean escoamentoAguas;
    private boolean alimentacaoEnergia;
    private String tipoEletricidade;
    private boolean seperadosSexo;
    private boolean lavatoriosComEspelho;
    private boolean sanitasAutomaticaAgua;
    private boolean comunicacaoVentilacao;
    private boolean esgotoAguas;
    private boolean paredesPavimentos;
    private boolean zonasDestinadas;
    private boolean instalacoesFrigorificas;
    private boolean sectoresLimpos;
    private boolean pisosParedes;
    private boolean pisosResistentes;
    private boolean paredesInteriores;
    private boolean paredes3metros;
    private boolean unioesParedes;
    private boolean ventilacoesNecessarias;
    private boolean iluminacao;
    private boolean aguaPotavel;
    private boolean distribuicaoAgua;
    private boolean redeDistribuicao;
    private boolean redeEsgotos;
    private boolean maximoHigieneSeguranca;
    private boolean equipamentoUtensilios;
    private boolean equipamentoPrimeirosSocorros;
    private boolean recipientesLixo;
    private boolean limpezaDiaria;

    @Column(columnDefinition = "TEXT")
    private String descreverIrregularidades;
    private boolean aptoAberto;
    private boolean comDeficiencias;

    @Column(columnDefinition = "TEXT")
    private String recomendacoes;
    private int prazo;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_vistoria_id", referencedColumnName = "id")
    private PedidoVistoria pedidoVistoria;

    @OneToMany(mappedBy = "autoVistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "autoVistoria", allowSetters = true)
    private List<Documento> documentos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User funcionario;
}
