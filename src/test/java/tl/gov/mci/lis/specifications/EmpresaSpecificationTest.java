package tl.gov.mci.lis.specifications;

import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import tl.gov.mci.lis.dtos.report.EmpresaReportFilter;
import tl.gov.mci.lis.models.empresa.Empresa;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaSpecificationTest {

    @Mock
    private Root<Empresa> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<Object> path;

    @Mock
    private Path<Object> aldeia;

    @Mock
    private Path<Object> suco;

    @Mock
    private Path<Object> postoAdministrativo;

    @Mock
    private Path<Object> municipio;

    @Mock
    private Predicate predicate;

    @BeforeEach
    void setUp() {
        // Setup for sede path navigation
        lenient().when(root.get("sede")).thenReturn(path);
        lenient().when(path.get("aldeia")).thenReturn(aldeia);
        lenient().when(aldeia.get("suco")).thenReturn(suco);
        lenient().when(suco.get("postoAdministrativo")).thenReturn(postoAdministrativo);
        lenient().when(postoAdministrativo.get("municipio")).thenReturn(municipio);
        lenient().when(suco.get("id")).thenReturn(path);
        lenient().when(postoAdministrativo.get("id")).thenReturn(path);
        lenient().when(municipio.get("id")).thenReturn(path);
        lenient().when(criteriaBuilder.equal(any(), anyLong())).thenReturn(predicate);
        lenient().when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(predicate);
    }

    @Test
    void withFilter_emptyFilter_returnsNoFilters() {
        EmpresaReportFilter filter = new EmpresaReportFilter();
        Specification<Empresa> spec = EmpresaSpecification.withFilter(filter);

        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertNotNull(result);
        verify(criteriaBuilder).and(any(Predicate[].class));
    }

    @Test
    void withFilter_municipioIdProvided_addsMunicipioFilter() {
        EmpresaReportFilter filter = new EmpresaReportFilter();
        filter.setMunicipioId(1L);

        Specification<Empresa> spec = EmpresaSpecification.withFilter(filter);
        spec.toPredicate(root, query, criteriaBuilder);

        verify(root).get("sede");
        verify(path).get("aldeia");
        verify(aldeia).get("suco");
        verify(suco).get("postoAdministrativo");
        verify(postoAdministrativo).get("municipio");
        verify(municipio).get("id");
        verify(criteriaBuilder).equal(any(), eq(1L));
    }

    @Test
    void withFilter_postoAdministrativoIdProvided_addsPostoFilter() {
        EmpresaReportFilter filter = new EmpresaReportFilter();
        filter.setPostoAdministrativoId(2L);

        Specification<Empresa> spec = EmpresaSpecification.withFilter(filter);
        spec.toPredicate(root, query, criteriaBuilder);

        verify(root).get("sede");
        verify(path).get("aldeia");
        verify(aldeia).get("suco");
        verify(suco).get("postoAdministrativo");
        verify(postoAdministrativo).get("id");
        verify(criteriaBuilder).equal(any(), eq(2L));
    }

    @Test
    void withFilter_sucoIdProvided_addsSucoFilter() {
        EmpresaReportFilter filter = new EmpresaReportFilter();
        filter.setSucoId(3L);

        Specification<Empresa> spec = EmpresaSpecification.withFilter(filter);
        spec.toPredicate(root, query, criteriaBuilder);

        verify(root).get("sede");
        verify(path).get("aldeia");
        verify(aldeia).get("suco");
        verify(suco).get("id");
        verify(criteriaBuilder).equal(any(), eq(3L));
    }

    @Test
    void withFilter_allEnderecoFiltersProvided_addsAllFilters() {
        EmpresaReportFilter filter = new EmpresaReportFilter();
        filter.setMunicipioId(1L);
        filter.setPostoAdministrativoId(2L);
        filter.setSucoId(3L);

        Specification<Empresa> spec = EmpresaSpecification.withFilter(filter);
        spec.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).equal(any(), eq(1L));
        verify(criteriaBuilder).equal(any(), eq(2L));
        verify(criteriaBuilder).equal(any(), eq(3L));
    }

    @Test
    void withFilter_municipioAndPostoProvided_addsBothFilters() {
        EmpresaReportFilter filter = new EmpresaReportFilter();
        filter.setMunicipioId(1L);
        filter.setPostoAdministrativoId(2L);

        Specification<Empresa> spec = EmpresaSpecification.withFilter(filter);
        spec.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).equal(any(), eq(1L));
        verify(criteriaBuilder).equal(any(), eq(2L));
    }
}
