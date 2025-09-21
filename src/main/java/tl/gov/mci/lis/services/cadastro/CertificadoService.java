package tl.gov.mci.lis.services.cadastro;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.mappers.CertificadoMapper;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.atividade.CertificadoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.cadastro.CertificadoInscricaoCadastroRepository;
import tl.gov.mci.lis.services.endereco.EnderecoService;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificadoService {
    private static final Logger logger = LoggerFactory.getLogger(CertificadoService.class);
    private final EntityManager entityManager;
    private final EnderecoService enderecoService;
    private final CertificadoInscricaoCadastroRepository certificadoInscricaoCadastroRepository;
    private final CertificadoMapper certificadoMapper;
    private final CertificadoLicencaAtividadeRepository certificadoLicencaAtividadeRepository;

    public Optional<?> findById(Long id, AplicanteType aplicanteType, Categoria categoria) {
        logger.info("Buscando certificado pelo id: {}", id);
        switch (aplicanteType) {
            case CADASTRO -> {
                return certificadoInscricaoCadastroRepository.findByIdAndAplicanteIdAndCategoria(id, categoria).map(certificadoMapper::toDto1);
            }
            case ATIVIDADE -> {
                return certificadoLicencaAtividadeRepository.findByIdAndAplicanteIdAndCategoria(id, categoria).map(certificadoMapper::toDto1);
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    public Optional<?> findByIdAndType(Long id, AplicanteType aplicanteType) {
        switch (aplicanteType) {
            case CADASTRO -> {
                logger.info("Buscando certificado de inscricao pelo id: {}", id);
                return certificadoInscricaoCadastroRepository.findById(id).map(certificadoMapper::toDto1);
            }
            case ATIVIDADE -> {
                logger.info("Buscando Licenca de atividade pelo id: {}", id);
                return certificadoLicencaAtividadeRepository.findById(id).map(certificadoMapper::toDto1);
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<?> findPageApprovedCertificados(AplicanteType aplicanteType, Categoria categoria, int page, int size) {
        logger.info("Buscando certificados de inscricao pelo categoria: {}", categoria);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        switch (aplicanteType) {
            case CADASTRO -> {
                return certificadoInscricaoCadastroRepository.findApprovedByCategoria(categoria, paging).map(certificadoMapper::toDto1);
            }
            case ATIVIDADE -> {
                return certificadoLicencaAtividadeRepository.findApprovedByCategoria(categoria, paging).map(certificadoMapper::toDto1);
            }
            default -> {
                return null;
            }
        }
    }

    @Transactional
    public CertificadoInscricaoCadastro saveCertificadoInscricaoCadastro(Aplicante aplicante, User diretor) {
        logger.info("Salvando certificado de inscricao: {}", aplicante.getId());

        Endereco sede = new Endereco();
        Endereco sedePedido = aplicante.getPedidoInscricaoCadastro().getEmpresaSede();
        sede.setLocal(sedePedido.getLocal());
        sede.setAldeia(sedePedido.getAldeia());

        sede = enderecoService.create(sede);

        String nomeDiretor = diretor.getFirstName() + " " + diretor.getLastName();

        // Emitir certificado
        CertificadoInscricaoCadastro certificadoInscricaoCadastro = new CertificadoInscricaoCadastro();
        certificadoInscricaoCadastro.setPedidoInscricaoCadastro(aplicante.getPedidoInscricaoCadastro());
        certificadoInscricaoCadastro.setSociedadeComercial(aplicante.getEmpresa().getNome());
        certificadoInscricaoCadastro.setNumeroRegistoComercial(aplicante.getEmpresa().getNumeroRegistoComercial());
        certificadoInscricaoCadastro.setSede(sede);
        certificadoInscricaoCadastro.setAtividade(aplicante.getPedidoInscricaoCadastro().getClasseAtividade().getDescricao());
        certificadoInscricaoCadastro.setDataEmissao(LocalDate.now().toString());
        certificadoInscricaoCadastro.setDataValidade(LocalDate.now().plusYears(2).toString());
        certificadoInscricaoCadastro.setNomeDiretorGeral(nomeDiretor);

        entityManager.persist(certificadoInscricaoCadastro);
        return certificadoInscricaoCadastro;
    }

    @Transactional
    public CertificadoLicencaAtividade saveCertificadoLicencaAtividade(Aplicante aplicante, User diretor) {
        logger.info("Salvando certificado de licenca de atividade: {}", aplicante.getId());

        Endereco sede = new Endereco();
        Endereco sedePedido = aplicante.getPedidoLicencaAtividade().getEmpresaSede();
        sede.setLocal(sedePedido.getLocal());
        sede.setAldeia(sedePedido.getAldeia());

        sede = enderecoService.create(sede);

        String nomeDiretor = diretor.getFirstName() + " " + diretor.getLastName();

        // Emitir certificado
        CertificadoLicencaAtividade certificadoLicencaAtividade = new CertificadoLicencaAtividade();
        certificadoLicencaAtividade.setPedidoLicencaAtividade(aplicante.getPedidoLicencaAtividade());
        certificadoLicencaAtividade.setSociedadeComercial(aplicante.getEmpresa().getNome());
        certificadoLicencaAtividade.setNumeroRegistoComercial(aplicante.getEmpresa().getNumeroRegistoComercial());
        certificadoLicencaAtividade.setSede(sede);
        certificadoLicencaAtividade.setAtividade(aplicante.getPedidoLicencaAtividade().getTipoAtividade().getDescricao());
        certificadoLicencaAtividade.setAtividadeCodigo(aplicante.getPedidoLicencaAtividade().getTipoAtividade().getCodigo());
        certificadoLicencaAtividade.setNivelRisco(aplicante.getPedidoLicencaAtividade().getRisco());
        certificadoLicencaAtividade.setDataEmissao(LocalDate.now().toString());
        certificadoLicencaAtividade.setDataValidade(LocalDate.now().plusYears(2).toString());
        certificadoLicencaAtividade.setNomeDiretorGeral(nomeDiretor);

        entityManager.persist(certificadoLicencaAtividade);
        return certificadoLicencaAtividade;
    }
}
