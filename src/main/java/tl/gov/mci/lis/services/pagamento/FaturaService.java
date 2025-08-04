package tl.gov.mci.lis.services.pagamento;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.configs.minio.MinioService;
import tl.gov.mci.lis.dtos.mappers.FaturaMapper;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.documento.DocumentoDownload;
import tl.gov.mci.lis.repositories.documento.DocumentoRepository;
import tl.gov.mci.lis.repositories.pagamento.FaturaRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FaturaService {
    private static final Logger logger = LoggerFactory.getLogger(FaturaService.class);
    private final FaturaRepository faturaRepository;
    private final MinioService minioService;
    private final EntityManager entityManager;
    private final FaturaMapper faturaMapper;
    private final DocumentoRepository documentoRepository;


    @Transactional
    public DocumentoDto saveRecibo(Long aplicanteId, Long pedidoId, String username, Long faturaId, MultipartFile file) {
        logger.info("Salvando recibo da fatura: {}", faturaId);
        return faturaRepository.findByIdAndPedidoInscricaoCadastro_IdAndPedidoInscricaoCadastro_Aplicante_Id(faturaId, pedidoId, aplicanteId)
                .map(fatura -> {
                    Documento recibo = minioService.uploadFile(username, file);
                    recibo.setFatura(fatura);
                    recibo.getFatura().setStatus(FaturaStatus.PAGA);
                    this.entityManager.persist(recibo);
                    return faturaMapper.toDto(recibo);
                })
                .orElseThrow();
    }

    public DocumentoDownload downloadRecibo(Long id, Long faturaId, Long pedidoId, Long aplicanteId) {
        Documento documento = documentoRepository
                .findByIdAndFatura_IdAndFatura_PedidoInscricaoCadastro_IdAndFatura_PedidoInscricaoCadastro_Aplicante_Id(id, faturaId, pedidoId, aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado com ID: " + id));

        InputStreamResource stream = minioService.downloadFileAsStream(documento);
        return new DocumentoDownload(stream, documento.getNome(), documento.getTipo());
    }

    @Transactional
    public DocumentoDto deleteRecibo(Long reciboId, Long faturaId, Long pedidoId, Long aplicanteId) {
        return faturaRepository
                .findByIdAndPedidoInscricaoCadastro_IdAndPedidoInscricaoCadastro_Aplicante_Id(faturaId, pedidoId, aplicanteId)
                .map(fatura -> {
                    if (!Objects.equals(fatura.getRecibo().getId(), reciboId)) {
                        throw new ResourceNotFoundException("Documento nao encontrado com ID: " + reciboId);
                    }
                    Documento documento = fatura.getRecibo();
                    fatura.setRecibo(null);
                    fatura.setStatus(FaturaStatus.EMITIDA);
                    entityManager.flush();

                    minioService.archiveFile(documento);
                    logger.info("Documento removed no BD: {}", documento.getNome());
                    return faturaMapper.toDto(documento);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Documento nao encontrado com ID: " + reciboId));

    }

}
