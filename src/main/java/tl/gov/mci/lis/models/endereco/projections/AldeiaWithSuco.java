package tl.gov.mci.lis.models.endereco.projections;

import org.springframework.data.rest.core.config.Projection;
import tl.gov.mci.lis.models.endereco.Aldeia;

@Projection(name = "withSuco", types = Aldeia.class)
public interface AldeiaWithSuco {
    Long getId();

    String getNome();

    SucoExcerpt getSuco();

    interface SucoExcerpt {
        Long getId();

        String getNome();
    }
}
