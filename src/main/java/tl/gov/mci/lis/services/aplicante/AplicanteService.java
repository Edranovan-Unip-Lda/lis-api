package tl.gov.mci.lis.services.aplicante;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.mappers.CertificadoMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.dtos.mappers.PedidoInscricaoCadastroMapper;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.AplicanteNumber;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.repositories.aplicante.AplicanteNumberRepository;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.aplicante.HistoricoEstadoAplicanteRepository;
import tl.gov.mci.lis.repositories.cadastro.CertificadoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.cadastro.PedidoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.dadosmestre.DirecaoRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.ClasseAtividadeRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.GrupoAtividadeRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;
import tl.gov.mci.lis.services.endereco.EnderecoService;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;


@Service
@RequiredArgsConstructor
public class AplicanteService {
    private static final Logger logger = LoggerFactory.getLogger(AplicanteService.class);
    private static final String PREFIX = "MCI";
    private final AplicanteRepository aplicanteRepository;
    private final AplicanteNumberRepository repository;
    private final PedidoInscricaoCadastroRepository pedidoInscricaoCadastroRepository;
    private final PedidoInscricaoCadastroMapper pedidoInscricaoCadastroMapper;
    private final ClasseAtividadeRepository classeAtividadeRepository;
    private final EnderecoService enderecoService;
    private final EmpresaRepository empresaRepository;
    private final EntityManager entityManager;
    private final DirecaoRepository direcaoRepository;
    private final EmpresaMapper empresaMapper;
    private final PedidoInscricaoCadastroService pedidoInscricaoCadastroService;
    private final HistoricoEstadoAplicanteRepository historicoEstadoAplicanteRepository;
    private final CertificadoInscricaoCadastroRepository certificadoInscricaoCadastroRepository;
    private final CertificadoMapper certificadoMapper;
    private final GrupoAtividadeRepository grupoAtividadeRepository;


    public Page<AplicanteDto> getPage(int page, int size) {
        logger.info("Getting page: {} and size {}", page, size);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        return aplicanteRepository.getPageApprovedAplicante(AplicanteStatus.APROVADO, paging);
    }

