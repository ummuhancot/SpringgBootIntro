package com.tpe.dto;

import com.tpe.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//dto databaseden 6 filt yerine 3 fielt yani data gelmesini sağlar
@Getter
@Setter
@AllArgsConstructor//zorunlu değil bi kolaylık icin ekledik
@NoArgsConstructor//zorunlu değil
public class StudentDTO {
    @NotBlank(message = "name can not be blank!")
    @Size(min = 2,max = 50,message = "name must be between 2 and 50")
    private String name;

    @NotBlank(message = "lastname can not be blank!")
    @Size(min = 2,max = 50,message = "lastname must be between 2 and 50")
    private String lastname;

    @NotNull(message = "please provide grade!")
    private Integer grade;

    //parametreli constructor
    public  StudentDTO(Student student){
        this.name=student.getName();
        this.lastname=student.getLastname();
        this.grade=student.getGrade();
    }

}
