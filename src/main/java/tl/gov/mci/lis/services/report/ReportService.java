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
import tl.gov.mci.lis.dtos.atividade.CertificadoLicencaAtividadeDetailDto;
import tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroDetailsDto;
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
    private final static Sort SORT_DESC_ID = Sort.by(Sort.Direction.DESC, "id");

    @Transactional(readOnly = true)
    public List<EmpresaDto> generateEmpresaReport(EmpresaReportFilter filter) {
        logger.info("Generating Empresa report with filter: {}", filter);
        return empresaRepository.findAll(EmpresaSpecification.withFilter(filter), SORT_DESC_ID).stream()
                .map(empresaMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<EmpresaDto> generateEmpresaReportPagination(EmpresaReportFilter filter, int page, int size) {
        logger.info("Generating Empresa report with filter pagination: {}", filter);
        Pageable pageable = PageRequest.of(page, size, SORT_DESC_ID);
        return empresaRepository.findAll(EmpresaSpecification.withFilter(filter), pageable)
                .map(empresaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<AplicanteDto> generateAplicanteReport(AplicanteReportFilter filter) {
        logger.info("Generating Aplicante report with filter: {}", filter);
        return aplicanteRepository.findAll(AplicanteSpecification.withFilter(filter), SORT_DESC_ID).stream()
                .map(aplicanteMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Page<AplicanteDto> generateAplicanteReportPagination(AplicanteReportFilter filter, int page, int size) {
        logger.info("Generating Aplicante report with filter pagination: {}", filter);
        Pageable pageable = PageRequest.of(page, size, SORT_DESC_ID);
        return aplicanteRepository.findAll(AplicanteSpecification.withFilter(filter), pageable)
                .map(aplicanteMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<CertificadoInscricaoCadastroDetailsDto> generateCertificadoInscricaoCadastroReport(
            CertificadoInscricaoCadastroReportFilter filter) {
        logger.info("Generating CertificadoInscricaoCadastro report with filter: {}", filter);
        return certificadoInscricaoCadastroRepository.findAll(
                        CertificadoInscricaoCadastroSpecification.withFilter(filter), SORT_DESC_ID)
                .stream().map(certificadoMapper::toDto1).toList();
    }

    @Transactional(readOnly = true)
    public Page<CertificadoInscricaoCadastroDetailsDto> generateCertificadoInscricaoCadastroReportPagination(
            CertificadoInscricaoCadastroReportFilter filter, int page, int size) {
        logger.info("Generating CertificadoInscricaoCadastro report with filter pagination: {}", filter);
        Pageable pageable = PageRequest.of(page, size, SORT_DESC_ID);
        return certificadoInscricaoCadastroRepository.findAll(
                        CertificadoInscricaoCadastroSpecification.withFilter(filter), pageable)
                .map(certificadoMapper::toDto1);
    }

    @Transactional(readOnly = true)
    public List<CertificadoLicencaAtividadeDetailDto> generateCertificadoLicencaAtividadeReport(
            CertificadoLicencaAtividadeReportFilter filter) {
        logger.info("Generating CertificadoLicencaAtividade report with filter: {}", filter);
        return certificadoLicencaAtividadeRepository.findAll(
                        CertificadoLicencaAtividadeSpecification.withFilter(filter), SORT_DESC_ID)
                .stream().map(certificadoMapper::toDto1).toList();
    }

    @Transactional(readOnly = true)
    public Page<CertificadoLicencaAtividadeDetailDto> generateCertificadoLicencaAtividadeReportPagination(
            CertificadoLicencaAtividadeReportFilter filter, int page, int size) {
        logger.info("Generating CertificadoLicencaAtividade report with filter pagination: {}", filter);
        Pageable pageable = PageRequest.of(page, size, SORT_DESC_ID);
        return certificadoLicencaAtividadeRepository.findAll(
                        CertificadoLicencaAtividadeSpecification.withFilter(filter), pageable)
                .map(certificadoMapper::toDto1);
    }
}
