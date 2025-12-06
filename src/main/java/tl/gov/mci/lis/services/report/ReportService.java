package tl.gov.mci.lis.services.report;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.atividade.CertificadoLicencaAtividadeDto;
import tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.AplicanteMapper;
import tl.gov.mci.lis.dtos.mappers.CertificadoMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.dtos.report.AplicanteReportFilter;
import tl.gov.mci.lis.dtos.report.CertificadoInscricaoCadastroReportFilter;
import tl.gov.mci.lis.dtos.report.CertificadoLicencaAtividadeReportFilter;
import tl.gov.mci.lis.dtos.report.EmpresaReportFilter;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.atividade.CertificadoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.cadastro.CertificadoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.specifications.AplicanteSpecification;
import tl.gov.mci.lis.specifications.CertificadoInscricaoCadastroSpecification;
import tl.gov.mci.lis.specifications.CertificadoLicencaAtividadeSpecification;
import tl.gov.mci.lis.specifications.EmpresaSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final EmpresaRepository empresaRepository;
    private final AplicanteRepository aplicanteRepository;
    private final CertificadoInscricaoCadastroRepository certificadoInscricaoCadastroRepository;
    private final CertificadoLicencaAtividadeRepository certificadoLicencaAtividadeRepository;

    private final EmpresaMapper empresaMapper;
    private final AplicanteMapper aplicanteMapper;
    private final CertificadoMapper certificadoMapper;

    @Transactional(readOnly = true)
    public List<EmpresaDto> generateEmpresaReport(EmpresaReportFilter filter) {
        logger.info("Generating Empresa report with filter: {}", filter);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return empresaRepository.findAll(EmpresaSpecification.withFilter(filter), sort).stream()
                .map(empresaMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<EmpresaDto> generateEmpresaReportPagination(EmpresaReportFilter filter, int page, int size) {
        logger.info("Generating Empresa report with filter pagination: {}", filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return empresaRepository.findAll(EmpresaSpecification.withFilter(filter), pageable)
                .map(empresaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<AplicanteDto> generateAplicanteReport(AplicanteReportFilter filter, int page, int size) {
        logger.info("Generating Aplicante report with filter: {}", filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return aplicanteRepository.findAll(AplicanteSpecification.withFilter(filter), pageable)
                .map(aplicanteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CertificadoInscricaoCadastroDto> generateCertificadoInscricaoCadastroReport(
            CertificadoInscricaoCadastroReportFilter filter, int page, int size) {
        logger.info("Generating CertificadoInscricaoCadastro report with filter: {}", filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return certificadoInscricaoCadastroRepository.findAll(
                        CertificadoInscricaoCadastroSpecification.withFilter(filter), pageable)
                .map(certificadoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CertificadoLicencaAtividadeDto> generateCertificadoLicencaAtividadeReport(
            CertificadoLicencaAtividadeReportFilter filter, int page, int size) {
        logger.info("Generating CertificadoLicencaAtividade report with filter: {}", filter);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return certificadoLicencaAtividadeRepository.findAll(
                        CertificadoLicencaAtividadeSpecification.withFilter(filter), pageable)
                .map(certificadoMapper::toDto);
    }
}
