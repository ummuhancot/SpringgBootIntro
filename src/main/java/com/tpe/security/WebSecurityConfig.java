package com.tpe.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration//yapılandırma ayarlarının yapılacağını gösteriyor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//methodlarda yetkilendirme yapmamızı sağlıyor.
//@EnableGlobalMethodSecurity(prePostEnabled = true) ifadesi, Spring Security'de yöntem düzeyinde güvenliği
// etkinleştirmek için kullanılır. Bu, uygulamanızdaki yöntemleri güvence altına almak için
// @PreAuthorize ve @PostAuthorize gibi ek açıklamaları kullanmanıza olanak tanır.
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    //configure yazınca cıkıyor.
    @Override//sprig security nin filtrelerine bir filtrede biz verdik.
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().//REST API:session yok
        //spring security defaul dolarak csrf korumasıyla gelir.
        //disable kapat demek rest apıda sessin yok bu yüzden kaldırdık.
        authorizeHttpRequests().
        //kayıt olma aşamasında bilgilerini giricek bilgilerini girebilmesi icin filtreye takılmaması gerekir bunu icin yazılır.ana sayfaya girmek icin filtreye ihtiyac olmamalı mesela
                antMatchers("index.html","/register","/login").permitAll().//izin ver                authenticated().
                anyRequest().//bunun dışındakiler otantike et yani kimliklendir sor yetkilendir sor demek
                authenticated().//bunun dışındakiler otantike et yani kimliklendir sor yetkilendir sor demek
                and().
                httpBasic();//bilgileri kontrol et demek
    }


    @Bean//haslama yapcaz burda
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    //kullanıcının passwordunu hashleyerek kaslamak icin bize gerekli
    //AuthProvider ise DB deki passwordu requestte gelen ile karşılaştırma icin  kullanılır.
    @Bean//şifreleri kodlayarak kayıt etmemiz kullanabiliriz bunlardan en iyisi tuzlama oda bu
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();//zorluk:4-34
    }

    //configure yazınca cıkıyor.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
}
/*Spring Security'de, şifreleme ve hashleme işlemleri için farklı algoritmalar kullanabilirsiniz. Aşağıda, yaygın olarak kullanılan şifreleme algoritmalarından bazılarını örnek olarak gösterdim:

### 1. **BCrypt (Tuzlama İçerir)**
BCrypt, tuzlama işlemini otomatik olarak gerçekleştirir ve güçlü bir hashleme algoritmasıdır.
```java
@Bean
public PasswordEncoder bcryptPasswordEncoder() {
    return new BCryptPasswordEncoder(); // Varsayılan güç seviyesi: 10
}
```

### 2. **PBKDF2**
PBKDF2, birçok iterasyon kullanarak güvenli bir hash üretir.
```java
@Bean
public PasswordEncoder pbkdf2PasswordEncoder() {
    return new Pbkdf2PasswordEncoder("gizliAnahtar", 185000, 256);
    // Secret key, iteration count ve hash length
}
```

### 3. **SCrypt**
SCrypt, bellek kullanımı gerektirerek daha güvenli bir hashleme sağlar.
```java
@Bean
public PasswordEncoder scryptPasswordEncoder() {
    return new SCryptPasswordEncoder(16384, 8, 1, 32, 64);
    // (CPU/Memory cost, parallelization, block size, key length, salt length)
}
```

### 4. **Argon2**
Argon2, modern ve güçlü bir hashleme algoritmasıdır.
```java
@Bean
public PasswordEncoder argon2PasswordEncoder() {
    return new Argon2PasswordEncoder(16, 32, 1, 1 << 16, 3);
    // (Salt length, hash length, parallelism, memory cost, iterations)
}
```

### 5. **SHA-256 (Tuzlama ile Manuel)**
SHA-256 basit bir hashleme algoritmasıdır ancak tuzlama işlemi manuel olarak yapılmalıdır.
```java
@Bean
public PasswordEncoder sha256PasswordEncoder() {
    return new MessageDigestPasswordEncoder("SHA-256");
}
```

### 6. **NoOp (Şifreleme Yok - Önerilmez)**
Şifreyi düz metin olarak saklar. **Güvenlik açısından asla önerilmez.**
```java
@Bean
public PasswordEncoder noopPasswordEncoder() {
    return NoOpPasswordEncoder.getInstance();
}
```

### Hangi Algoritmayı Seçmeliyim?
- **Tuzlama ve hash güvenliği** için **BCrypt**, **Argon2**, veya **PBKDF2** tavsiye edilir.
- Modern uygulamalarda genellikle **BCrypt** veya **Argon2** kullanılır.
- **SHA-256** gibi algoritmalar modern saldırılara karşı dayanıksız olabilir, tuzlama olmadan kullanılmamalıdır.

Her biri, şifrelerinizi güvenli bir şekilde saklamanızı sağlar. **BCrypt**, çoğu senaryo için en iyi seçenektir çünkü yaygın olarak desteklenir ve güçlüdür. Argon2 ise daha yeni ve oldukça güvenlidir.*/