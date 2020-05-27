package com.gruppo42.restapi.repository;

import com.gruppo42.restapi.models.Role;
import com.gruppo42.restapi.models.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
    Optional<Role> findByName(RoleName roleName);
}