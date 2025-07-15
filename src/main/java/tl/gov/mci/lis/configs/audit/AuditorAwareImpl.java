package tl.gov.mci.lis.configs.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)){
            return Optional.of("System");
        }
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return Optional.ofNullable(userDetails.getUsername());
        } else {
            return Optional.of(authentication.getName());
        }
    }
}
