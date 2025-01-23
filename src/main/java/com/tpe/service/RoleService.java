package com.tpe.service;

import com.tpe.domain.Role;
import com.tpe.domain.RoleType;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    ///userin rolünün verilmesi ---
    //type ı rverildiğinde DB gidip bulmamız gerekiyor
    public Role getRoleByType(RoleType type){
        Role role = repository.findByType(type).
                orElseThrow(()->new ResourceNotFoundException("Role is not found!!!"));
        return role;
    }







}
