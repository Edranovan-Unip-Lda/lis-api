package tl.gov.mci.lis.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.dtos.report.CertificadoInscricaoCadastroReportFilter;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CertificadoInscricaoCadastroSpecification {

    public static Specification<CertificadoInscricaoCadastro> withFilter(CertificadoInscricaoCadastroReportFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCategoria() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pedidoInscricaoCadastro").get("aplicante").get("categoria"), filter.getCategoria()));
            }

            if (filter.getEmpresaId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pedidoInscricaoCadastro").get("aplicante").get("empresa").get("id"), filter.getEmpresaId()));
            }


            if (filter.getTipoEstabelecimento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pedidoInscricaoCadastro").get("tipoEstabelecimento"), filter.getTipoEstabelecimento()));
            }

            if (filter.getCaraterizacaoEstabelecimento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pedidoInscricaoCadastro").get("caraterizacaoEstabelecimento"), filter.getCaraterizacaoEstabelecimento()));
            }

            if (filter.getRisco() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pedidoInscricaoCadastro").get("risco"), filter.getRisco()));
            }

            if (filter.getAto() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pedidoInscricaoCadastro").get("ato"), filter.getAto()));
            }

            if (Objects.nonNull(filter.getClasseAtividadeId())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("pedidoInscricaoCadastro").get("classeAtividade").get("id"),
                        filter.getClasseAtividadeId()
                ));
            }

            if (Objects.nonNull(filter.getMunicipioId())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("sede").get("aldeia").get("suco").get("postoAdministrativo").get("municipio").get("id"),
                        filter.getMunicipioId()
                ));
            }

            if (Objects.nonNull(filter.getPostoAdministrativoId())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("sede").get("aldeia").get("suco").get("postoAdministrativo").get("id"),
                        filter.getPostoAdministrativoId()
                ));
            }

            if (Objects.nonNull(filter.getSucoId())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("sede").get("aldeia").get("suco").get("id"),
                        filter.getSucoId()
                ));
            }

            if (filter.getDataValidadeFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataValidade"), filter.getDataValidadeFrom()));
            }

            if (filter.getDataValidadeTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataValidade"), filter.getDataValidadeTo()));
            }

            if (filter.getDataEmissaoFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataEmissao"), filter.getDataEmissaoFrom()));
            }

            if (filter.getDataEmissaoTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataEmissao"), filter.getDataEmissaoTo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
