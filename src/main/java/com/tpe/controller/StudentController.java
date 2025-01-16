package com.tpe.controller;

import com.fasterxml.jackson.core.format.InputAccessor;
import com.tpe.domain.Student;
import com.tpe.dto.StudentDTO;
import com.tpe.dto.UpdateStudentDTO;
import com.tpe.service.StudentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
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

    //en başa almamız gerekiyor hepsine ekleme yapmamız icin
    Logger logger = LoggerFactory.getLogger(StudentController.class);


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
    ///log ekledik
    //Response : ogrenci tabloya eklenecek, basarili mesaji + 201(Created) cevap olarak istemciye bunlar dondurulur
    @PostMapping
    public ResponseEntity<String> createStudent(@Valid @RequestBody Student student){ //ResponseEntity<String> burda String turunde bir deger donderecegim
        ///eklenti
        try {
            service.saveStudent(student);
            logger.info("yeni öğrenci eklendi : "+student.getName());

            return new ResponseEntity<>("Student is created successfully...",HttpStatus.CREATED); //Durumu : HttpStatus.CREATE : 201

        }catch (Exception e){
            logger.warn(e.getMessage());
            //return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);//404
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);//400
        }

        //eski
        //service.saveStudent(student);
        //return new ResponseEntity<>("Student is created successfully...",HttpStatus.CREATED); //Durumu : HttpStatus.CREATE : 201
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
    @GetMapping("/lastname")
    public ResponseEntity<List<Student>> getStudentsByLastName(@RequestParam String lastname){

        List<Student> studentList=service.getAllStudentByLastname(lastname);

        return ResponseEntity.ok(studentList);//200
    }



    ///Meraklısına ÖDEVVV:)isim veya soyisme göre filtreleme
    //request:http://localhost:8080/students/search?word=harry + GET
    //uniq değilse list döner.
    //email veya id ile filtreleme yaparsak uniq olanlar yani Student dönebilir.
    //cok fazla kayıt varsa sayfalandırma yapabilirizmiş
    @GetMapping("/search")
    public ResponseEntity<List<Student>> getAllStudentByNameOrLastName(@RequestParam("word") String word){
        List<Student> studentList = service.getAllStudentByNameOrLastname(word);
        return ResponseEntity.ok(studentList);

    }
    //and kısmı ile
    @GetMapping("/search/and")
    public ResponseEntity<List<Student>> getAllStudentByNameAndLastName(@RequestParam("word") String word){
        List<Student> studentList = service.getAllStudentByNameOrLastname(word);
        return ResponseEntity.ok(studentList);

    }

    ///Meraklısına:) ÖDEVVV:2
    //Meraklısına ÖDEVVV:)name içinde ".." hecesi geçen öğrencileri filtreleme
    //request:http://localhost:8080/students/filter?word=al + GET ex:halil, lale
    @GetMapping("/filter")
    public ResponseEntity<List<Student>> filterByKeyWord(@RequestParam("word") String word){
        List<Student> studentList = service.getByKeyword(word);
        return ResponseEntity.ok(studentList);
    }

    //Meraklısına ÖDEVVV : 3 :)name içinde ".." hecesi ve lastname icinde "sa" geçen öğrencileri filtreleme
    //request:http://localhost:8080/students/filter/like?word=al + GET ex:halil, lale
    @GetMapping("/filter/like")
    public ResponseEntity<List<Student>> filterByKeywordNameOrLastname(@RequestParam("word") String word) {
        List<Student> studentList = service.getByKeyWordNameOrLastname(word);
        return ResponseEntity.ok(studentList);
    }

    //17-id'si verilen öğrencinin name,lastname ve grade getirme
    //request:http://localhost:8080/students/info/2 + GET
    //responsex.id si verilen öğrencinin sadece 3 datasını studet entity le değil DTO ile getirelim
    @GetMapping("/info/{id}")
    public ResponseEntity<StudentDTO> getStudentInfo(@PathVariable("id") Long id){

        //StudentDTO studentDTO = service.getStudentByIdDto(id);    //18-a
        StudentDTO studentDTO = service.getStudentByInfoByDTO(id);  //18-b
        return ResponseEntity.ok(studentDTO);
    }

    /*
    Loglama, bir yazılım veya sistemin çalışırken yaptığı önemli olayları,
     işlemleri ve hataları kaydetmesi anlamına gelir. Loglar,

     bilgisayar programlarının bir çeşit "günlüğü" veya "karne defteri" gibi düşünülebilir.
     Sistem, yaptığı işleri ve karşılaştığı problemleri
     buraya yazar ve bu bilgiler, geliştiriciler veya sistem yöneticileri için çok değerlidir.
    */

    ///her class icin ayrı log dosyaları oluşturulur.
    ///ihtiyacımız olan yerlere ayrı ayrı koymamız gerekiyor.
    //import org.slf4j.Logger; den import ediyoruz
   // Logger logger = LoggerFactory.getLogger(StudentController.class);

    //19-http://localhost:8080/students/welcome + GET
    @GetMapping("/welcome")
    public String welcome(HttpServletRequest request){
        logger.info("Welcome isteği geldi¹");//normal işlemler sıradan islemler info
        logger.warn("welcome isteğinin pathi : "+request.getServletPath());
        logger.warn("welcome isteğinin methodu : "+request.getMethod());
        return "welcome:)";
    }
    //Not:http://localhost:8080/students/update?name=Ali&lastname=Can&email=ali@mail.com

    /*
    Spring Boot Actuator, bir uygulamanın sağlık durumunu ve çalışma metriklerini izlemek
    için kullanılan bir Spring Boot kütüphanesidir. Actuator, bir uygulamanın
    arka planda nasıl çalıştığını görmenizi sağlar ve uygulamanın izlenebilirliğini artırır.
    Uygulama Sağlık Durumu:

    Uygulamanın çalışır durumda olup olmadığını kontrol eder.
    Örneğin: "Veritabanına bağlanabiliyor mu? Sunucu çalışıyor mu?"
    Metrik Takibi:

    Uygulamanın performansı hakkında bilgiler sağlar.
    Örneğin: "Kaç kullanıcı sisteme bağlandı? Bellek kullanımı ne durumda?"
    Günlük İşleyişin İzlenmesi:

    Loglama, yapılandırmalar, güvenlik bilgileri gibi iç detayları görmenizi sağlar.
    Sorun Giderme:

    Hata durumunda, sistemin hangi noktada sorun yaşadığını anlamanıza yardımcı olur
    /actuator/health    Uygulamanın sağlık durumunu gösterir.
    /actuator/metrics   Uygulamanın performansıyla ilgili metrikleri listeler.
    /actuator/env       Uygulamanın çevre değişkenlerini listeler.
    /actuator/loggers   Log seviyelerini ve log yapılandırmalarını kontrol eder.
    /actuator/info      Uygulama hakkında bilgi verir (ör. sürüm bilgisi).
    */

    /*Volkswagen'in Spring Boot Actuator'da gizlemediği şey, muhtemelen hassas
    uç noktalar (endpoints) veya yapılandırma bilgileri olabilir. Spring Boot Actuator,
    uygulamanın durumu ve metrikleri hakkında bilgi sağlayan çeşitli uç noktalar sunar.
    Bu uç noktalar arasında /env, /metrics, /health, /info gibi uç noktalar bulunur.
    Eğer bu uç noktalar güvenli bir şekilde yapılandırılmamışsa, hassas bilgiler
    (örneğin, GPS verileri, yapılandırma bilgileri) kötü niyetli kişiler tarafından
    erişilebilir hale gelebilir. Bu nedenle, Actuator uç noktalarını güvenli hale getirmek
    için aşağıdaki önlemler alınmalıdır:
    Uç Noktaları Güvenli Hale Getirme: Hassas uç noktaları güvenli hale getirmek için kimlik
    doğrulama ve yetkilendirme mekanizmaları kullanın.
    Uç Noktaları Kısıtlama: Sadece gerekli olan uç noktaları etkinleştirin ve diğerlerini devre dışı bırakın.
    Hassas Bilgileri Gizleme: Hassas bilgileri içeren uç noktaları gizleyin veya bu bilgileri maskeleyin.*/
}