    public AplicanteDto getById(Long id) {
        logger.info("Obtendo aplicante pelo id: {}", id);
        AplicanteDto aplicanteDto = aplicanteRepository.getFromId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));

        // Enrich Empresa
        if (aplicanteDto.getEmpresa() != null && aplicanteDto.getEmpresa().getId() != null) {
            empresaRepository.findById(aplicanteDto.getEmpresa().getId())
                    .map(empresaMapper::toDto)
                    .ifPresent(aplicanteDto::setEmpresa);
        }

        // Enrich PedidoInscricaoCadastro
        if (aplicanteDto.getPedidoInscricaoCadastro() != null && aplicanteDto.getPedidoInscricaoCadastro().getId() != null) {
            PedidoInscricaoCadastroDto pedidoDto = pedidoInscricaoCadastroService
                    .getByAplicanteId(aplicanteDto.getId());
            aplicanteDto.setPedidoInscricaoCadastro(pedidoDto);
        }

        // Enrich Certificado Cadastro
        certificadoInscricaoCadastroRepository.findByAplicante_Id(aplicanteDto.getId())
                .map(certificadoMapper::toDto)
                .ifPresent(aplicanteDto::setCertificadoInscricaoCadastro);

        aplicanteDto.setHistoricoStatus(
                historicoEstadoAplicanteRepository.findAllByAplicante_Id(id)
        );
        return aplicanteDto;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized String generateAplicanteNumber(Categoria categoriaCode, Long empresaId) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        // Count applications for this department, month, and year
        int count = repository.countByCategoriaCodeAndMonthAndYear(categoriaCode, month, year);
        int serial = count + 1; // Start from 1
        String formattedId = String.format("%04d", serial);
        String nif = empresaRepository.getFromId(empresaId).getNif();

        // Format full application code
        String finalCode = String.format("%s/%s/%02d/%d/%s", PREFIX, categoriaCode, month, year, nif + serial);

        // Save new entry
        AplicanteNumber record = new AplicanteNumber();
        record.setCategoriaCode(categoriaCode);
        record.setMonth(month);
        record.setYear(year);
        record.setFormattedCode(finalCode);

        entityManager.persist(record);

        return finalCode;
    }

    /**
     * Create a new PedidoInscricaoCadastro
     *
     * @param aplicanteId aplicante id
     * @param obj         PedidoInscricaoCadastro
     * @return PedidoInscricaoCadastro dto
     */
    @Transactional
    public PedidoInscricaoCadastroDto createPedidoInscricaoCadastro(Long aplicanteId, PedidoInscricaoCadastro obj) throws BadRequestException {
        logger.info("Criando PedidoInscricaoCadastro pelo Aplicante id: {} e PedidoInscricaoCadastro: {}", aplicanteId, obj);

        // 1) Carregar Aplicante já com Empresa+Sede (1 consulta)
        Aplicante aplicante = aplicanteRepository
                .findByIdWithEmpresaAndEmpresa_Sede(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante não encontrado"));

        Empresa empresa = aplicante.getEmpresa();
        if (empresa == null || empresa.getSede() == null) {
            throw new BadRequestException("Empresa/Sede do aplicante não configurada");
        }

        Endereco empresaSede = new Endereco();
        empresaSede.setLocal(empresa.getSede().getLocal());
        empresaSede.setAldeia(empresa.getSede().getAldeia());

        obj.setNomeEmpresa(empresa.getNome());
        obj.setEmpresaNif(empresa.getNif());
        obj.setEmpresaNumeroRegistoComercial(empresa.getNumeroRegistoComercial());
        obj.setEmpresaTelefone(empresa.getTelefone());
        obj.setEmpresaTelemovel(empresa.getTelemovel());
        obj.setEmpresaGerente(empresa.getGerente());
        obj.setEmpresaEmail(empresa.getUtilizador().getEmail());

        obj.setAplicante(aplicante);
        obj.setEmpresaSede(empresaSede);
        obj.setLocalEstabelecimento(enderecoService.create(obj.getLocalEstabelecimento()));
        obj.setClasseAtividade(classeAtividadeRepository.getReferenceById(obj.getClasseAtividade().getId()));
        obj.setStatus(PedidoStatus.SUBMETIDO);
        entityManager.persist(obj);
        return pedidoInscricaoCadastroMapper.toDto(obj);
    }

    @Transactional
    public PedidoInscricaoCadastroDto updatePedidoInscricaoCadastro(
            Long aplicanteId, Long pedidoId, PedidoInscricaoCadastro incoming) throws BadRequestException {

        logger.info("Atualizando PedidoInscricaoCadastro: pedidoId={}, payload={}", pedidoId, incoming);

        // 1) Load managed entity once (throws if not found / not owned by aplicante)
        PedidoInscricaoCadastro entity = pedidoInscricaoCadastroRepository
                .findByIdAndAplicante_Id(pedidoId, aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de inscricao nao encontrado"));

        // 2) Endereços (evitar chamadas desnecessárias)
        if (incoming.getEmpresaSede() != null) {
            entity.setEmpresaSede(enderecoService.update(incoming.getEmpresaSede()));
        }
        if (incoming.getLocalEstabelecimento() != null) {
            entity.setLocalEstabelecimento(enderecoService.update(incoming.getLocalEstabelecimento()));
        }

        // 3) Classe de atividade via referência (evita select quando só precisamos da FK)
        if (incoming.getClasseAtividade() != null && incoming.getClasseAtividade().getId() != null) {
            Long newId = incoming.getClasseAtividade().getId();
            if (entity.getClasseAtividade() == null || !newId.equals(entity.getClasseAtividade().getId())) {
                entity.setClasseAtividade(classeAtividadeRepository.getReferenceById(newId));
            }
        }

        // 4) Copiar campos simples apenas quando mudam (menos “dirty” = menos "UPDATEs")
        setIfChanged(entity::setTipoPedidoCadastro, entity.getTipoPedidoCadastro(), incoming.getTipoPedidoCadastro());
        setIfChanged(entity::setNomeEmpresa, entity.getNomeEmpresa(), incoming.getNomeEmpresa());
        setIfChanged(entity::setEmpresaNif, entity.getEmpresaNif(), incoming.getEmpresaNif());
        setIfChanged(entity::setEmpresaNumeroRegistoComercial, entity.getEmpresaNumeroRegistoComercial(), incoming.getEmpresaNumeroRegistoComercial());
        setIfChanged(entity::setEmpresaTelefone, entity.getEmpresaTelefone(), incoming.getEmpresaTelefone());
        setIfChanged(entity::setEmpresaTelemovel, entity.getEmpresaTelemovel(), incoming.getEmpresaTelemovel());
        setIfChanged(entity::setEmpresaGerente, entity.getEmpresaGerente(), incoming.getEmpresaGerente());
        setIfChanged(entity::setNomeEstabelecimento, entity.getNomeEstabelecimento(), incoming.getNomeEstabelecimento());
        setIfChanged(entity::setTipoEstabelecimento, entity.getTipoEstabelecimento(), incoming.getTipoEstabelecimento());
        setIfChanged(entity::setCaraterizacaoEstabelecimento, entity.getCaraterizacaoEstabelecimento(), incoming.getCaraterizacaoEstabelecimento());
        setIfChanged(entity::setRisco, entity.getRisco(), incoming.getRisco());
        setIfChanged(entity::setAto, entity.getAto(), incoming.getAto());
        setIfChanged(entity::setTipoEmpresa, entity.getTipoEmpresa(), incoming.getTipoEmpresa());
        setIfChanged(entity::setQuantoAtividade, entity.getQuantoAtividade(), incoming.getQuantoAtividade());
        setIfChanged(entity::setAlteracoes, entity.getAlteracoes(), incoming.getAlteracoes());
        setIfChanged(entity::setDataEmissaoCertAnterior, entity.getDataEmissaoCertAnterior(), incoming.getDataEmissaoCertAnterior());
        setIfChanged(entity::setObservacao, entity.getObservacao(), incoming.getObservacao());

        // 5) Nada de save(): a entidade está gerenciada; flush ocorre no commit (menos I/O)
        return pedidoInscricaoCadastroMapper.toDto(entity);
    }

    @Transactional
    public PedidoLicencaAtividade createPedidoLicencaAtividade(Long aplicanteId, PedidoLicencaAtividade reqsObj) {
        logger.info("Criando PedidoLicencaAtividade pelo Aplicante id: {} e PedidoLicencaAtividade: {}", aplicanteId, reqsObj);

        reqsObj.setAplicante(aplicanteRepository.getReferenceById(aplicanteId));
        reqsObj.setTipoAtividade(grupoAtividadeRepository.getReferenceById(reqsObj.getTipoAtividade().getId()));
        entityManager.persist(reqsObj);
        return reqsObj;
    }

    @Transactional
    public void atribuirDirecao(Long aplicanteId, Long direcaoId) {
        Direcao direcao = direcaoRepository.findById(direcaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Direção não encontrada"));

        Aplicante aplicante = aplicanteRepository.findById(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante não encontrado"));

        aplicante.setDirecaoAtribuida(direcao);
        entityManager.merge(aplicante);
    }

    /**
     * Define valor apenas se mudou (lida com nulls).
     */
    private static <T> void setIfChanged(Consumer<T> setter, T current, T next) {
        if (!Objects.equals(current, next)) {
            setter.accept(next);
        }
    }

}
