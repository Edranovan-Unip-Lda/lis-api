package tl.gov.mci.lis.configs.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "emailConfig", path = "email")
public interface EmailConfigRepository extends JpaRepository<EmailConfig, Long> {
    EmailConfig findTopByOrderByIdDesc();
}