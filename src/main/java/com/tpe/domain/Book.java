package com.tpe.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Book {//bir öğrencinin birden fazla kitabı olabilir //MANY

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @JsonProperty("bookName")
    /*@JsonProperty("bookName") anotasyonu, JSON serileştirme ve serileştirme işlemleri
    sırasında name alanının JSON'da bookName olarak gösterilmesini sağlar. Bu, JSON çıktısında
    veya girdisinde alan adını özelleştirmek için kullanılır.
    sadece JSON formatında bu fieldın isminin belirtilen şekilde gösterilmesini sağlar.
    */
    private String name;

    @ManyToOne
    @JsonIgnore//bu fieldı JSON formatında ignore et(görmezden gel)
    /*@JsonIgnore anotasyonu, JSON serileştirme ve serileştirme işlemleri sırasında student
    alanının JSON formatında görmezden gelinmesini sağlar. Bu, bir Book nesnesi JSON'a
    dönüştürüldüğünde student alanının JSON çıktısında yer almayacağı anlamına gelir.
    Benzer şekilde, JSON'dan bir Book nesnesi oluşturulurken de student alanı dikkate alınmaz.
     */
    private Student student;
    //kitabı kaydederken bu kitap hangi öğrenciye ait
    //bu öğrenciyi bulup set etmeliyiz
}
