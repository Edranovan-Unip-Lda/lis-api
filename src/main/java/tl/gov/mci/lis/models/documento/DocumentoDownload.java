package tl.gov.mci.lis.models.documento;

import org.springframework.core.io.InputStreamResource;

public record DocumentoDownload(InputStreamResource stream, String fileName, String contentType) {
}
