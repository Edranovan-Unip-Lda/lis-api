package tl.gov.mci.lis.dtos.notificacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoDto {
    private Long id;
    private Long notificacaoId;
    private String title;
    private String description;
    private Boolean visto;
    private Instant vistoEm;
    private Instant createdAt;

    // Aplicante details
    private Long aplicanteId;
    private String aplicanteNumero;
    private AplicanteStatus aplicanteStatus;
    private AplicanteType aplicanteTipo;
    private Categoria categoria;

    // Empresa details
    private Long empresaId;
    private String empresaNome;
    private String empresaNif;
}

