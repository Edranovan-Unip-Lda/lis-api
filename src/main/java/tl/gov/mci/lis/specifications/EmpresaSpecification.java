package tl.gov.mci.lis.specifications;

import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.dtos.report.EmpresaReportFilter;
import tl.gov.mci.lis.models.empresa.Empresa;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class EmpresaSpecification {

    public static Specification<Empresa> withFilter(EmpresaReportFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getNome() != null && !filter.getNome().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        "%" + filter.getNome().toLowerCase() + "%"
                ));
            }

            if (filter.getNif() != null && !filter.getNif().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nif")),
                        "%" + filter.getNif().toLowerCase() + "%"
                ));
            }

            if (filter.getNumeroRegistoComercial() != null && !filter.getNumeroRegistoComercial().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("numeroRegistoComercial")),
                        "%" + filter.getNumeroRegistoComercial().toLowerCase() + "%"
                ));
            }

            if (filter.getTelefone() != null && !filter.getTelefone().isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("telefone"), "%" + filter.getTelefone() + "%"));
            }

            if (filter.getTelemovel() != null && !filter.getTelemovel().isBlank()) {
                predicates.add(criteriaBuilder.like(root.get("telemovel"), "%" + filter.getTelemovel() + "%"));
            }

            if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + filter.getEmail().toLowerCase() + "%"
                ));
            }

            if (filter.getTipoPropriedade() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoPropriedade"), filter.getTipoPropriedade()));
            }

            if (filter.getTipoEmpresa() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoEmpresa"), filter.getTipoEmpresa()));
            }

            if (filter.getDataRegistoFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataRegisto"), filter.getDataRegistoFrom()));
            }

            if (filter.getDataRegistoTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataRegisto"), filter.getDataRegistoTo()));
            }

            if (filter.getCapitalSocialMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("capitalSocial"), filter.getCapitalSocialMin()));
            }

            if (filter.getCapitalSocialMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("capitalSocial"), filter.getCapitalSocialMax()));
            }

            if (filter.getTotalTrabalhadoresMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalTrabalhadores"), filter.getTotalTrabalhadoresMin()));
            }

            if (filter.getTotalTrabalhadoresMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalTrabalhadores"), filter.getTotalTrabalhadoresMax()));
            }

            if (filter.getSociedadeComercialId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sociedadeComercial").get("id"), filter.getSociedadeComercialId()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
