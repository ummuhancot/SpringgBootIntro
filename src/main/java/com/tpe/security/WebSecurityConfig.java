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
                anyRequest().//bunun dışındakiler izin verme filtreye takıl diyoruz.
                authenticated().
                and().
                httpBasic();
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
