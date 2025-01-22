package com.tpe.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Enumerated(EnumType.STRING)//enum sabitlerinin DB de String olarak kaydedilmesini sağlar
    //string olarak düşünebilirsin dedik.//ordinal dersek sayısal olarak saklayacağız demek istedik
    @Column(nullable = false)
    private RoleType type;//ROLE_STUDENT





}
