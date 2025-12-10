package tl.gov.mci.lis.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.dtos.report.CertificadoLicencaAtividadeReportFilter;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CertificadoLicencaAtividadeSpecification {

    public static Specification<CertificadoLicencaAtividade> withFilter(
            CertificadoLicencaAtividadeReportFilter filter
    ) {
        return (root, query, cb) -> {
            // Avoid duplicate CLA rows from the to-many join
            Objects.requireNonNull(query).distinct(true);

            Join<CertificadoLicencaAtividade, PedidoLicencaAtividade> pla =
                    root.join("pedidoLicencaAtividade", JoinType.INNER);

            // PLA -> PV (restrict join to only APROVADO inspections)
            Join<PedidoLicencaAtividade, PedidoVistoria> pv =
                    pla.join("listaPedidoVistoria", JoinType.INNER);
            pv.on(cb.equal(pv.get("status"), PedidoStatus.SUBMETIDO)); // Only approved Vistoria

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCategoria() != null) {
                predicates.add(cb.equal(pla.get("aplicante").get("categoria"), filter.getCategoria()));
            }

            if (filter.getEmpresaId() != null) {
                predicates.add(cb.equal(pla.get("aplicante").get("empresa").get("id"), filter.getEmpresaId()));
            }

            // CertificadoLicencaAtividade related filters
            if (Objects.nonNull(filter.getMunicipioId())) {
                predicates.add(cb.equal(
                        root.get("sede").get("aldeia").get("suco").get("postoAdministrativo").get("municipio").get("id"),
                        filter.getMunicipioId()
                ));
            }

            if (Objects.nonNull(filter.getPostoAdministrativoId())) {
                predicates.add(cb.equal(
                        root.get("sede").get("aldeia").get("suco").get("postoAdministrativo").get("id"),
                        filter.getPostoAdministrativoId()
                ));
            }

            if (Objects.nonNull(filter.getSucoId())) {
                predicates.add(cb.equal(
                        root.get("sede").get("aldeia").get("suco").get("id"),
                        filter.getSucoId()
                ));
            }

            if (filter.getDataValidadeFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataValidade"), filter.getDataValidadeFrom()));
            }

            if (filter.getDataValidadeTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataValidade"), filter.getDataValidadeTo()));
            }

            if (filter.getDataEmissaoFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataEmissao"), filter.getDataEmissaoFrom()));
            }

            if (filter.getDataEmissaoTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataEmissao"), filter.getDataEmissaoTo()));
            }

            // PedidoLicencaAtividade related filters
            if (Objects.nonNull(filter.getClasseAtividadeId())) {
                predicates.add(cb.equal(
                        pla.get("classeAtividade").get("id"),
                        filter.getClasseAtividadeId()
                ));
            }

            if (filter.getRisco() != null) {
                predicates.add(cb.equal(pla.get("risco"), filter.getRisco()));
            }

            //PedidoVistoria related filters

            if (filter.getTipoVistoria() != null) {
                predicates.add(cb.equal(pv.get("tipoVistoria"), filter.getTipoVistoria()));
            }

            if (filter.getTipoEstabelecimento() != null) {
                predicates.add(cb.equal(pv.get("tipoEstabelecimento"), filter.getTipoEstabelecimento()));
            }

            if (filter.getAtividade() != null) {
                predicates.add(cb.equal(pv.get("atividade"), filter.getAtividade()));
            }

            if (filter.getTipoAtividade() != null) {
                predicates.add(cb.equal(pv.get("tipoAtividade"), filter.getTipoAtividade()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}