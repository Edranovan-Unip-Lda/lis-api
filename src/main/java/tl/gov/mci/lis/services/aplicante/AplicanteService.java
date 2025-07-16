package tl.gov.mci.lis.services.aplicante;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.AplicantePageDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.mappers.AplicanteMapper;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.AplicanteNumber;
import tl.gov.mci.lis.repositories.aplicante.AplicanteNumberRepository;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class AplicanteService {
    private static final Logger logger = LoggerFactory.getLogger(AplicanteService.class);
    private static final String PREFIX = "MCI";
    private final AplicanteRepository aplicanteRepository;
    private final AplicanteMapper aplicanteMapper;
    private final AplicanteNumberRepository repository;


    public Page<AplicantePageDto> getPage(int page, int size) {
        logger.info("Getting page: {} and size {}", page, size);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Aplicante> aplicantes = aplicanteRepository.findAll(paging);
        return aplicantes.map(aplicanteMapper::toDto1);
    }

    public AplicanteDto getById(Long id) {
        logger.info("Obtendo aplicante pelo id: {}", id);
        return aplicanteRepository.getFromId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized String generateAplicanteNumber(String categoriaCode) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        // Count applications for this department, month, and year
        int count = repository.countByCategoriaCodeAndMonthAndYear(categoriaCode, month, year);
        int serial = count + 1; // Start from 1
        String formattedId = String.format("%04d", serial);

        // Format full application code
        String finalCode = String.format("%s/%s/%02d/%d/%s", PREFIX, categoriaCode, month, year, formattedId);

        // Save new entry
        AplicanteNumber record = new AplicanteNumber();
        record.setCategoriaCode(categoriaCode);
        record.setMonth(month);
        record.setYear(year);
        record.setFormattedCode(finalCode);

        repository.save(record);

        return finalCode;
    }

}
