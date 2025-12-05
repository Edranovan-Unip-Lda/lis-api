package tl.gov.mci.lis.specifications;

import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.dtos.report.CertificadoInscricaoCadastroReportFilter;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class CertificadoInscricaoCadastroSpecification {

    public static Specification<CertificadoInscricaoCadastro> withFilter(CertificadoInscricaoCadastroReportFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSociedadeComercial() != null && !filter.getSociedadeComercial().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("sociedadeComercial")),
                        "%" + filter.getSociedadeComercial().toLowerCase() + "%"
                ));
            }

            if (filter.getNumeroRegistoComercial() != null && !filter.getNumeroRegistoComercial().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("numeroRegistoComercial")),
                        "%" + filter.getNumeroRegistoComercial().toLowerCase() + "%"
                ));
            }

            if (filter.getAtividade() != null && !filter.getAtividade().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("atividade")),
                        "%" + filter.getAtividade().toLowerCase() + "%"
                ));
            }

            if (filter.getDataValidade() != null && !filter.getDataValidade().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("dataValidade"), filter.getDataValidade()));
            }

            if (filter.getDataEmissao() != null && !filter.getDataEmissao().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("dataEmissao"), filter.getDataEmissao()));
            }

            if (filter.getNomeDiretorGeral() != null && !filter.getNomeDiretorGeral().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nomeDiretorGeral")),
                        "%" + filter.getNomeDiretorGeral().toLowerCase() + "%"
                ));
            }

            if (filter.getAplicanteId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("pedidoInscricaoCadastro").get("aplicante").get("id"),
                        filter.getAplicanteId()
                ));
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
