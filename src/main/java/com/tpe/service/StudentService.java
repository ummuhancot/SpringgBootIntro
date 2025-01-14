package com.tpe.service;

import com.tpe.domain.Student;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

//normalde interfaceden yararlanilir fakat simdilik kolay olsun diye direk biz olusturacagiz
@Service
@RequiredArgsConstructor
public class StudentService {

    @Autowired
    private StudentRepository repository;

    //2-tablodan tum kayitlari getirme
    public List<Student> findAllStudents() {
        return repository.findAll();
    }

    //4
    public void saveStudent(@Valid Student student) {
        //student ayni email ile daha once tabloya eklenmis mi? kontrol etmemiz gerekiyor
        //select * from t_student where email=student.getEmail.... geriye donen bir deger varsa bu maili bir kullaniyor demektir

        boolean existsStudent = repository.existsByEmail(student.getEmail()); //kayit yoksa 0, kayit varsa 0dan buyuk deger donderir
        if (existsStudent){
            // bu email daha once kullanilmis, ---> hata firlatalim
            throw new ConflictException("Email Already Exist!");
        }
        repository.save(student); // insert into.. ile bize otomatik olarak bizim icin kayit islemi yapiyor


    }
    //7-
    public Student getStudentById(Long id) {
       Student student = repository.findById(id).
               orElseThrow(()->new ResourceNotFoundException("Student is not found by id: " + id));
       return student;
    }

    //9-
    public void deleteStudentById(Long id) {
        //id si verilen sutudent yoksa özel bir mesaj ile custom eşception fırlatmak istiyoruz

        //1.yol
        //getStudentById(id);
        //repository.deleteById(id);

        //2.yol
        //bu id ile öğrenci var mı?
        Student student = getStudentById(id);
        repository.delete(student);

    }

    //11-idsi verilen öğrencinin bilgilerini dto'da gelen bilgiler ile değiştirelim
    public void updateStudent(Long id, UpdateStudentDTO studentDTO) {

        Student foundStudent=getStudentById(id);//1,Jack,Sparrow,jack@mail.com,98,13.01...

        //emailin unique olmasına engel bir durum var mı
        //DTO dan gelen yeni email           tablodaki emailler
        //1-xxx@mail.com                     YOK V (existsByEmail : false) -->update
        //2-harry@mail.com                   başka bir öğrenciye ait X (eşistsByEmail : true) -->ConflictException
        //3-jack@mail.com                    kendisine ait V (existsByEmail : true) --> bu bir cakışma değil

        //istek ile gönderilen email tabloda var mı ?
        boolean existEmail=repository.existsByEmail(studentDTO.getEmail());//T : kendisinin veya başkasının
        boolean selfEmail =foundStudent.getEmail().equals(studentDTO.getEmail());//T : kendisine ait email demek
        if (existEmail && !selfEmail){
            //çakışma var
            throw new ConflictException("Email already exists!!!");
        }
        foundStudent.setName(studentDTO.getName());
        foundStudent.setLastname(studentDTO.getLastname());
        foundStudent.setEmail(studentDTO.getEmail());
        repository.save(foundStudent);//saveOrUpdate gibi calıştını gösterir cünkü yukarda bilgisi olan biri var.

    }

    //13-gerekli parametreleri(bilgileri) pageable ile vererek
    //tüm öğrencilerin sayfalanmasını talep edilen sayfanın döndürülmesi sağlayalım
    public Page<Student> getAllStudentsByPage(Pageable pageable) {
        Page<Student> studentPage=repository.findAll(pageable);
        return studentPage;
    }

}
