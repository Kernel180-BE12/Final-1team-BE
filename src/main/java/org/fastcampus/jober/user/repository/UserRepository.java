package org.fastcampus.jober.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import org.fastcampus.jober.user.entity.Users;

public interface UserRepository extends CrudRepository<Users, Long> {
  Optional<Users> findByUsernameAndIsDeletedFalse(String username);

  Optional<Users> findByEmailAndIsDeletedFalse(String email);

  Optional<Users> findByUserIdAndIsDeletedFalse(Long userId);

  boolean existsByUsernameAndIsDeletedFalse(String username);

  boolean existsByEmailAndIsDeletedFalse(String email);
}
