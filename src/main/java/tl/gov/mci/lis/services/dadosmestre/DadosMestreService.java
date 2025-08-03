package tl.gov.mci.lis.services.dadosmestre;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.repositories.cadastro.PedidoInscricaoCadastroRepository;

@Service
@RequiredArgsConstructor
public class DadosMestreService {
    private static final Logger logger = LoggerFactory.getLogger(DadosMestreService.class);
    private final PedidoInscricaoCadastroRepository pedidoInscricaoCadastroRepository;


//    public void deleteAtividadeEconomicaById(Long id) {
//        logger.info("Excluindo Atividade Economica pelo id: {}", id);
//
//        AtividadeEconomica atividadeEconomica = atividadeEconomicaRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Atividade Economica nao encontrada"));
//
//        if (
//                pedidoInscricaoCadastroRepository.countByTipoAtividade_Id(id) > 0
//                        ||
//                        pedidoInscricaoCadastroRepository.countByAtividadePrincipal_Id(id) > 0) {
//            throw new IllegalStateException("Não é possível eliminar este registo porque existem dados relacionados. Elimine ou remova as ligações antes de continuar.");
//        }
//        atividadeEconomicaRepository.delete(atividadeEconomica);
//    }
}
