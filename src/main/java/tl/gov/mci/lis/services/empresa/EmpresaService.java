package tl.gov.mci.lis.services.empresa;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
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

    public Empresa create(Empresa obj) throws BadRequestException {
        logger.info("Criando empresa: {}", obj);
        // Register the account first
        obj.getUtilizador().setRole(roleRepository.getReferenceById(3L)); // 3 = empresa
        obj.setUtilizador(userServices.register(obj.getUtilizador()));
        obj.setSede(enderecoService.create(obj.getSede()));
        return empresaRepository.save(obj);
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
        logger.info("Obtendo empresa by id: {}", id);
        return empresaRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
    }

    public Page<EmpresaDto> getPageByPageAndSize(int page, int size) {
        logger.info("Obtendo page: {} and size {}", page, size);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Empresa> empresas = empresaRepository.findAll(paging);
        return empresas.map(empresaMapper::toDto);
    }

    public Aplicante createAplicante(Long empresaId, Aplicante obj) {
        logger.info("Criando aplicante: {}", obj);
        obj.setEmpresa(empresaRepository.getReferenceById(empresaId));
        return aplicanteRepository.save(obj);
    }

}
