package tl.gov.mci.lis.dtos.endereco;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.endereco.Endereco}
 */
@Value
public class EnderecoDto implements Serializable {
    Long id;
    String local;
    AldeiaDto aldeia;

    /**
     * DTO for {@link tl.gov.mci.lis.models.endereco.Aldeia}
     */
    @Value
    public static class AldeiaDto implements Serializable {
        Long id;
        @NotBlank(message = "Nome é obrigatório")
        String nome;
        SucoDto suco;

        /**
         * DTO for {@link tl.gov.mci.lis.models.endereco.Suco}
         */
        @Value
        public static class SucoDto implements Serializable {
            Long id;
            @NotBlank(message = "Nome é obrigatório")
            String nome;
            PostoAdministrativoDto postoAdministrativo;

            /**
             * DTO for {@link tl.gov.mci.lis.models.endereco.PostoAdministrativo}
             */
            @Value
            public static class PostoAdministrativoDto implements Serializable {
                Long id;
                @NotBlank(message = "é obrigatório")
                String nome;
                MunicipioDto municipio;

                /**
                 * DTO for {@link tl.gov.mci.lis.models.endereco.Municipio}
                 */
                @Value
                public static class MunicipioDto implements Serializable {
                    Long id;
                    @NotBlank(message = "Nome é obrigatório")
                    String nome;
                }
            }
        }
    }
}