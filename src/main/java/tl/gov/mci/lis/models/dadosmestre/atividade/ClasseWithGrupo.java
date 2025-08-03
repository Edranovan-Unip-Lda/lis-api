package tl.gov.mci.lis.models.dadosmestre.atividade;

import org.springframework.data.rest.core.config.Projection;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

@Projection(name = "withGrupo", types = ClasseAtividade.class)
public interface ClasseWithGrupo {
    Long getId();

    GrupoAtividadeExcerpt getGrupoAtividade();

    String getCodigo();

    String getDescricao();

    Categoria getTipo();

    NivelRisco getTipoRisco();

    NivelRisco getRisco();

    interface GrupoAtividadeExcerpt {
        Long getId();

        String getCodigo();

        String getDescricao();
    }
}
