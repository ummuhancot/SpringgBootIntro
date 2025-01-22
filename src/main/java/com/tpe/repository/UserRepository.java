package com.tpe.repository;

import com.tpe.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    //doğrulama yapabilmek icin böyle bir user var mı kontrolü icin username ve password gönderilecek karşılaştırma yapmak icin kullanıcaz
    Optional<User> findByUserName(String username);
    //cilayt dan user name geldiğinde filtrelemeyi nasıl yaparız

}
