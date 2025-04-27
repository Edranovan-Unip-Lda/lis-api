package tl.gov.mci.lis.configs.email;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Table(name = "lis_email_config")
@Getter
@Setter
public class EmailConfig extends EntityDB {
    private String smtpHost;
    private int smtpPort;
    private String username;
    private String password;
    private String fromEmail;
    private boolean isActive;
}
