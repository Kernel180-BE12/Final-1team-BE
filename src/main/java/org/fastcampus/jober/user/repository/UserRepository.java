package org.fastcampus.jober.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import org.fastcampus.jober.user.entity.Users;

public interface UserRepository extends CrudRepository<Users, Long> {
  Optional<Users> findByUsername(String username);

  Optional<Users> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
