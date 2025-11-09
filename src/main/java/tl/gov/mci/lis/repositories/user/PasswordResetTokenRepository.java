package tl.gov.mci.lis.repositories.user;

import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.user.PasswordResetToken;
import tl.gov.mci.lis.models.user.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByToken(String token);
}