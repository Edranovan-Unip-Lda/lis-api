package tl.gov.mci.lis.services.atividade;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.dtos.mappers.LicencaMapper;
import tl.gov.mci.lis.repositories.atividade.PedidoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.endereco.EnderecoRepository;

@Service
@RequiredArgsConstructor
public class PedidoLicencaAtividadeService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoLicencaAtividadeService.class);
    private final PedidoLicencaAtividadeRepository pedidoLicencaAtividadeRepository;
    private final EnderecoRepository enderecoRepository;
    private final LicencaMapper licencaMapper;



}
