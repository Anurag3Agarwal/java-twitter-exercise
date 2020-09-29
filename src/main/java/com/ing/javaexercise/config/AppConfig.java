package com.ing.javaexercise.config;

import com.ing.javaexercise.authentication.Authenticator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

@Bean
public Authenticator authenticator(){
return new Authenticator();
}
}
