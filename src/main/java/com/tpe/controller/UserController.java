package com.tpe.controller;

import com.tpe.dto.UserDTO;
import com.tpe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //useri kaydetme:register
    //request:http://localhost:8080/register + POST + body
    //response:mesaj + 201
    @RequestMapping("/register")
    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO){
        userService.saveUser(userDTO);
        return new ResponseEntity<>("User is registered succesfully...", HttpStatus.CREATED);
    }



}
