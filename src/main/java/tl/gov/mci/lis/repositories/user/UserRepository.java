package tl.gov.mci.lis.repositories.user;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.user.User;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
              select u
              from User u
              left join fetch u.role
              left join fetch u.direcao
              where u.username = :username
            """)
    Optional<User> findByUsername(String username);

    @Query("""
              select u
              from User u
              left join fetch u.role
              left join fetch u.direcao
              where u.email = :email
            """)
    Optional<User> findByEmail(String email);

    @Query("""
              select u
              from User u
              left join fetch u.role
              left join fetch u.direcao
              where u.username = :username or u.email = :email
            """)
    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.empresa LEFT JOIN FETCH u.role WHERE u.username = :username")
    Optional<User> findUserByUsername(String username);

    @Query("SELECT new User(u.id, u.firstName, u.lastName, u.username, u.email, u.role.id, u.role.name, u.jwtSession, u.status, u.createdAt, u.createdBy) FROM User u")
    Page<User> getPageBy(Pageable pageable);

    @Query("SELECT u.direcao FROM User u WHERE u.username = :username")
    Optional<Direcao> findDirecaoIdByUsername(@Param("username") String username);

    Optional<User> queryByUsername(String username);

    @RestResource(path = "byDirecao", rel = "byDirecao")
    List<User> findByDirecao_Id(Long direcaoId);

    @RestResource(path = "byDirecaoAndRole", rel = "byDirecaoAndRole")
    List<User> findByDirecao_IdAndRole_Name(Long direcaoId, String roleName);

    @Query("SELECT u FROM User u  JOIN FETCH u.role WHERE u.role.name = :roleName AND u.direcao.nome = :direcaoNome AND u.status = :status")
    Optional<User> findByRole_NameAndDirecao_NomeAndStatusActive(String roleName, Categoria direcaoNome, String status);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.id = :id")
    Optional<User> findByIdWithRole(@Param("id") Long id);

}