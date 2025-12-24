package tl.gov.mci.lis.services.notificacao;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.configs.email.EmailService;
import tl.gov.mci.lis.dtos.notificacao.NotificacaoDto;
import tl.gov.mci.lis.dtos.notificacao.NotificationTemplate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificacaoService {
    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);
    private final UserNotificacaoRepository userNotificacaoRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final NotificationTemplateResolver templateResolver;
    private final NotificationMessageRenderer messageRenderer;

    @Transactional
    public void createNotification(Long receiverId, Aplicante aplicante, EmailTemplate template) {
        logger.info("Criando notificação...");

        // Build notification context for placeholder replacement
        Map<String, Object> context = buildNotificationContext(aplicante, null);

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
            // Load Staff for category
            User staff = userRepository.findByRole_NameAndDirecao_NomeAndStatusActive(
                    Role.ROLE_STAFF.name(),
                    aplicante.getCategoria(),
                    AccountStatus.active.name()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Utilizador Funcionario(a) não existe!")
            );

            // Resolve and render template for staff (staff uses CHIEF template)
            NotificationTemplate staffTemplate = templateResolver.resolve(aplicante.getEstado(), Role.ROLE_CHIEF);
            NotificationTemplate renderedStaffTemplate = messageRenderer.render(staffTemplate, context);

            criarUserNotificacaoWithTemplate(notificacao, staff, renderedStaffTemplate);
            emailService.sendNotificacaoEmail(staff, aplicante, template);
        }

        // 5. Resolve templates for each role
        NotificationTemplate clienteTemplate = templateResolver.resolve(aplicante.getEstado(), Role.ROLE_CLIENT);
        NotificationTemplate chefeTemplate = templateResolver.resolve(aplicante.getEstado(), Role.ROLE_CHIEF);
        NotificationTemplate diretorTemplate = templateResolver.resolve(aplicante.getEstado(), Role.ROLE_MANAGER);

        // 6. Render templates with context
        NotificationTemplate renderedClienteTemplate = messageRenderer.render(clienteTemplate, context);
        NotificationTemplate renderedChefeTemplate = messageRenderer.render(chefeTemplate, context);
        NotificationTemplate renderedDiretorTemplate = messageRenderer.render(diretorTemplate, context);

        // 7. Create UserNotificacao (one for each user)
        criarUserNotificacaoWithTemplate(notificacao, receptor, renderedClienteTemplate);
        criarUserNotificacaoWithTemplate(notificacao, chief, renderedChefeTemplate);
        criarUserNotificacaoWithTemplate(notificacao, manager, renderedDiretorTemplate);

        // 8. Send Emails
        emailService.sendNotificacaoEmail(receptor, aplicante, template);
        emailService.sendNotificacaoEmail(chief, aplicante, template);
        emailService.sendNotificacaoEmail(manager, aplicante, template);

        logger.info("Notificação criada com sucesso para Cliente, Chefia e Diretor(a).");
    }

    @Transactional
    public void createAssignNotification(Long staffId, Aplicante aplicante, EmailTemplate template) {
        logger.info("Criando notificação de atribuição...");

        // 1. Load Receptor (staff)
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Funsionário não existe!"));

        String responsavel = staff.getFirstName() + ' ' + staff.getLastName();
        // Build notification context with responsavel
        Map<String, Object> context = buildNotificationContext(aplicante, responsavel);

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

        User cliente = userRepository.findByIdWithRole(aplicante.getEmpresa().getUtilizador().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador cliente não existe!"));

        // 4. Create Notificação
        Notificacao notificacao = new Notificacao();
        notificacao.setAplicanteStatus(aplicante.getEstado());
        notificacao.setAplicante(aplicante);

        entityManager.persist(notificacao);

        // 5. Resolve templates for each role
        NotificationTemplate clienteTemplate = templateResolver.resolve(aplicante.getEstado(), Role.ROLE_CLIENT);
        NotificationTemplate chefeTemplate = templateResolver.resolve(aplicante.getEstado(), Role.ROLE_CHIEF);
        NotificationTemplate diretorTemplate = templateResolver.resolve(aplicante.getEstado(), Role.ROLE_MANAGER);

        // 6. Render templates with context
        NotificationTemplate renderedClienteTemplate = messageRenderer.render(clienteTemplate, context);
        NotificationTemplate renderedChefeTemplate = messageRenderer.render(chefeTemplate, context);
        NotificationTemplate renderedDiretorTemplate = messageRenderer.render(diretorTemplate, context);
        NotificationTemplate renderedStaffTemplate = messageRenderer.render(chefeTemplate, context); // Staff uses CHEFE template

        // 7. Create UserNotificacao (one for each user)
        criarUserNotificacaoWithTemplate(notificacao, staff, renderedStaffTemplate);
        criarUserNotificacaoWithTemplate(notificacao, chief, renderedChefeTemplate);
        criarUserNotificacaoWithTemplate(notificacao, manager, renderedDiretorTemplate);
        criarUserNotificacaoWithTemplate(notificacao, cliente, renderedClienteTemplate);

        // 8. Send Emails
        emailService.sendNotificacaoEmail(staff, aplicante, template);
        emailService.sendNotificacaoEmail(chief, aplicante, template);
        emailService.sendNotificacaoEmail(manager, aplicante, template);
        emailService.sendNotificacaoEmail(cliente, aplicante, template);

        logger.info("Notificação criada com sucesso para Funcionario, Chefia e Diretor(a).");
    }


    private void criarUserNotificacaoWithTemplate(Notificacao notificacao, User user, NotificationTemplate template) {
        UserNotificacao un = new UserNotificacao();
        un.setDestinatario(user);
        un.setVisto(false);
        un.setVistoEm(null);
        un.setTitle(template.title());
        un.setDescription(template.description());

        // Keep both sides in sync (correct JPA pattern)
        notificacao.addDestinatario(un);

        entityManager.persist(un);
    }

    private Map<String, Object> buildNotificationContext(Aplicante aplicante, String responsavel) {
        return buildNotificationContext(aplicante, responsavel, null);
    }

    private Map<String, Object> buildNotificationContext(Aplicante aplicante, String responsavel, String motivo) {
        String clienteNome = aplicante.getEmpresa().getUtilizador().getFirstName() + ' ' + aplicante.getEmpresa().getUtilizador().getLastName();
        Map<String, Object> context = new HashMap<>();
        context.put("processoId", aplicante.getNumero());
        context.put("nomeCliente", clienteNome);
        context.put("empresa", aplicante.getEmpresa().getNome());

        if (responsavel != null) {
            context.put("responsavel", responsavel);
        }

        if (motivo != null && !motivo.isEmpty()) {
            context.put("motivo", motivo);
        }

        return context;
    }

    public void markAsSeen(Long notificacaoId, Long userId) {
        UserNotificacao un = userNotificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada"));

        if (!un.getDestinatario().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notificação não pertence ao utilizador");
        }

        if (!un.getVisto()) {
            un.setVisto(true);
            un.setVistoEm(Instant.now());
            entityManager.merge(un);
        }
    }

    @Transactional
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

    public List<NotificacaoDto> getUserNotifications(Long userId) {
        return userNotificacaoRepository.findNotificationsByUserId(userId);
    }

    public List<NotificacaoDto> getUserUnreadNotifications(Long userId) {
        return userNotificacaoRepository.findByVistoFalseAndDestinatario_Id(userId);
    }

    public Page<NotificacaoDto> getUserNotificationsPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userNotificacaoRepository.findNotificationsByUserIdPaginated(userId, pageable);
    }
}
