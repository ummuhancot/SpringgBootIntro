package com.tpe.repository;

import com.tpe.domain.Role;
import com.tpe.domain.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByType(RoleType type);//kayıt aşamasında türü verilen bir rolü bulmak icin yazılmış.

}
