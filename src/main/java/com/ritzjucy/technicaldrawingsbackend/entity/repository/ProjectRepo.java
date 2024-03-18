package com.ritzjucy.technicaldrawingsbackend.entity.repository;

import com.ritzjucy.technicaldrawingsbackend.entity.ProjectEntity;
import com.ritzjucy.technicaldrawingsbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepo extends JpaRepository<ProjectEntity, Long>
{
    List<ProjectEntity> findAllByUser(UserEntity user);

}