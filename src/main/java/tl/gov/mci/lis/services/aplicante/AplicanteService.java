package tl.gov.mci.lis.services.aplicante;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;


@Service
@RequiredArgsConstructor
public class AplicanteService {
    private static final Logger logger = LoggerFactory.getLogger(AplicanteService.class);
    private final AplicanteRepository aplicanteRepository;



    public Page<Aplicante> getPageByPageAndSize(int page, int size) {
        logger.info("Getting page: {} and size {}", page, size);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        return aplicanteRepository.getPageBy(paging);
    }

    public Aplicante getById(Long id) {
        logger.info("Getting aplicante by id: {}", id);
        return aplicanteRepository.getReferenceById(id);
    }

}
