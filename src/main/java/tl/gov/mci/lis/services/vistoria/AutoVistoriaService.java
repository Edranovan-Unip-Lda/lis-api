package tl.gov.mci.lis.services.vistoria;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.EmailTemplate;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;
import tl.gov.mci.lis.models.vistoria.AutoVistoria;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;
import tl.gov.mci.lis.models.vistoria.Requerente;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.ClasseAtividadeRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
import tl.gov.mci.lis.repositories.vistoria.PedidoVistoriaRepository;
import tl.gov.mci.lis.services.aplicante.AssignmentService;
import tl.gov.mci.lis.services.authorization.AuthorizationService;
import tl.gov.mci.lis.services.endereco.EnderecoService;
import tl.gov.mci.lis.services.notificacao.NotificacaoService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AutoVistoriaService {
    private static final Logger logger = LoggerFactory.getLogger(AutoVistoriaService.class);
    private final AplicanteRepository aplicanteRepository;
    private final AssignmentService assignmentService;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final EnderecoService enderecoService;
    private final ClasseAtividadeRepository classAtividadeRepo;
    private final PedidoVistoriaRepository pedidoVistoriaRepository;
    private final AuthorizationService authorizationService;
    private final NotificacaoService notificacaoService;

    @Transactional
    public AutoVistoria create(Long pedidoVistoriaId, AutoVistoria obj) {
        logger.info("Criando auto vistoria com pedidoVistoria Id: {}", pedidoVistoriaId);

        PedidoVistoria pedidoVistoria = pedidoVistoriaRepository.findById(pedidoVistoriaId)
                .orElseThrow(() -> {
                    logger.error("Pedido Vistoria nao encontrado");
                    return new ResourceNotFoundException("Pedido Vistoria nao encontrado");
                });

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

        obj.setPedidoVistoria(pedidoVistoriaRepository.getReferenceById(pedidoVistoriaId));
        obj.setFuncionario(userRepository.getReferenceById(obj.getFuncionario().getId()));

        entityManager.persist(obj);

        // atualizar estado do aplicante sem carregar o objeto inteiro:
        Aplicante aplicante = aplicanteRepository.getReferenceById(pedidoVistoria.getPedidoLicencaAtividade().getAplicante().getId());

        if (obj.getPrazo() != 0) {
            aplicante.setEstado(AplicanteStatus.SUSPENDE);

            // add history
            HistoricoEstadoAplicante historico = new HistoricoEstadoAplicante();
            historico.setStatus(aplicante.getEstado());
            historico.setAlteradoPor(authorizationService.getCurrentUsername());
            aplicante.addHistorico(historico);
            // send notification
            notificacaoService.createNotification(aplicante.getEmpresa().getUtilizador().getId(), aplicante, EmailTemplate.SUSPENSO);
        } else {

            aplicante.setEstado(AplicanteStatus.REVISAO);

            // add history
            HistoricoEstadoAplicante historico = new HistoricoEstadoAplicante();
            historico.setStatus(aplicante.getEstado());
            historico.setAlteradoPor(authorizationService.getCurrentUsername());
            aplicante.addHistorico(historico);

            // close assignment
            assignmentService.closeAssignment(aplicante.getId());
            // send notification
            notificacaoService.createNotification(aplicante.getEmpresa().getUtilizador().getId(), aplicante, EmailTemplate.REVISTO);
        }

        return obj;
    }
}
