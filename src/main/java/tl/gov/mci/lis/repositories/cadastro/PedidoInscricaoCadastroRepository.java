package tl.gov.mci.lis.repositories.cadastro;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;

import java.util.Optional;

@JaversSpringDataAuditable
public interface PedidoInscricaoCadastroRepository extends JpaRepository<PedidoInscricaoCadastro, Long> {

//    @Query("SELECT " +
//            "new PedidoInscricaoCadastro (p.id, p.tipoPedido, p.nomeEmpresa, p.nif, p.gerente, p.numeroRegistoComercial, p.email, p.telefone, p.telemovel, p.sede.id, p.categoria, p.tipoEmpresa, p.nomeEstabelecimento,p.localEstabelecimento,p.tipoEstabelecimento,p.caraterizacaoEstabelecimento,p.risco,p.ato,p.tipoAtividade,p.tipoAtividadeCodigo,p.atividadePrincipal,p.atividadePrincipalCodigo,p.alteracoes,p.dataEmissaoCertAnterior,p.observacao)" +
//            "FROM PedidoInscricaoCadastro p WHERE p.aplicante.id = ?1")
//    PedidoInscricaoCadastro getByAplicante_Id(Long id);

    Optional<PedidoInscricaoCadastro> findByAplicante_Id(Long applicanteId);

    Optional<PedidoInscricaoCadastro> findByIdAndAplicante_Id(Long id, Long applicanteId);

    int countByTipoAtividade_Id(Long id);

    int countByAtividadePrincipal_Id(Long id);
}