package tl.gov.mci.lis.models.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

import java.time.Instant;

@Entity
@Table(name = "lis_password_reset_token")
@Getter
@Setter
public class PasswordResetToken extends EntityDB {
    private String token;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id", referencedColumnName = "id")
    private User user;
    private Instant expiryDate;
}
