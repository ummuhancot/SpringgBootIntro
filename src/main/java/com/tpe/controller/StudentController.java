package com.tpe.controller;

import com.fasterxml.jackson.core.format.InputAccessor;
import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.service.StudentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/*
clienttan 3 sekilde veri alinir:
1- requestin BODY(JSON)
2-requestin URL query param
3-requestin URL path param
*/

@RestController //request'ler buradan eslestirilecegini belirtiyoruz, requestler bu classtaki methodlarla eslestirilecek ve responselar hazirlanacak
//@ResponseBody : metodun dönüş değerini JSON formatında cevap olarak hazırla
//RequestBody   : requestin icindeki(body) JSON formatinda olan datayi methodlari parametresinde kullanabilmemizi saglar
// :obje<->JSON'a donusturulmesi isini, Jackson yapar: objenin JSON'a Veya JSON'in objeye donusturulmesini jackson kutuphanesi sagliyor
//jackson kutuphanesi spring-web-starter ile otomatik olarak gelir
@RequestMapping("/students") //https://localhost:8080/students...
@RequiredArgsConstructor //sadece final olan field'lari const. olusturur
/*
@NoArgsConstructor -- default parametresiz const olusturur
@AllArgsConstructor-- tum fieldlari parametre olarak alan const. olusturur
@RequiredArgsConstructor -- zorunlu (final) olan field lari parametre olarak alan const. olusturur
*/
public class StudentController {

    @Autowired
    private StudentService service;

    //bunlarini kullanmamiza gerek yok bunu @RequiredArgsConstructor ile bunu cozmus olduk
//    @Autowired //tek oldugu icin bunu yazmaya gerek yok ama okunabilirlik icin yazdim simdilik
//    public StudentController(StudentService service) {
//        this.service = service;
//    }

    //SpringBOOT'u selamlama :)
    //http://localhost:8080/students/greet + GET
    //!! @ResponseBody //bu annatation @RestController icinde oldugu icin burda kullanmama gerek yok
    @GetMapping("/greet")
    public String greet(){
        return "Hello Spring Boot :)";
    }

    //1-tüm öğrencileri listeleyelim : READ
    //Request : http://localhost:8080/students + GET
    //Response : tum ogrencilerin listesini dondurecegiz + 200:OK(HttpStatus Code)yani cevabi destekleme kodu
    @GetMapping
    //@ResponseBody :RestController icerisinde var burada kullanmaya gerek kalmadi
    public ResponseEntity<List<Student>> getAllStudents(){
        //tablodan ogrencilerimizi getirecegiz
        List<Student> allStudents = service.findAllStudents();
        return new ResponseEntity<>(allStudents, HttpStatus.OK); //Durumu : HttpStatus.OK : 200 basarili olarak gonderdik
    }

    // ResponseEntity : response'un bodysini + status kodunu birlikte gonderebilmek icin kullanilir. kutu gibi icine bunlari koyup gonderecegim
    //3-ogrenci ekleme : CREATE
    //Request : http://localhost:8080/students + POST + body(JSON)
    /*
    {
    "name":"Jack",
    "lastname":"Sparrow",
    "email":"jack@mail.com",
    "grade":98
    }
     */
    //Response : ogrenci tabloya eklenecek, basarili mesaji + 201(Created) cevap olarak istemciye bunlar dondurulur
    @PostMapping
    public ResponseEntity<String> createStudent(@Valid @RequestBody Student student){ //ResponseEntity<String> burda String turunde bir deger donderecegim

        service.saveStudent(student);

        return new ResponseEntity<>("Student is created successfully...",HttpStatus.CREATED); //Durumu : HttpStatus.CREATE : 201
    }


    //6-query param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/query?id=1 + GET
    //response : student + 200
    @GetMapping("/query")
    public ResponseEntity<Student> getStudent(@RequestParam("id") Long id){
        Student foundStudent = service.getStudentById(id);
        return new ResponseEntity<>(foundStudent,HttpStatus.OK);
    }

    //ÖDEV:(Alternatif)6-path param ile id si verilen öğrenciyi getirme
    //request: http://localhost:8080/students/1 + GET
    //response : student + 200
    //esponseEntity<?> generik olarak kullanımı
    @GetMapping("/{id}")
    public ResponseEntity<Student> findStudent(@PathVariable("id") Long id){
        Student foundstudent=service.getStudentById(id);
        return new ResponseEntity<>(foundstudent,HttpStatus.OK);
    }
    //8-path param ile id si verilen öğrenciyi silme
    //request: http://localhost:8080/students/1 + DELETE
    //response: tablodan kayıt silinir, saşarılı bir şekilde silindi başarılı mesajı + 200
    @DeleteMapping("/{deletedId}")//bu kısmı biz belirliyoruz
    public ResponseEntity<String> deleteStudent(@PathVariable("deletedId") Long id) {
        service.deleteStudentById(id);
        return ResponseEntity.ok("Student is deleted successfully...");
        //return new ResponseEntity<>("Student is deleted successfully...",HttpStatus.OK);    }
    }

