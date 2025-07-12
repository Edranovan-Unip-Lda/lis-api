package tl.gov.mci.lis.services.endereco;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.repositories.endereco.AldeiaRepository;
import tl.gov.mci.lis.repositories.endereco.EnderecoRepository;

@Service
@RequiredArgsConstructor
public class EnderecoService {
    private static final Logger logger = LoggerFactory.getLogger(EnderecoService.class);
    private final EnderecoRepository enderecoRepository;
    private final AldeiaRepository aldeiaRepository;

    public Endereco create(Endereco obj) throws BadRequestException {
        logger.info("Criando endereco: {}", obj);
        if (obj.getAldeia() == null || obj.getAldeia().getId() == null) {
            throw new BadRequestException("ID da Aldeia é obrigatório");
        }
        obj.setAldeia(aldeiaRepository.getReferenceById(obj.getAldeia().getId()));
        return enderecoRepository.save(obj);
    }

    public Endereco update(Endereco obj) throws BadRequestException {
        logger.info("Atualizando endereco: {}", obj);
        Endereco endereco = enderecoRepository.findById(obj.getId()).orElseThrow(() -> new ResourceNotFoundException("Endereco não encontrado"));
        if (obj.getAldeia() == null || obj.getAldeia().getId() == null) {
            throw new BadRequestException("ID da Aldeia é obrigatório");
        }
        endereco.setAldeia(aldeiaRepository.getReferenceById(obj.getAldeia().getId()));
        endereco.setLocal(obj.getLocal());
        return enderecoRepository.save(endereco);
    }
}
