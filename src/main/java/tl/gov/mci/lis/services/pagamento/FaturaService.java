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
import tl.gov.mci.lis.models.pagamento.Fatura;
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
    public DocumentoDto saveRecibo(Long faturaId, Long pedidoId, Long aplicanteId, String username, MultipartFile file) {
        Objects.requireNonNull(username, "username is required");
        Objects.requireNonNull(faturaId, "faturaId is required");
        Objects.requireNonNull(file, "file is required");

        logger.info("Salvando recibo da fatura: {}", faturaId);

        Fatura fatura = faturaRepository.findDetailById(faturaId)
                .orElseThrow(() -> {
                    logger.error("Fatura não encontrada com ID: {}", faturaId);
                    return new ResourceNotFoundException("Fatura não encontrada com ID: " + faturaId);
                });

        if (Objects.isNull(fatura.getPedidoInscricaoCadastro()) && Objects.isNull(fatura.getPedidoLicencaAtividade()) && Objects.isNull(fatura.getPedidoVistoria())) {
            logger.error("Fatura {} não pertence ao pedido {} ou aplicante {}", faturaId, pedidoId, aplicanteId);
            throw new ResourceNotFoundException("Fatura não encontrada com ID: " + faturaId);
        }

        // 2) Prevent duplicate receipt
        if (fatura.getRecibo() != null) {
            throw new IllegalStateException("Fatura já possui recibo vinculado.");
        }

        // 3) Upload first (I/O), then persist (DB)
        Documento recibo = minioService.uploadFile(username, file);

        // 4) Keep both sides consistent
        recibo.setFatura(fatura);
        fatura.setRecibo(recibo); // if you have a helper in Fatura, call it instead

        // 5) Update status
        fatura.setStatus(FaturaStatus.PAGA);

        // 6) Persist the owning side (Documento owns the FK if it has @JoinColumn("fatura_id"))
        entityManager.persist(recibo);

        return faturaMapper.toDto(recibo);
    }

    public DocumentoDownload downloadRecibo(Long id, Long faturaId, Long pedidoId, Long aplicanteId) {
        Documento documento = documentoRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.error("Documento nao encontrado: {}", id);
                    return new ResourceNotFoundException("Documento nao encontrado com ID: " + id);
                });


        InputStreamResource stream = minioService.downloadFileAsStream(documento);
        return new DocumentoDownload(stream, documento.getNome(), documento.getTipo());
    }

    @Transactional
    public DocumentoDto deleteRecibo(Long reciboId, Long faturaId, Long pedidoId, Long aplicanteId) {
        return faturaRepository
                .findById(faturaId)
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
                .orElseThrow(() -> {
                    logger.error("Documento nao encontrado com ID: {}", reciboId);
                    return new ResourceNotFoundException("Documento nao encontrado com ID: " + reciboId);
                });

    }

}
