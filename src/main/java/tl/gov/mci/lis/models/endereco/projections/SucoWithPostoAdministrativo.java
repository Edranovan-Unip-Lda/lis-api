package tl.gov.mci.lis.models.endereco.projections;

import org.springframework.data.rest.core.config.Projection;
import tl.gov.mci.lis.models.endereco.Suco;

@Projection(name = "withPostoAdministrativo", types = Suco.class)
public interface SucoWithPostoAdministrativo {
    Long getId();

    String getNome();

    PostoAdministrativoExcerpt getPostoAdministrativo();

    interface PostoAdministrativoExcerpt {
        Long getId();

        String getNome();
    }
}
