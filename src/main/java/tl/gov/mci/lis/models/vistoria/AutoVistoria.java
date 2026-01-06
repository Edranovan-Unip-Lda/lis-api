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

    private Boolean legislacaoUrbanistica;
    @Column(columnDefinition = "TEXT")
    private String legislacaoUrbanisticaDescricao;
    private String tipoLocal;

    private Boolean acessoEstrada;
    @Column(columnDefinition = "TEXT")
    private String acessoEstradaDescricao;
    private Double larguraEstrada;

    private Boolean escoamentoAguas;
    @Column(columnDefinition = "TEXT")
    private String escoamentoAguasDescricao;

    private Boolean alimentacaoEnergia;
    @Column(columnDefinition = "TEXT")
    private String alimentacaoEnergiaDescricao;
    private String tipoEletricidade;

    private Boolean separadosSexo;
    @Column(columnDefinition = "TEXT")
    private String separadosSexoDescricao;

    private Boolean lavatoriosComEspelho;
    @Column(columnDefinition = "TEXT")
    private String lavatoriosComEspelhoDescricao;

    private Boolean sanitasAutomaticaAgua;
    @Column(columnDefinition = "TEXT")
    private String sanitasAutomaticaAguaDescricao;

    private Boolean comunicacaoVentilacao;
    @Column(columnDefinition = "TEXT")
    private String comunicacaoVentilacaoDescricao;

    private Boolean esgotoAguas;
    @Column(columnDefinition = "TEXT")
    private String esgotoAguasDescricao;

    private Boolean paredesPavimentos;
    @Column(columnDefinition = "TEXT")
    private String paredesPavimentosDescricao;

    private Boolean zonasDestinadas;
    @Column(columnDefinition = "TEXT")
    private String zonasDestinadasDescricao;

    private Boolean instalacoesFrigorificas;
    @Column(columnDefinition = "TEXT")
    private String instalacoesFrigorificasDescricao;

    private Boolean sectoresLimpos;
    @Column(columnDefinition = "TEXT")
    private String sectoresLimposDescricao;

    private Boolean pisosParedes;
    @Column(columnDefinition = "TEXT")
    private String pisosParedesDescricao;

    private Boolean pisosResistentes;
    @Column(columnDefinition = "TEXT")
    private String pisosResistentesDescricao;

    private Boolean paredesInteriores;
    @Column(columnDefinition = "TEXT")
    private String paredesInterioresDescricao;

    private Boolean paredes3metros;
    @Column(columnDefinition = "TEXT")
    private String paredes3metrosDescricao;

    private Boolean unioesParedes;
    @Column(columnDefinition = "TEXT")
    private String unioesParedesDescricao;

    private Boolean ventilacoesNecessarias;
    @Column(columnDefinition = "TEXT")
    private String ventilacoesNecessariasDescricao;

    private Boolean iluminacao;
    @Column(columnDefinition = "TEXT")
    private String iluminacaoDescricao;

    private Boolean aguaPotavel;
    @Column(columnDefinition = "TEXT")
    private String aguaPotavelDescricao;

    private Boolean distribuicaoAgua;
    @Column(columnDefinition = "TEXT")
    private String distribuicaoAguaDescricao;

    private Boolean redeDistribuicao;
    @Column(columnDefinition = "TEXT")
    private String redeDistribuicaoDescricao;

    private Boolean redeEsgotos;
    @Column(columnDefinition = "TEXT")
    private String redeEsgotosDescricao;

    private Boolean maximoHigieneSeguranca;
    @Column(columnDefinition = "TEXT")
    private String maximoHigieneSegurancaDescricao;

    private Boolean equipamentoUtensilios;
    @Column(columnDefinition = "TEXT")
    private String equipamentoUtensiliosDescricao;

    private Boolean equipamentoPrimeirosSocorros;
    @Column(columnDefinition = "TEXT")
    private String equipamentoPrimeirosSocorrosDescricao;

    private Boolean recipientesLixo;
    @Column(columnDefinition = "TEXT")
    private String recipientesLixoDescricao;

    private Boolean limpezaDiaria;
    @Column(columnDefinition = "TEXT")
    private String limpezaDiariaDescricao;

    @Column(columnDefinition = "TEXT")
    private String descreverIrregularidades;
    private Boolean aptoAberto;
    private Boolean comDeficiencias;

    @Column(columnDefinition = "TEXT")
    private String recomendacoes;
    private int prazo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_vistoria_id", referencedColumnName = "id")
    private PedidoVistoria pedidoVistoria;

    @OneToMany(mappedBy = "autoVistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "autoVistoria", allowSetters = true)
    private List<Documento> documentos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User funcionario;
}
