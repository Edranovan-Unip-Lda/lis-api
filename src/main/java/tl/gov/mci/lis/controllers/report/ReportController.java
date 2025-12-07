package tl.gov.mci.lis.controllers.report;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.atividade.CertificadoLicencaAtividadeDto;
import tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.report.AplicanteReportFilter;
import tl.gov.mci.lis.dtos.report.CertificadoInscricaoCadastroReportFilter;
import tl.gov.mci.lis.dtos.report.CertificadoLicencaAtividadeReportFilter;
import tl.gov.mci.lis.dtos.report.EmpresaReportFilter;
import tl.gov.mci.lis.services.report.ReportService;

/**
 * REST Controller for generating reports on various entities.
 * All endpoints are restricted to users with ADMIN, MANAGER, CHIEF, or STAFF roles.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Generate a paginated report of Empresa entities with optional filters.
     *
     * @param filter Filter criteria for the report (all fields optional)
     * @param page   Page number
     * @param size   Page size
     * @return Paginated list of Empresa DTOs matching the filter criteria
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF')")
    @PostMapping("/empresas")
    public ResponseEntity<Page<EmpresaDto>> generateEmpresaReport(
            @RequestBody(required = false) EmpresaReportFilter filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (filter == null) {
            filter = new EmpresaReportFilter();
        }
        if (page != null && size != null) {
            return ResponseEntity.ok(reportService.generateEmpresaReportPagination(filter, page, size));
        } else {
            return ResponseEntity.ok(new PageImpl<>(reportService.generateEmpresaReport(filter)));
        }
    }

    /**
     * Generate a paginated report of Aplicante entities with optional filters.
     *
     * @param filter Filter criteria for the report (all fields optional)
     * @param page   Page number
     * @param size   Page size
     * @return Paginated list of Aplicante DTOs matching the filter criteria
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF')")
    @PostMapping("/aplicantes")
    public ResponseEntity<Page<AplicanteDto>> generateAplicanteReport(
            @RequestBody(required = false) AplicanteReportFilter filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (filter == null) {
            filter = new AplicanteReportFilter();
        }
        if (page != null && size != null) {
            return ResponseEntity.ok(reportService.generateAplicanteReportPagination(filter, page, size));
        } else {
            return ResponseEntity.ok(new PageImpl<>(reportService.generateAplicanteReport(filter)));
        }
    }

    /**
     * Generate a paginated report of CertificadoInscricaoCadastro entities with optional filters.
     *
     * @param filter Filter criteria for the report (all fields optional)
     * @param page   Page number (0-indexed, default: 0)
     * @param size   Page size (default: 50)
     * @return Paginated list of CertificadoInscricaoCadastro DTOs matching the filter criteria
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF')")
    @PostMapping("/certificados-inscricao-cadastro")
    public ResponseEntity<Page<CertificadoInscricaoCadastroDto>> generateCertificadoInscricaoCadastroReport(
            @RequestBody(required = false) CertificadoInscricaoCadastroReportFilter filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        if (filter == null) {
            filter = new CertificadoInscricaoCadastroReportFilter();
        }
        return ResponseEntity.ok(reportService.generateCertificadoInscricaoCadastroReport(filter, page, size));
    }

    /**
     * Generate a paginated report of CertificadoLicencaAtividade entities with optional filters.
     *
     * @param filter Filter criteria for the report (all fields optional)
     * @param page   Page number (0-indexed, default: 0)
     * @param size   Page size (default: 50)
     * @return Paginated list of CertificadoLicencaAtividade DTOs matching the filter criteria
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CHIEF')")
    @PostMapping("/certificados-licenca-atividade")
    public ResponseEntity<Page<CertificadoLicencaAtividadeDto>> generateCertificadoLicencaAtividadeReport(
            @RequestBody(required = false) CertificadoLicencaAtividadeReportFilter filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size
    ) {
        if (filter == null) {
            filter = new CertificadoLicencaAtividadeReportFilter();
        }
        return ResponseEntity.ok(reportService.generateCertificadoLicencaAtividadeReport(filter, page, size));
    }
}
