package com.tpe.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false,length = 50)
    private String firstName;

    @Column(nullable = false,length = 50)
    private String lastName;

    @Column(nullable = false,length = 50,unique = true)
    private String userName;

    @Column(nullable = false,length = 50)
    private String password;
    //password DB ye kaydedilmeden önce şifrelenecek(hashlenecek)

    @ManyToMany//bir kullanıcının birden fazla rolu olacak
    private Set<Role> role = new HashSet<>();//setler benzersiz dataları saklamayı sağlıyor.

    @Column(nullable = false,length = 255)//maxda kısıtlama yapmicaz cünkü hashlicaz db kaydedilmeden önce
    private String email;

}
