package com.ritzjucy.technicaldrawingsbackend.entity.repository;

import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long>
{
    Optional<UserEntity> findByUsername(String username);

}