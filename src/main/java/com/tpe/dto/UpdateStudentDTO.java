package com.tpe.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UpdateStudentDTO {

    @NotBlank(message = "name can not be blank!")
    @Size(min = 2,max = 50,message = "name must be between 2 and 50")
    private String name;

    @NotBlank(message = "lastname can not be blank!")
    @Size(min = 2,max = 50,message = "lastname must be between 2 and 50")
    private String lastname;

    @Email(message = "please provide valid email!")//aaa@bbb.ccc email formatında olmasını doğrulama
    private String email;
}