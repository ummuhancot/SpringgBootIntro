package com.tpe;

import com.tpe.domain.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //configuration otomatik olarak gerceklestirilir
//componetScan bu classin icinde bulundugu packageda default olarak tarar
public class SpringBootIntroApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootIntroApplication.class, args);
		//burada tomcat iceride entegre oldugu icin tomcat burdan calistiriliyo
		/*Student student = new Student();
		student.getEmail();
		student.setName(""); //direk burdan ulastik gormek icin bunu kullandik
		student.setId;*/ //HATA: burda  setter olusturmasina izin vermedigimiz icin kullanamadik


	}

}
