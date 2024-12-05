package com.res.shein;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration


public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.builder()
            .username("Omaima")
            .password("{noop}omaima2003")  // Use {noop} for plain text passwords
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authorizeRequests -> 
                authorizeRequests
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/home", "/home/**", "/search", "/cart/**", "/cart", "/products").permitAll()
                    .requestMatchers("/images/**", "/css/**", "/products/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin()
                .defaultSuccessUrl("/admin/products", true)
                .permitAll()
            .and()
            .logout().permitAll();

        return http.build();
    }


}


