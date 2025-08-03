package tl.gov.mci.lis.controllers.dadosmestre;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tl.gov.mci.lis.dtos.atividade.ClasseAtividadeDto;
import tl.gov.mci.lis.dtos.endereco.AldeiaDto;
import tl.gov.mci.lis.dtos.endereco.PostoAdministrativoDto;
import tl.gov.mci.lis.dtos.endereco.SucoDto;
import tl.gov.mci.lis.dtos.mappers.AldeiaMapper;
import tl.gov.mci.lis.dtos.mappers.AtividadeEconomicaMapper;
import tl.gov.mci.lis.dtos.mappers.PostoAdministrativoMapper;
import tl.gov.mci.lis.dtos.mappers.SucoMapper;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.endereco.Aldeia;
import tl.gov.mci.lis.models.endereco.PostoAdministrativo;
import tl.gov.mci.lis.models.endereco.Suco;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.ClasseAtividadeRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.GrupoAtividadeRepository;
import tl.gov.mci.lis.repositories.endereco.AldeiaRepository;
import tl.gov.mci.lis.repositories.endereco.MunicipioRepository;
import tl.gov.mci.lis.repositories.endereco.PostoAdministrativoRepository;
import tl.gov.mci.lis.repositories.endereco.SucoRepository;
import tl.gov.mci.lis.services.dadosmestre.DadosMestreService;

import java.util.Objects;

@RepositoryRestController
@RequiredArgsConstructor
public class DadosMestreController {
    private final MunicipioRepository municipioRepository;
    private final PostoAdministrativoRepository postoAdministrativoRepository;
    private final SucoRepository sucoRepository;
    private final AldeiaRepository aldeiaRepository;
    private final PostoAdministrativoMapper postoAdministrativoMapper;
    private final SucoMapper sucoMapper;
    private final AldeiaMapper aldeiaMapper;
    private final AtividadeEconomicaMapper atividadeEconomicaMapper;
    private final DadosMestreService dadosMestreService;
    private final GrupoAtividadeRepository grupoAtividadeRepository;
    private final ClasseAtividadeRepository classeAtividadeRepository;

    @PostMapping("/postos")
    public ResponseEntity<PostoAdministrativoDto> registoPostoAdministrativo(@Valid @RequestBody PostoAdministrativo obj) {
        if (obj.getMunicipio() == null || obj.getMunicipio().getId() == null) {
            throw new ResourceNotFoundException("ID do Municipio é obrigatório");
        }
        obj.setMunicipio(
                municipioRepository.findById(obj.getMunicipio().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Municipio com ID " + obj.getMunicipio().getId() + " não encontrado"))
        );
        return new ResponseEntity<>(postoAdministrativoMapper.toDto(postoAdministrativoRepository.save(obj)), HttpStatus.CREATED);
    }

    @PostMapping("/sucos")
    public ResponseEntity<SucoDto> registoSuco(@Valid @RequestBody Suco obj) {
        if (obj.getPostoAdministrativo() == null || obj.getPostoAdministrativo().getId() == null) {
            throw new ResourceNotFoundException("ID do Posto é obrigatório");
        }
        obj.setPostoAdministrativo(
                postoAdministrativoRepository.findById(obj.getPostoAdministrativo().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Posto com id " + obj.getPostoAdministrativo().getId() + " não encontrado"))
        );
        return new ResponseEntity<>(sucoMapper.toDto(sucoRepository.save(obj)), HttpStatus.CREATED);
    }

    @PostMapping("/aldeias")
    public ResponseEntity<AldeiaDto> registoAldeia(@Valid @RequestBody Aldeia obj) {
        if (obj.getSuco() == null || obj.getSuco().getId() == null) {
            throw new ResourceNotFoundException("ID do Suco é obrigatório");
        }
        obj.setSuco(
                sucoRepository.findById(obj.getSuco().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Suco com id " + obj.getSuco().getId() + " não encontrado"))
        );
        return new ResponseEntity<>(aldeiaMapper.toDto(aldeiaRepository.save(obj)), HttpStatus.CREATED);
    }

    @PostMapping("/classe-atividades")
    public ResponseEntity<ClasseAtividadeDto> registoClasseAtividade(@Valid @RequestBody ClasseAtividade obj) {
        if (obj.getGrupoAtividade() == null || Objects.isNull(obj.getGrupoAtividade().getId())) {
            throw new ResourceNotFoundException("ID do Grupo Atividade é obrigatório");
        }
        obj.setGrupoAtividade(
                grupoAtividadeRepository.getReferenceById(obj.getGrupoAtividade().getId())
        );
        return new ResponseEntity<>(atividadeEconomicaMapper.toDto(classeAtividadeRepository.save(obj)), HttpStatus.CREATED);
    }
}
