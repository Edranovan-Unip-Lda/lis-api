package tl.gov.mci.lis.services.notificacao;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.configs.email.EmailService;
import tl.gov.mci.lis.enums.AccountStatus;
import tl.gov.mci.lis.enums.EmailTemplate;
import tl.gov.mci.lis.enums.Role;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.notificacao.Notificacao;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.models.user.UserNotificacao;
import tl.gov.mci.lis.repositories.notificacao.UserNotificacaoRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {
    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);
    private final UserNotificacaoRepository userNotificacaoRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Transactional
    public void createNotification(Long receiverId, Aplicante aplicante, EmailTemplate template) {
        logger.info("Criando notificação...");
        // 1. Load Receptor (cliente)
        User receptor = userRepository.findByIdWithRole(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não existe!"));

        // 2. Load Chief for category
        User chief = userRepository.findByRole_NameAndDirecao_NomeAndStatusActive(
                Role.ROLE_CHIEF.name(),
                aplicante.getCategoria(),
                AccountStatus.active.name()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Utilizador Chefia não existe!")
        );

        // 3. Load Manager for category
        User manager = userRepository.findByRole_NameAndDirecao_NomeAndStatusActive(
                Role.ROLE_MANAGER.name(),
                aplicante.getCategoria(),
                AccountStatus.active.name()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Utilizador Diretor(a) não existe!")
        );


        // 4. Create Notificação
        Notificacao notificacao = new Notificacao();
        notificacao.setAplicanteStatus(aplicante.getEstado());
        notificacao.setAplicante(aplicante);

        entityManager.persist(notificacao);

        if (template.equals(EmailTemplate.ATRIBUIR)) {
            // 3. Load Manager for category
            User staff = userRepository.findByRole_NameAndDirecao_NomeAndStatusActive(
                    Role.ROLE_STAFF.name(),
                    aplicante.getCategoria(),
                    AccountStatus.active.name()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Utilizador Funcionario(a) não existe!")
            );
            criarUserNotificacao(notificacao, staff);
            emailService.sendNotificacaoEmail(staff, aplicante, template);
        }

        // 5. Create UserNotificacao (one for each user)
        criarUserNotificacao(notificacao, receptor);
        criarUserNotificacao(notificacao, chief);
        criarUserNotificacao(notificacao, manager);

        // 6. Send Emails
        emailService.sendNotificacaoEmail(receptor, aplicante, template);
        emailService.sendNotificacaoEmail(chief, aplicante, template);
        emailService.sendNotificacaoEmail(manager, aplicante, template);

        logger.info("Notificação criada com sucesso para Cliente, Chefia e Diretor(a).");
    }


    private void criarUserNotificacao(Notificacao notificacao, User user) {
        UserNotificacao un = new UserNotificacao();
        un.setDestinatario(user);
        un.setVisto(false);
        un.setVistoEm(null);

        // Keep both sides in sync (correct JPA pattern)
        notificacao.addDestinatario(un);

        entityManager.persist(un);
    }

    public void markAllAsSeen(Long userId) {
        List<UserNotificacao> list = userNotificacaoRepository.findByDestinatario_IdOrderByIdDesc(userId);

        for (UserNotificacao un : list) {
            if (!un.getVisto()) {
                un.setVisto(true);
                un.setVistoEm(Instant.now());
                entityManager.merge(un);
            }
        }
    }
}
