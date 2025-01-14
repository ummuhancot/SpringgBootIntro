package com.tpe.repository;

import com.tpe.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;

@Repository // burada bunu yamamiza gerek yok JpaRepository bize Bead olusturuyor. ama okunabilirlik icin yazilabilir opsiyonel
public interface StudentRepository extends JpaRepository<Student, Long> {
    //JpaRepositorydeki metodlar Spring tarafından otomatik olarak implemente edilir, bizim implemente etmemize gerek yok

    //5
    boolean existsByEmail(String email);
    //existsBy bu kok kelimeden sonra Email, Grade, Name, LastName gibi fieldlari sonuna ekleyerek bu sekilde override edebiliyoruz



    //burda spring kullanacagimiz icin SQL sorgularini direk yapabildigimiz icin burdan direk JpaRepository 'den miras alarak bu islemleri otomatik yapacagiz



}
