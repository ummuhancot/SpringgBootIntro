package com.tpe.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Getter //tüm fieldlar için getter metodunun tanımlanmasını sağlar
@Setter  //tüm fieldlar için setter metodunun tanımlanmasını sağlar
@AllArgsConstructor //tum field'larin parametrede verildigi const metodunu tanimlar,
@NoArgsConstructor // parametresiz contructor methodunu tanimlar

//@RequiredArgsConstructor // bu annatation'i @NoArgsConstructor ile kullanamassin,  burada bu annatation'i ogrenmek icin baktik simdilik bunu kullanmayacagiz
//-----------
//objeyi const ederken final olan zorunlu degerleri vercegiz ama bunu @RequiredArgsConstructor annatation ile cozuyoruz
/*public Student(String name, String lastname) {
    this.name = name;
    this.lastname = lastname;
}*/
//----------------
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // setter gibi methodlarin olusturulmamasini istedigimz field'larin uzerine bu sekilde not duserek setter methodunun olusturulmasini engelleyebiliriz
    private Long id;

    @NotBlank(message = "name can not be blank!!") // bu kontrolu db'ye gitmeden direk girdi asamasinda kontrol ediyoruz
    @Size(min = 2, max = 50, message = "name must be between 2 and 50") //karakter sayisi ile sinirlama
    @Column(nullable = false) // bu kayit asamasinda uyari verir
    /*final*/ private String name;

    @NotBlank(message = "lastname can not be blank!!")
    @Size(min = 2, max = 50, message = "lastname must be between 2 and 50")
    @Column(nullable = false)
    /*final*/ private String lastname;

    //@NotBlank(message = "grade can not be blank!!")
    @Column(nullable = false)
    private Integer grade;

    @Email(message = "please provide valid email!!") //email: belirli bir formatta olmali --- aaa@ccc email @Email annatation formatinda olup olmadigini kontrol eder
    // @Pattern() // email'in sadece belirli bir duzende olmasini istiyorsak bunu kullanabiliriz--> regex ile format kisitlamasi yapilabilir
    @Column(nullable = false, unique = true) //bos olmasin ve benzersin olsun
    private String email;

    @Setter(AccessLevel.NONE) // burdada LocalDateTime filetindan setter methodunun olusmasi isnini kapatmis olduk
    private LocalDateTime createDate=LocalDateTime.now();

    //getter-setter

}
