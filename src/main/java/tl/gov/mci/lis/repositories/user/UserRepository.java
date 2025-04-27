package tl.gov.mci.lis.repositories.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new User(u.id, u.firstName, u.lastName, u.username, u.email, u.password, u.role.id, u.role.name, u.jwtSession, u.status) FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("SELECT new User(u.id, u.firstName, u.lastName, u.username, u.email, u.password, u.role.id, u.role.name, u.jwtSession, u.status) FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "role")
    Optional<User> findUserByUsername(String username);

    @Query("SELECT new User(u.id, u.firstName, u.lastName, u.username, u.email, u.role.id, u.role.name, u.jwtSession, u.status) FROM User u")
    Page<User> getPageBy(Pageable pageable);
}