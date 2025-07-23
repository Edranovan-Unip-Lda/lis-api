package tl.gov.mci.lis.repositories.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.dadosmestre.AtividadeEconomica;

import java.util.ArrayList;
import java.util.List;

public class AtividadeEconomicaSpecification {
    public static Specification<AtividadeEconomica> matchesFilters(String codigo, Categoria tipo, NivelRisco tipoRisco) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (codigo != null && !codigo.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("codigo")), "%" + codigo.toLowerCase() + "%"));
            }

            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }

            if (tipoRisco != null) {
                predicates.add(cb.equal(root.get("tipoRisco"), tipoRisco));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
