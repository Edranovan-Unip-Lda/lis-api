package tl.gov.mci.lis.dtos.vistoria;

import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;
import tl.gov.mci.lis.dtos.user.UserDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link tl.gov.mci.lis.models.vistoria.AutoVistoria}
 */
@Value
public class AutoVistoriaDto implements Serializable {
    Long id;
    Instant updatedAt;
    EnderecoDto local;
    String numeroProcesso;
    RequerenteDto requerente;
    Set<ParticipanteDto> membrosEquipaVistoria;
    String nomeAtuante;
    boolean legislacaoUrbanistica;
    String tipoLocal;
    boolean accessoEstrada;
    Double larguraEstrada;
    boolean escoamentoAguas;
    boolean alimentacaoEnergia;
    String tipoEletricidade;
    boolean seperadosSexo;
    boolean lavatoriosComEspelho;
    boolean sanitasAutomaticaAgua;
    boolean comunicacaoVentilacao;
    boolean esgotoAguas;
    boolean paredesPavimentos;
    boolean zonasDestinadas;
    boolean instalacoesFrigorificas;
    boolean sectoresLimpos;
    boolean pisosParedes;
    boolean pisosResistentes;
    boolean paredesInteriores;
    boolean paredes3metros;
    boolean unioesParedes;
    boolean ventilacoesNecessarias;
    boolean iluminacao;
    boolean aguaPotavel;
    boolean distribuicaoAgua;
    boolean redeDistribuicao;
    boolean redeEsgotos;
    boolean maximoHigieneSeguranca;
    boolean equipamentoUtensilios;
    boolean equipamentoPrimeirosSocorros;
    boolean recipientesLixo;
    boolean limpezaDiaria;
    String descreverIrregularidades;
    boolean aptoAberto;
    boolean comDeficiencias;
    String recomendacoes;
    int prazo;
    List<DocumentoDto> documentos;
    UserDto funcionario;

    String legislacaoUrbanisticaDescricao;
    String tipoLocalDescricao;
    String acessoEstradaDescricao;
    String escoamentoAguasDescricao;
    String alimentacaoEnergiaDescricao;
    String tipoEletricidadeDescricao;
    String separadosSexoDescricao;
    String lavatoriosComEspelhoDescricao;
    String sanitasAutomaticaAguaDescricao;
    String comunicacaoVentilacaoDescricao;
    String esgotoAguasDescricao;
    String paredesPavimentosDescricao;
    String zonasDestinadasDescricao;
    String instalacoesFrigorificasDescricao;
    String sectoresLimposDescricao;
    String pisosParedesDescricao;
    String pisosResistentesDescricao;
    String paredesInterioresDescricao;
    String paredes3metrosDescricao;
    String unioesParedesDescricao;
    String ventilacoesNecessariasDescricao;
    String iluminacaoDescricao;
    String aguaPotavelDescricao;
    String distribuicaoAguaDescricao;
    String redeDistribuicaoDescricao;
    String redeEsgotosDescricao;
    String maximoHigieneSegurancaDescricao;
    String equipamentoUtensiliosDescricao;
    String equipamentoPrimeirosSocorrosDescricao;
    String recipientesLixoDescricao;
    String limpezaDiariaDescricao;
}