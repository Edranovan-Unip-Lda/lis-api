package tl.gov.mci.lis.repositories.cadastro;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;

import java.util.Optional;

@JaversSpringDataAuditable
public interface CertificadoInscricaoCadastroRepository extends JpaRepository<CertificadoInscricaoCadastro, Long> {

    Optional<CertificadoInscricaoCadastro> findByPedidoInscricaoCadastro_Id(Long id);

    @Query("""
            SELECT cert FROM CertificadoInscricaoCadastro cert
                        WHERE cert.id = :id
                        AND cert.pedidoInscricaoCadastro.aplicante.categoria = :categoria
                        AND cert.pedidoInscricaoCadastro.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Optional<CertificadoInscricaoCadastro> findByIdAndAplicanteIdAndCategoria(Long id, Categoria categoria);

    @Query("""
            SELECT cert FROM CertificadoInscricaoCadastro cert
                        WHERE cert.pedidoInscricaoCadastro.aplicante.categoria = :categoria
                        AND cert.pedidoInscricaoCadastro.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Page<CertificadoInscricaoCadastro> findApprovedByCategoria(Categoria categoria, Pageable pageable);

    @Query("""
            SELECT cert FROM CertificadoInscricaoCadastro cert
                        WHERE cert.pedidoInscricaoCadastro.aplicante.categoria = :categoria
                        AND cert.pedidoInscricaoCadastro.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
                        AND cert.pedidoInscricaoCadastro.aplicante.empresa.id = :empresaId
            """)
    Page<CertificadoInscricaoCadastro> findApprovedByEmpresaIdAndCategoria(@Param("empresaId") Long empresaId, @Param("categoria") Categoria categoria, Pageable pageable);
}