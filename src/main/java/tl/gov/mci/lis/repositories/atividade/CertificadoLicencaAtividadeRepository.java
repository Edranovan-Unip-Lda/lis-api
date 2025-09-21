package tl.gov.mci.lis.repositories.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;

import java.util.Optional;

@JaversSpringDataAuditable
public interface CertificadoLicencaAtividadeRepository extends JpaRepository<CertificadoLicencaAtividade, Long> {

    Optional<CertificadoLicencaAtividade> findByPedidoLicencaAtividade_Id(Long id);

    @Query("""
            SELECT cert FROM CertificadoLicencaAtividade cert
                        WHERE cert.id = :id
                        AND cert.pedidoLicencaAtividade.aplicante.categoria = :categoria
                        AND cert.pedidoLicencaAtividade.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Optional<CertificadoLicencaAtividade> findByIdAndAplicanteIdAndCategoria(Long id, Categoria categoria);

    @Query("""
            SELECT cert FROM CertificadoLicencaAtividade cert
            WHERE cert.pedidoLicencaAtividade.aplicante.categoria = :categoria
            AND cert.pedidoLicencaAtividade.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Page<CertificadoLicencaAtividade> findApprovedByCategoria(Categoria categoria, Pageable pageable);

    @Query("""
            SELECT cert FROM CertificadoLicencaAtividade cert
            WHERE cert.pedidoLicencaAtividade.aplicante.empresa.id = :empresaId
            AND cert.pedidoLicencaAtividade.aplicante.categoria = :categoria
            AND cert.pedidoLicencaAtividade.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Page<CertificadoLicencaAtividade> findApprovedByEmpresaIdAndCategoria(Long empresaId, Categoria categoria, Pageable pageable);
}