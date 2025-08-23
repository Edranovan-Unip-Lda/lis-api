package tl.gov.mci.lis.repositories.documento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.documento.Documento;

import java.util.Optional;

@JaversSpringDataAuditable
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Optional<Documento> findByIdAndFatura_IdAndFatura_PedidoInscricaoCadastro_IdAndFatura_PedidoInscricaoCadastro_Aplicante_Id(Long id, Long faturaId, Long pedidoId, Long aplicanteId);

    Optional<Documento> findByIdAndFatura_IdAndFatura_PedidoLicencaAtividade_IdAndFatura_PedidoLicencaAtividade_Aplicante_Id(Long id, Long faturaId, Long pedidoId, Long aplicanteId);
}