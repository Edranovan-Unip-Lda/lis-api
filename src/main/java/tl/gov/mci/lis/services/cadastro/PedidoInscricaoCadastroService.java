package tl.gov.mci.lis.services.cadastro;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.mappers.PedidoInscricaoCadastroMapper;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.repositories.cadastro.PedidoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.endereco.EnderecoRepository;

@Service
@RequiredArgsConstructor
public class PedidoInscricaoCadastroService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoInscricaoCadastroService.class);
    private final PedidoInscricaoCadastroRepository pedidoInscricaoCadastroRepository;
    private final EnderecoRepository enderecoRepository;
    private final PedidoInscricaoCadastroMapper pedidoInscricaoCadastroMapper;

    public PedidoInscricaoCadastro getById(Long id) {
        logger.info("Obtendo pedido de inscricao pelo id: {}", id);
        PedidoInscricaoCadastro obj = pedidoInscricaoCadastroRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido de inscricao nao encontrado"));
        obj.setSede(enderecoRepository.getFromId(obj.getSede().getId()));
        return obj;
    }

    public PedidoInscricaoCadastroDto getByAplicanteId(Long aplicanteId) {
        logger.info("Getting Inscrição for Aplicante id: {}", aplicanteId);

        return pedidoInscricaoCadastroRepository
                .findByAplicante_Id(aplicanteId)
                .map(obj -> {
                    obj.setSede(enderecoRepository.getFromId(obj.getSede().getId()));
                    return pedidoInscricaoCadastroMapper.toDto(obj);
                })
                .orElse(null);  // ← return null when no record exists
    }
}
