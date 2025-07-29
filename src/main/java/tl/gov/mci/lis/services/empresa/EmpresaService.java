package tl.gov.mci.lis.services.empresa;

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
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.authorization.AuthorizationService;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;
import tl.gov.mci.lis.services.endereco.EnderecoService;
import tl.gov.mci.lis.services.user.UserServices;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private static final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final EmpresaRepository empresaRepository;
    private final UserServices userServices;
    private final AplicanteRepository aplicanteRepository;
    private final EnderecoService enderecoService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmpresaMapper empresaMapper;
    private final AuthorizationService authorizationService;
    private final AplicanteService aplicanteService;
    private final PedidoInscricaoCadastroService pedidoInscricaoCadastroService;
    private final EntityManager entityManager;

    @Transactional
    public Empresa create(Empresa obj) throws BadRequestException {
        logger.info("Criando empresa: {}", obj);
        // Register the account first
        obj.getUtilizador().setRole(roleRepository.getReferenceById(3L)); // 3 = empresa
        obj.setUtilizador(userServices.register(obj.getUtilizador()));
        obj.setSede(enderecoService.create(obj.getSede()));
        obj.getAcionistas().forEach(obj::addAcionista);
        entityManager.persist(obj);
        return obj;
    }

    public Empresa update(Empresa obj) throws BadRequestException {
        logger.info("Atualizando empresa: {}", obj);
        Empresa empresa = empresaRepository.findById(obj.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));

        empresa.setSede(enderecoService.update(obj.getSede()));

        if (obj.getUtilizador().getId() == null) {
            throw new BadRequestException("ID do utilizador é obrigatório");
        }

        User utilizador = userRepository.findById(obj.getUtilizador().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
        utilizador.setId(obj.getUtilizador().getId());
        utilizador.setFirstName(obj.getUtilizador().getFirstName());
        utilizador.setLastName(obj.getUtilizador().getLastName());
        utilizador.setEmail(obj.getUtilizador().getEmail());
        empresa.setUtilizador(utilizador);

        empresa.setNome(obj.getNome());
        empresa.setNif(obj.getNif());
        empresa.setGerente(obj.getGerente());
        empresa.setNumeroRegistoComercial(obj.getNumeroRegistoComercial());
        empresa.setTelefone(obj.getTelefone());
        empresa.setTelemovel(obj.getTelemovel());

        return empresaRepository.save(empresa);
    }

    public Empresa getById(Long id) {
        logger.info("Obtendo empresa pelo id: {}", id);
        return empresaRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
    }

    public Page<EmpresaDto> getPageByPageAndSize(int page, int size) {
        logger.info("Obtendo page: {} e size {}", page, size);

        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Empresa> empresas = empresaRepository.findAll(paging);
        return empresas.map(empresaMapper::toDto);
    }

    @Transactional
    public Aplicante createAplicante(Long empresaId, Aplicante obj) {
        logger.info("Criando aplicante: {}", obj);

        authorizationService.assertUserOwnsEmpresa(empresaId);
        obj.setEmpresa(empresaRepository.getReferenceById(empresaId));
        obj.setNumero(aplicanteService.generateAplicanteNumber(obj.getCategoria(), empresaId));
        obj.setEstado(AplicanteStatus.EM_CURSO);
        entityManager.persist(obj);
        return obj;
    }

    public Page<AplicanteDto> getAplicantePage(Long empresaId, int page, int size) {
        logger.info("Obtendo aplicante page pelo empresa id: {}", empresaId);

        authorizationService.assertUserOwnsEmpresa(empresaId);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        return aplicanteRepository.getPageByEmpresaId(empresaId, paging);
    }

    public AplicanteDto getAplicanteById(Long empresaId, Long aplicanteId) {
        logger.info("Obtendo aplicante by id: {}", aplicanteId);

        authorizationService.assertUserOwnsEmpresa(empresaId);
        return aplicanteRepository.getFromIdAndEmpresaId(aplicanteId, empresaId)
                .map(aplicanteDto -> {
                    aplicanteDto.setEmpresaDto(empresaMapper.toDto(getById(empresaId)));
                    aplicanteDto.setPedidoInscricaoCadastroDto(pedidoInscricaoCadastroService.getByAplicanteId(aplicanteId));
                    return aplicanteDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));
    }

    public boolean deleteAplicante(Long empresaId, Long aplicanteId) {
        logger.info("Deletando aplicante: {}", aplicanteId);

        Aplicante aplicante = aplicanteRepository.findById(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante not found"));

        authorizationService.assertUserOwnsEmpresa(aplicante.getEmpresa().getId());
        int deleted = aplicanteRepository.deleteByIdAndEmpresaId(aplicanteId, empresaId);

        if (deleted > 0) {
            logger.info("Aplicante {} excluído com sucesso", aplicanteId);
            return true;
        } else {
            logger.warn("Nenhuma aplicante excluída para id {} e empresa {}", aplicanteId, empresaId);
            return false;
        }
    }
}
