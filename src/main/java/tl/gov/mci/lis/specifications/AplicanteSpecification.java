package tl.gov.mci.lis.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.dtos.report.AplicanteReportFilter;
import tl.gov.mci.lis.models.aplicante.Aplicante;

import java.util.ArrayList;
import java.util.List;

public class AplicanteSpecification {
    public static final String ESTADO_APROVADO = "APROVADO";
    public static final String ESTADO_REJEITADO = "REJEITADO";

    public static Specification<Aplicante> withFilter(AplicanteReportFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTipo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), filter.getTipo()));
            }

            if (filter.getCategoria() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria"), filter.getCategoria()));
            }

            if (filter.getNumero() != null && !filter.getNumero().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("numero")),
                        "%" + filter.getNumero().toLowerCase() + "%"
                ));
            }

            if (filter.getEstado() != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), filter.getEstado()));
            } else {
                predicates.add(root.get("estado").in(ESTADO_APROVADO, ESTADO_REJEITADO));
            }

            if (filter.getEmpresaId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("empresa").get("id"), filter.getEmpresaId()));
            }

            if (filter.getUpdatedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedAtFrom()));
            }

            if (filter.getUpdatedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), filter.getUpdatedAtTo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
