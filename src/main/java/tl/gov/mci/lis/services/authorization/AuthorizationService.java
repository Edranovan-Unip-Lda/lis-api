package tl.gov.mci.lis.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.user.CustomUserDetails;

/**
 * Service class responsible for handling authorization logic related to Empresa entities.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    /**
     * Retrieves the ID of the Empresa associated with the currently authenticated user.
     *
     * @return the ID of the Empresa associated with the currently authenticated user
     * @throws ForbiddenException if the authenticated user is not linked to any Empresa or the Empresa ID is null
     */
    public Long getCurrentEmpresaId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        Empresa empresa = userDetails.getUser().getEmpresa();
        if (empresa == null || empresa.getId() == null) {
            throw new ForbiddenException("O usuário não está vinculado a nenhuma Empresa");
        }

        return empresa.getId();
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUsername();
    }

    /**
     * Verifies that the currently authenticated user is associated with the specified Empresa.
     * If the user is not associated with the target Empresa, a ForbiddenException is thrown.
     *
     * @param targetEmpresaId the ID of the Empresa to verify ownership against
     * @throws ForbiddenException if the authenticated user does not match the target Empresa
     */
    public void assertUserOwnsEmpresa(Long targetEmpresaId) {
        Long currentEmpresaId = getCurrentEmpresaId();
        if (!currentEmpresaId.equals(targetEmpresaId)) {
            throw new ForbiddenException("Acesso negado: Incompatibilidade da Empresa");
        }
    }

    public void assertUserOwnsUtilizador(String targetUtilizadorUsername) {
        String username = getCurrentUsername();
        if (!username.equals(targetUtilizadorUsername)) {
            throw new ForbiddenException("Acesso negado: Incompatibilidade do Utilizador");
        }
    }
}
