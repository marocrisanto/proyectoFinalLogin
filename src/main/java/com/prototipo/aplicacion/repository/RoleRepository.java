package com.prototipo.aplicacion.repository;


import com.prototipo.aplicacion.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    public Role findByName(String name);


}