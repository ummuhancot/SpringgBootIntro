package com.tpe.service;

import com.tpe.domain.Role;
import com.tpe.domain.RoleType;
import com.tpe.domain.User;
import com.tpe.dto.UserDTO;
import com.tpe.exception.ConflictException;
import com.tpe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public void saveUser(UserDTO userDTO) {
        //username tabloda var mı
        boolean existsUser=userRepository.existsByUserName(userDTO.getUserName());
        if (existsUser){
            throw new ConflictException("User already exists!!!");
        }

        User user=new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setUserName(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        //userın passwordünü DB ye hashleyerek kaydedelim
        String encodedPassword=passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);

        ///userin rolünün verilmesi ---
        //uygulama başlatıldığında roller hazır olur.
        //main methodun icersinde rolleri oluşturup kaydet diyebiliriz.
        //main method icerisinde main method vardır ve uygulama calıştığında roller hazırdır.
        //uygulama da defaold olarak en düşük seviyede rol verilir
        Set<Role> roleSet=new HashSet<>();
        Role role= roleService.getRoleByType(RoleType.ROLE_STUDENT);
        roleSet.add(role);
        //NOT:genellikle default olarak usera en düşük yetkideki rol verilir
        //gerekirse daha sonra admin tarafından değiştirilir
        user.setRoles(roleSet);


        userRepository.save(user);

    }

}
