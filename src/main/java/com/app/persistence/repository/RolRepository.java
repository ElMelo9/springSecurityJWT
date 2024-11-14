package com.app.persistence.repository;

import com.app.persistence.entity.RolEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolRepository extends CrudRepository<RolEntity,Long> {

    List<RolEntity> findRolByrolEnumIn(List<String>rolName);

}
