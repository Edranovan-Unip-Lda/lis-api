package tl.gov.mci.lis.services.cadastro;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.cadastro.PedidoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.endereco.EnderecoRepository;

@Service
@RequiredArgsConstructor
public class PedidoInscricaoCadastroService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoInscricaoCadastroService.class);
    private final PedidoInscricaoCadastroRepository pedidoInscricaoCadastroRepository;
    private final AplicanteRepository aplicanteRepository;
    private final EnderecoRepository enderecoRepository;

    public PedidoInscricaoCadastro create(PedidoInscricaoCadastro obj) {
        logger.info("Criando pedido de inscricao: {}", obj);
        obj.setAplicante(aplicanteRepository.getReferenceById(obj.getAplicante().getId()));
        return pedidoInscricaoCadastroRepository.save(obj);
    }

    public PedidoInscricaoCadastro getById(Long id) {
        logger.info("Obtendo pedido de inscricao by id: {}", id);
        PedidoInscricaoCadastro obj = pedidoInscricaoCadastroRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido de inscricao nao encontrado"));
        obj.setSede(enderecoRepository.getFromId(obj.getSede().getId()));
        return obj;
    }
}
