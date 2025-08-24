package org.macnigor.contenthub.repositories;

import org.macnigor.contenthub.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository< User,Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User>findUserByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User>findUserByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User>findUserById(Long id);


}