    //10-idsi verilen öğrencinin name,lastname ve emailini değiştirme(güncelleme)
    //request : http://localhost:8080/students/1 + PUT(yerine koyma isim soyisim kısmını değiştirirsen digerlerini de siler)/
    //PATCH(kısmi bir kısmını düzenlemek istediğiniz zaman kullanılır) + BODY(JSON) --->YENİ KAYITTA POST
    //response : güncelleme, başarılı mesaj + 201 kodu döndürülür yeni biligiler ekledimiz icin bazı bilgiler olsa bile 201 kullanılır.
    @PostMapping("/{id}")
    public ResponseEntity<String> updateStudent(@PathVariable("id") Long id, @Valid @RequestBody UpdateStudentDTO studentDTO){

        service.updateStudent(id,studentDTO);
        return new ResponseEntity<>("Student is updated successfully...",HttpStatus.CREATED);//201
    }


    //12-tüm öğrencileri listeleme : READ
    // pagination (sayfalandırma) : hız/performans (data baseden 100 üdür cetket si 10 sayfa cekmekmi)
    //tüm kayıtları page page (sayfa sayfa) gösterelim.
    //request :
    //http://localhost:8080/students/page?
    //                               page=3&
    //                               size=20&
    //                               sort=name&
    //                               direction=DESC(ASC) + GET
    @GetMapping("/page")
    public ResponseEntity<Page<Student>> getAllStudents(@RequestParam("page") int pageNo, //kaçıncı sayfa
                                                        @RequestParam("size") int size,  //her sayfada kaç tane kayıt
                                                        @RequestParam("sort") String property, //hangi özelliğe göre sıralma
                                                        @RequestParam("direction") Sort.Direction direction){//sıralamanın yönü için sabit değişken

        //findAll metodunun sayfa getirmesi için gerekli olan bilgileri
        //pageable tipinde verebiliriz.
        Pageable pageable= PageRequest.of(pageNo,size,Sort.by(direction,property));

        Page<Student> studentPage=service.getAllStudentsByPage(pageable);

        return new ResponseEntity<>(studentPage,HttpStatus.OK);//200

    }
    // 1 | 2 | 3 | 4 ...next

    //toplu kayıt işlemleri---biz yaptık---
    @PostMapping("/morestudents")
    public ResponseEntity<String> createMoreStudent(@RequestBody List<Student> students){
        service.saveMoreStudent(students);
        return new ResponseEntity<>("More students created",HttpStatus.CREATED);
    }

    //14-grade ile öğrencileri filtreleyelim
    // query veya path param kullanabiliriz
    // request:http://localhost:8080/students/grade/100
    //response: grade=100 olan öğrencileri listeleyelim + 200

    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<Student>> getAllStudentsByGrade(@PathVariable("grade") Integer grade){
        List<Student> studentList=service.getStudentsByGrade(grade);
        return ResponseEntity.ok(studentList);//200
    }

    //ÖDEVVV:
    //JPA in metodlarını türetme
    //JPQL/SQL ile custom sorgu
    //16-lastname ile öğrencileri filtreleyelim
    // request:http://localhost:8080/students/lastname?lastname=Potter + GET
    //response : lastname e sahip olan öğrenci listesi + 200



    //Meraklısına ÖDEVVV:)isim veya soyisme göre filtreleme
    //request:http://localhost:8080/students/search?word=harry + GET
    //uniq değilse list döner.
    //email veya id ile filtreleme yaparsak uniq olanlar yani Student dönebilir.
    //cok fazla kayıt varsa sayfalandırma yapabilirizmiş


    //17-id'si verilen öğrencinin name,lastname ve grade getirme
    //request:http://localhost:8080/students/info/2 + GET
    //responsex.id si verilen öğrencinin sadece 3 datasını studet entity le değil DTO ile getirelim
    @GetMapping("/info/{id}")
    public ResponseEntity<StudentDTO> getStudentInfo(@PathVariable("id") Long id){

        //StudentDTO studentDTO = service.getStudentByIdDto(id);    //18-a
        StudentDTO studentDTO = service.getStudentByInfoByDTO(id);  //18-b
        return ResponseEntity.ok(studentDTO);
    }


    //Not:http://localhost:8080/students/update?name=Ali&lastname=Can&email=ali@mail.com



}
