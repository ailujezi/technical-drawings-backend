package com.ritzjucy.technicaldrawingsbackend.entity.repository;

import com.ritzjucy.technicaldrawingsbackend.entity.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepo extends JpaRepository<ResultEntity, Long>
{
}