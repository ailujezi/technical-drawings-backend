package com.ritzjucy.technicaldrawingsbackend.entity.repository;

import com.ritzjucy.technicaldrawingsbackend.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepo extends JpaRepository<ImageEntity, Long>
{

}