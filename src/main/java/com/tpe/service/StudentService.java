package com.tpe.service;

import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.exception.ConflictException;
import com.tpe.exception.ResourceNotFoundException;
import com.tpe.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

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
        //BeanUtils.copyProperties(studentDTO, foundStudent); 3 satırın kısaltması
        repository.save(foundStudent);//saveOrUpdate gibi calıştını gösterir cünkü yukarda bilgisi olan biri var.

    }



    //13-gerekli parametreleri(bilgileri) pageable ile vererek
    //tüm öğrencilerin sayfalanmasını talep edilen sayfanın döndürülmesi sağlayalım
    public Page<Student> getAllStudentsByPage(Pageable pageable) {
        Page<Student> studentPage=repository.findAll(pageable);
        return studentPage;
    }



    //toplu kayıt işlemleri --- biz yaptık ---
    public void saveMoreStudent(List<Student> students) {
        repository.saveAll(students);
    }



    //15-
    public List<Student> getStudentsByGrade(Integer grade) {
        //select * from Student where grade=100
        //return repository.findAllByGrade(grade);
        return repository.filterStudentsByGrade(grade);
    }


    //18-a : id'si verilen studenti tablodan getirelim
    public StudentDTO getStudentByIdDto(Long id) {

        Student student = getStudentById(id);

        //Entity --> DTO
        // tablodan gelen entitynin içindeki 3 datayı alıp
        //dto objesi içine yerleştirdik

        //@AllArgsConstructor kısmını kullandık burada
        //StudentDTO studentDTO=new StudentDTO(student.getName(),student.getLastname(),student.getGrade());
        //StudentDTO studentDTO=new StudentDTO();
        //studentDTO.setName(student.getName());....

        //yukarıdaki 2 seçenek zahmetli, bunun yerine DTO oluşturmak için
        // constructorın parametresine Entity objesi verip dönüşümü sağlayabiliriz
        StudentDTO studentDTO=new StudentDTO(student);
        return studentDTO;
    }

    //18-b : repositoryden doğrudan DTO objesi getirelim
    public StudentDTO getStudentByInfoByDTO(Long id) {

        //Student student = getStudentById(id);//burayı kullanmadık student döndürüyor studentDto döndürmüyor bu yüzden aşadaki gibi yazdık
        StudentDTO studentDTO = repository.findStudentDtoById(id).
                orElseThrow(()->new ResourceNotFoundException("Student is not found by id: " + id));
        return studentDTO;
    }

    //ÖDEV:16
    public List<Student> getAllStudentByLastname(String lastName) {
        return repository.findAllByLastnameIgnoreCase(lastName);
         //IgnoreCase kısmını ekledik büyük küçük duyarsız oldu
    }


    //meraklısına :) ÖDEV
    public List<Student> getAllStudentByNameOrLastname(String word) {
        return repository.findByNameOrLastname(word,word);

    }
    //and
    public List<Student> getAllStudentByNameAndLastname(String word) {
        return repository.findByNameAndLastname(word,word);

    }


    //Meraklısına:) ÖDEVVV:2
    public List<Student> getByKeyword(String word) {
        return repository.findByNameContainsIgnoreCase(word);
    }

    //Meraklısına:) ÖDEVVV:3
    public List<Student> getByKeyWordNameOrLastname(String word) {
        return repository.findByNameOrLastnameContainsIgnoreCase(word,word);
    }
}
