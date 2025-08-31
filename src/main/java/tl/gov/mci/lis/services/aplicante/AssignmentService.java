package tl.gov.mci.lis.services.aplicante;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.EstadoTarefa;
import tl.gov.mci.lis.enums.Role;
import tl.gov.mci.lis.exceptions.AlreadyExistException;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.AplicanteAssignment;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.aplicante.AplicanteAssignmentRepository;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);
    private final AplicanteAssignmentRepository assignmentRepository;
    private final AplicanteRepository aplicanteRepo;
    private final UserRepository staffRepo;
    private final EntityManager entityManager;

    @Transactional
    public AplicanteAssignment assign(Long aplicanteId, String managerUsername, String assigneeStaffUsername, String notes) {
        logger.info("Atribuindo aplicante id: {} para o funcionario: {} pelo Diretor: {}", aplicanteId, assigneeStaffUsername, managerUsername);

        Aplicante aplicante = aplicanteRepo.findById(aplicanteId)
                .orElseThrow(() -> {
                    logger.error("Aplicante nao encontrado");
                    return new ResourceNotFoundException("Aplicante nao encontrado");
                });

        User assignee = staffRepo.findUserByUsername(assigneeStaffUsername)
                .orElseThrow(() -> {
                    logger.error("Funcionario nao encontrado");
                    return new ResourceNotFoundException("Aplicante nao encontrado");
                });

        User manager = staffRepo.findUserByUsername(managerUsername)
                .orElseThrow(() -> {
                    logger.error("Diretor nao encontrado");
                    return new ResourceNotFoundException("Diretor nao encontrado");
                });

        if (!manager.getRole().getName().equals(Role.ROLE_MANAGER.toString())) {
            logger.error(("O Utilizador tem permissao para atribuir aplicantes"));
            throw new ForbiddenException(("O Utilizador tem permissao para atribuir aplicantes"));
        }

        // Optional: avoid exact duplicate active assignment for same pair
        if (assignmentRepository.existsByAplicanteIdAndAssigneeIdAndActiveTrue(aplicanteId, assignee.getId())) {
            logger.error("O Aplicante ja foi atribuido a este Funcionario");
            throw new AlreadyExistException("O Aplicante ja foi atribuido a este Funcionario");
        }

        aplicante.setEstado(AplicanteStatus.ATRIBUIDO);

        // create new assignment
        AplicanteAssignment aa = new AplicanteAssignment();
        aa.setAplicante(aplicante);
        aa.setAssignee(assignee);
        aa.setAssignedBy(manager);
        aa.setActive(true);
        aa.setStatus(EstadoTarefa.ATRIBUIDO);
        aa.setNotes(notes);

        aplicante.addAssignment(aa); // sets back-reference
        entityManager.persist(aa);
        return aa;
    }

    @Transactional
    public void closeAssignment(Long assignmentId) {

        AplicanteAssignment aa = assignmentRepository.findByAplicante_IdAndActiveTrue(assignmentId)
                .orElseThrow(() -> {
                    logger.error("Atribuicao nao encontrada: {}", assignmentId);
                    return new ResourceNotFoundException("Atribuicao nao encontrada: " + assignmentId);
                });

        aa.setActive(false);
        aa.setStatus(EstadoTarefa.FECHADO);
    }

    @Transactional
    public int closeAllActiveForAplicante(Long aplicanteId, Long managerStaffId, String notes) {
        User manager = staffRepo.findById(managerStaffId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found: " + managerStaffId));
        if (!manager.getRole().getName().equals(Role.ROLE_MANAGER.toString())) {
            throw new IllegalStateException("Only a MANAGER can close assignments.");
        }

        var activeList = assignmentRepository.findByAplicanteIdAndActiveTrueOrderByAssignedAtDesc(aplicanteId);
        activeList.forEach(a -> {
            a.setActive(false);
            a.setStatus(EstadoTarefa.FECHADO);
            if (notes != null && !notes.isBlank()) a.setNotes(notes);
        });
        assignmentRepository.saveAll(activeList);
        return activeList.size();
    }

    @Transactional(readOnly = true)
    public List<AplicanteAssignment> listActive(Long aplicanteId) {
        return assignmentRepository.findByAplicanteIdAndActiveTrueOrderByAssignedAtDesc(aplicanteId);
    }

}
