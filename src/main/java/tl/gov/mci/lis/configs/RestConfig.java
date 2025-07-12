package tl.gov.mci.lis.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import tl.gov.mci.lis.models.dadosmestre.Role;
import tl.gov.mci.lis.models.endereco.Aldeia;
import tl.gov.mci.lis.models.endereco.Municipio;
import tl.gov.mci.lis.models.endereco.PostoAdministrativo;
import tl.gov.mci.lis.models.endereco.Suco;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(
                Role.class,
                Municipio.class,
                PostoAdministrativo.class,
                Suco.class,
                Aldeia.class
        );
    }
}