package tl.gov.mci.lis.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.RecaptchaAction;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.services.cadastro.CertificadoService;
import tl.gov.mci.lis.services.recaptcha.RecaptchaService;

@Slf4j
@RestController
@RequestMapping("/api/v1/certificados")
@RequiredArgsConstructor
public class CertificadoController {
    private final CertificadoService certificadoService;
    private final RecaptchaService recaptchaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificado(
            @PathVariable Long id,
            @RequestParam("type") AplicanteType aplicanteType,
            @RequestParam(value = "categoria", required = false) Categoria categoria
    ) {
        if (aplicanteType != null && categoria == null) {
            return ResponseEntity.ok(
                    certificadoService.findByIdAndType(id, aplicanteType)
                            .orElseThrow(() -> new ResourceNotFoundException("Certificado nao existe"))
            );
        } else if (aplicanteType != null) {
            return ResponseEntity.ok(
                    certificadoService.findById(id, aplicanteType, categoria)
                            .orElseThrow()
            );
        } else {
            return ResponseEntity.badRequest().body("Parametros invalidos");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getCertificadoByAplicanteNumero(
            @RequestParam("numero") String numero,
            @RequestParam("recaptchaToken") String recaptchaToken
    ) {
        log.info("Buscando certificado pelo numero do aplicante: {}", numero);
        recaptchaService.validateOrThrow(recaptchaToken, RecaptchaAction.CERTIFICADO_SEARCH.name(), null);
        return ResponseEntity.ok(
                certificadoService.findByAplicanteNumero(numero)
                        .orElseThrow(() -> new ResourceNotFoundException("Certificado nao encontrado para o numero: " + numero))
        );
    }
}
