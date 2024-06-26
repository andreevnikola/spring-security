package com.andreev.security.user.repositories.authentication;

import com.andreev.security.user.domain.authentication.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUuid(String uuid);

    @Transactional
    void deleteByEmail(String email);
}
