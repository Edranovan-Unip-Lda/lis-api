package tl.gov.mci.lis.services.vistoria;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.mappers.VistoriaMapper;
import tl.gov.mci.lis.dtos.vistoria.AutoVistoriaDto;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.vistoria.AutoVistoria;
import tl.gov.mci.lis.models.vistoria.Requerente;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.ClasseAtividadeRepository;
import tl.gov.mci.lis.repositories.endereco.PostoAdministrativoRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
import tl.gov.mci.lis.repositories.vistoria.AutoVistoriaRepository;
import tl.gov.mci.lis.services.aplicante.AssignmentService;
import tl.gov.mci.lis.services.endereco.EnderecoService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AutoVistoriaService {
    private static final Logger logger = LoggerFactory.getLogger(AutoVistoriaService.class);
    private final AplicanteRepository aplicanteRepository;
    private final AssignmentService assignmentService;
    private final EntityManager entityManager;
    private final AutoVistoriaRepository autoVistoriaRepository;
    private final VistoriaMapper vistoriaMapper;
    private final UserRepository userRepository;
    private final EnderecoService enderecoService;
    private final ClasseAtividadeRepository classAtividadeRepo;
    private final PostoAdministrativoRepository postoAdministrativoRepository;

    @Transactional
    public AutoVistoria create(Long aplicanteId, AutoVistoria obj) {
        logger.info("Criando auto vistoria com aplicanteId: {}", aplicanteId);
        if (!aplicanteRepository.existsById(aplicanteId)) {
            logger.error("Aplicante nao encontrado");
            throw new ResourceNotFoundException("Aplicante nao encontrado");
        }

        obj.setLocal(
                enderecoService.create(obj.getLocal())
        );

        Requerente requerente = obj.getRequerente();

        if (requerente.getSede() != null && Objects.isNull(requerente.getSede().getId())) {
            requerente.setSede(enderecoService.create(requerente.getSede()));
        }

        if (requerente.getResidencia() != null && Objects.isNull(requerente.getResidencia().getId())) {
            requerente.setResidencia(enderecoService.create(requerente.getResidencia()));
        }

        // attach master data by id instead of new()
        if (requerente.getClasseAtividade() != null && requerente.getClasseAtividade().getId() != null) {
            requerente.setClasseAtividade(classAtividadeRepo.getReferenceById(requerente.getClasseAtividade().getId()));
        }
        if (requerente.getPostoAdministrativo() != null && requerente.getPostoAdministrativo().getId() != null) {
            requerente.setPostoAdministrativo(postoAdministrativoRepository.getReferenceById(requerente.getPostoAdministrativo().getId()));
        }

        obj.setAplicante(aplicanteRepository.getReferenceById(aplicanteId));
        obj.setFuncionario(userRepository.getReferenceById(obj.getFuncionario().getId()));

        entityManager.persist(obj);

        // atualizar estado do aplicante sem carregar o objeto inteiro:
        aplicanteRepository.getReferenceById(aplicanteId)
                .setEstado(AplicanteStatus.REVISAO); // dirty checking

        // close assignment
        assignmentService.closeAssignment(aplicanteId);
        return obj;
    }

    @Transactional(readOnly = true)
    public AutoVistoriaDto getById(Long id) {
        logger.info("Obtendo auto vistoria com id: {}", id);

        return autoVistoriaRepository.findById(id)
                .map(vistoriaMapper::toDto)
                .orElseThrow(() -> {
                    logger.error("Auto Vistoria nao encontrada");
                    return new ResourceNotFoundException("Auto Vistoria nao encontrada");
                });
    }

    @Transactional(readOnly = true)
    public AutoVistoria getByAplicanteId(Long aplicanteId) {
        logger.info("Obtendo auto vistoria com aplicante id: {}", aplicanteId);

        return autoVistoriaRepository.findByAplicante_id(aplicanteId)
                .orElse(null);
    }

}
