package tl.gov.mci.lis.services.cadastro;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.services.endereco.EnderecoService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CertificadoService {
    private static final Logger logger = LoggerFactory.getLogger(CertificadoService.class);
    private final EntityManager entityManager;
    private final EnderecoService enderecoService;

    @Transactional
    public CertificadoInscricaoCadastro saveCertificadoInscricaoCadastro(Aplicante aplicante) {
        logger.info("Salvando certificado de inscricao: {}", aplicante.getId());

        Endereco sede = new Endereco();
        Endereco sedePedido = aplicante.getPedidoInscricaoCadastro().getEmpresaSede();
        sede.setLocal(sedePedido.getLocal());
        sede.setAldeia(sedePedido.getAldeia());

        sede = enderecoService.create(sede);

        // Emitir certificado
        CertificadoInscricaoCadastro certificadoInscricaoCadastro = new CertificadoInscricaoCadastro();
        certificadoInscricaoCadastro.setAplicante(aplicante);
        certificadoInscricaoCadastro.setSociedadeComercial(aplicante.getEmpresa().getNome());
        certificadoInscricaoCadastro.setNumeroRegistoComercial(aplicante.getEmpresa().getNumeroRegistoComercial());
        certificadoInscricaoCadastro.setSede(sede);
        certificadoInscricaoCadastro.setAtividade(aplicante.getPedidoInscricaoCadastro().getClasseAtividade().getDescricao());
        certificadoInscricaoCadastro.setDataEmissao(LocalDate.now().toString());
        certificadoInscricaoCadastro.setDataValidade(LocalDate.now().plusYears(2).toString());
        certificadoInscricaoCadastro.setNomeDiretorGeral("Donald Trump");

        entityManager.persist(certificadoInscricaoCadastro);
        return certificadoInscricaoCadastro;
    }

    @Transactional
    public CertificadoLicencaAtividade saveCertificadoLicencaAtividade(Aplicante aplicante) {
        logger.info("Salvando certificado de licenca de atividade: {}", aplicante.getId());

        Endereco sede = new Endereco();
        Endereco sedePedido = aplicante.getPedidoLicencaAtividade().getEmpresaSede();
        sede.setLocal(sedePedido.getLocal());
        sede.setAldeia(sedePedido.getAldeia());

        sede = enderecoService.create(sede);

        // Emitir certificado
        CertificadoLicencaAtividade certificadoLicencaAtividade = new CertificadoLicencaAtividade();
        certificadoLicencaAtividade.setAplicante(aplicante);
        certificadoLicencaAtividade.setSociedadeComercial(aplicante.getEmpresa().getNome());
        certificadoLicencaAtividade.setNumeroRegistoComercial(aplicante.getEmpresa().getNumeroRegistoComercial());
        certificadoLicencaAtividade.setSede(sede);
        certificadoLicencaAtividade.setAtividade(aplicante.getPedidoLicencaAtividade().getTipoAtividade().getDescricao());
        certificadoLicencaAtividade.setAtividadeCodigo(aplicante.getPedidoLicencaAtividade().getTipoAtividade().getCodigo());
        certificadoLicencaAtividade.setNivelRisco(aplicante.getPedidoLicencaAtividade().getRisco());
        certificadoLicencaAtividade.setDataEmissao(LocalDate.now().toString());
        certificadoLicencaAtividade.setDataValidade(LocalDate.now().plusYears(2).toString());
        certificadoLicencaAtividade.setNomeDiretorGeral("Donald Trump");

        entityManager.persist(certificadoLicencaAtividade);
        return certificadoLicencaAtividade;
    }
}
