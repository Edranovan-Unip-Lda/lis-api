package tl.gov.mci.lis.models.endereco.projections;

import org.springframework.data.rest.core.config.Projection;
import tl.gov.mci.lis.models.endereco.PostoAdministrativo;

@Projection(name = "withMunicipio", types = PostoAdministrativo.class)
public interface PostoAdministrativoWithMunicipio {
    Long getId();

    String getNome();

    MunicipioExcerpt getMunicipio();

    interface MunicipioExcerpt {
        Long getId();

        String getNome();
    }
}
