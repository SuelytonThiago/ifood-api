package com.example.msorder.security;

import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader(){
        return new BasicGrpcAuthenticationReader();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        return http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/v1/auth/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/v2/api-docs")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/v3/api-docs/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/swagger-resources")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/swagger-resources/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/configuration/ui")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/configuration/security")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/webjars/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui.html")).permitAll()

                .requestMatchers(mvcMatcherBuilder.pattern("/api/bag/create")).hasAnyRole("USER","OWNER","ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/bag")).hasAnyRole("USER","OWNER","ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/bag/delete/**")).hasAnyRole("USER","OWNER","ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/bag/update/**")).hasAnyRole("USER","OWNER","ADMIN")

                .requestMatchers(mvcMatcherBuilder.pattern("/api/orders/create")).hasAnyRole("USER","OWNER","ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/orders/getAll")).hasAnyRole("USER","OWNER","ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/orders/update")).hasRole("ADMIN")


                .anyRequest().authenticated().and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
