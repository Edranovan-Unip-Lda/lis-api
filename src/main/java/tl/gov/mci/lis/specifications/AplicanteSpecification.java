package tl.gov.mci.lis.specifications;

import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.dtos.report.AplicanteReportFilter;
import tl.gov.mci.lis.models.aplicante.Aplicante;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class AplicanteSpecification {

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
            }

            if (filter.getEmpresaId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("empresa").get("id"), filter.getEmpresaId()));
            }

            if (filter.getDirecaoId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("direcaoAtribuida").get("id"), filter.getDirecaoId()));
            }

            if (filter.getCreatedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtFrom()));
            }

            if (filter.getCreatedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtTo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
