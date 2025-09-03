package tl.gov.mci.lis.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.dadosmestre.Role;
import tl.gov.mci.lis.models.dadosmestre.SociedadeComercial;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.dadosmestre.atividade.GrupoAtividade;
import tl.gov.mci.lis.models.endereco.Aldeia;
import tl.gov.mci.lis.models.endereco.Municipio;
import tl.gov.mci.lis.models.endereco.PostoAdministrativo;
import tl.gov.mci.lis.models.endereco.Suco;
import tl.gov.mci.lis.models.pagamento.Taxa;
import tl.gov.mci.lis.models.user.User;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(
                Role.class,
                Municipio.class,
                PostoAdministrativo.class,
                Suco.class,
                Aldeia.class,
                Taxa.class,
                Taxa.class,
                SociedadeComercial.class,
                GrupoAtividade.class,
                ClasseAtividade.class,
                Direcao.class,
                User.class
        );
    }
}