package com.app.config;

import com.app.config.filter.JwtTokenValidator;
import com.app.service.UserDetailServiceImp;
import com.app.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    /*
       1. filter chain
       2. authentication manager
       3. athentication provider -> passwordEncoder and userDetails
     */
    @Autowired
    private JwtUtils jwtUtils;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                //tipo de autentificacion
                .httpBasic(Customizer.withDefaults())
                //politica de autentificacion
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http ->{

                    //endpoint public
                    http.requestMatchers(HttpMethod.POST,"/auth/log-in").permitAll();
                    http.requestMatchers(HttpMethod.POST,"/auth/sign-in").permitAll();
                    //endpoint secured
                    http.requestMatchers(HttpMethod.GET,"/user/getSecurity").hasRole("ADMIN");
                    http.requestMatchers(HttpMethod.GET,"/user/get").hasRole("ADMIN");

                    http.anyRequest().denyAll();

                })
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        /*
        authenticationManager --> encargado de la administracion de la autentificacion
         */
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImp userDetailServiceImp){

        // 1. instance authenticationProvider
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        // 2. passwordEncoder
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        // 3. userDetails
        authenticationProvider.setUserDetailsService(userDetailServiceImp);

     return authenticationProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        /*
        aqui se establece la encriptacion de las contrasenas
         */
        return new BCryptPasswordEncoder();
    }


}
