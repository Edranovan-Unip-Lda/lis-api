package tl.gov.mci.lis.models.atividade;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.endereco.Endereco;

import java.time.LocalDate;

@Entity
@Table(name = "lis_atividade_pedido_licenca_arrendador")
@Getter
@Setter
public class Arrendador extends EntityDB {
    private String tipo;
    private String nome;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;

    private String tipoDocumento;
    private String numeroDocumento;
    private Double areaTotalTerreno;
    private Double areaTotalConstrucao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Double valorRendaMensal;
}
