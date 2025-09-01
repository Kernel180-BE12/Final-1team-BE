package org.fastcampus.jober.user.repository;

import org.fastcampus.jober.user.entity.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
}
