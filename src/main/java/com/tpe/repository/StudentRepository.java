package com.tpe.repository;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;

@Repository // burada bunu yamamiza gerek yok JpaRepository bize Bead olusturuyor. ama okunabilirlik icin yazilabilir opsiyonel
public interface StudentRepository extends JpaRepository<Student, Long> {
    //JpaRepositorydeki metodlar Spring tarafından otomatik olarak implemente edilir, bizim implemente etmemize gerek yok
    //burda spring kullanacagimiz icin SQL sorgularini direk yapabildigimiz icin burdan direk JpaRepository 'den miras alarak bu islemleri otomatik yapacagiz


    //5
    boolean existsByEmail(String email);
    //existsBy bu kok kelimeden sonra Email, Grade, Name, LastName gibi fieldlari sonuna ekleyerek bu sekilde override edebiliyoruz

    //15-a
    List<Student> findAllByGrade(Integer grade);//select * from Student where grade = 100

    //15-b
    //JPQL:javaca
    @Query("FROM Student WHERE grade=:pGrade")
    List<Student> filterStudentsByGrade(@Param("pGrade") Integer grade);//param metthodun parametresinde verilen değeri pGrade icine alır bunu Query de string olarak kullanabiliriz.
    //@Param methodun parametresinde verilen degeri pGrade icerisine alir
    //ve bu degiskeni sorgu icerisinde kullanabiliriz

    //15-c
    //SQL:value özelliği ile SQL ifadesi String olarak verilir ve nativeQuery
    //true olarak aktif edilir. doğal sorğu demek
    @Query(value = "SELECT * FROM student WHERE grade=:pGrade",nativeQuery = true)
    List<Student> filterStudentsByGradeSQL(@Param("pGrade") Integer grade);


    //18-c : JPQL ile tablodan gelen Entity objesini DTO nun constructorı
    //ile doğrudan DTO objesine dönüştürebiliriz.
    @Query("SELECT new com.tpe.dto.StudentDTO(s) FROM Student s WHERE s.id=:pId")//StudentDTO clasındaki constractor u cağırdık new com.tpe.dto.StudentDTO(s) kısmı
    Optional<StudentDTO> findStudentDtoById(@Param("pId") Long id);

    //16:ÖDEV
    List<Student> findAllByLastnameIgnoreCase(String lastName);

    //meraklısına ÖDEV :)
    //JPQL
    //SELECT * FROM Student WHERE name=:pWord OR lastname=:pWord1
    List<Student> findByNameOrLastname(String word, String word1);
    //and
    List<Student> findByNameAndLastname(String word, String word1);

    //Meraklısına:) ÖDEVVV:2
    List<Student> findByNameContainsIgnoreCase(String word);

    //Meraklısına:) ÖDEVVV:2
    List<Student> findByNameOrLastnameContainsIgnoreCase(String word, String word1);
}
