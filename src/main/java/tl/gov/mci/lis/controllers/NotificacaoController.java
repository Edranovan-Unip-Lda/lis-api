package tl.gov.mci.lis.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.notificacao.NotificacaoDto;
import tl.gov.mci.lis.models.user.CustomUserDetails;
import tl.gov.mci.lis.services.notificacao.NotificacaoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {
    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoDto>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<NotificacaoDto> notifications = notificacaoService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificacaoDto>> getUserUnreadNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<NotificacaoDto> notifications = notificacaoService.getUserUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<NotificacaoDto>> getMyNotificationsPaginated(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long userId = userDetails.getUser().getId();
        Page<NotificacaoDto> notifications = notificacaoService.getUserNotificationsPaginated(userId, page, size);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificacaoId}/mark-seen")
    public ResponseEntity<Void> markAsSeen(
            @PathVariable Long notificacaoId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        notificacaoService.markAsSeen(notificacaoId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/mark-all-seen")
    public ResponseEntity<Void> markAllAsSeen(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        notificacaoService.markAllAsSeen(userId);
        return ResponseEntity.noContent().build();
    }
}

