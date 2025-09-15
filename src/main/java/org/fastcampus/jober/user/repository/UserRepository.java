package org.fastcampus.jober.user.repository;

import org.fastcampus.jober.error.BusinessException;
import org.fastcampus.jober.error.ErrorCode;
import org.fastcampus.jober.user.entity.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Users, Long> {

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    default Users findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new BusinessException(
                ErrorCode.USER_NOT_FOUND,
                "존재하지 않는 사용자입니다"
        ));
    }

    default Users findByIdOrThrow(Long userId){
        return findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    };
}
