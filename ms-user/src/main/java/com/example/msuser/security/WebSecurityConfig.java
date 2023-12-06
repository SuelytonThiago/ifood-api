package com.example.msuser.security;

import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JWTFilter filter;

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

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
                .requestMatchers(mvcMatcherBuilder.pattern("/api/auth/login")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/auth/refresh")).hasAnyRole("USER","ADMIN","OWNER")

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

                .requestMatchers(mvcMatcherBuilder.pattern("/api/users/create")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/users/update")).hasAnyRole("USER","ADMIN","OWNER")

                .requestMatchers(mvcMatcherBuilder.pattern("/api/address/create")).hasAnyRole("USER","ADMIN","OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/address/update/**")).hasAnyRole("USER","ADMIN","OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/address/delete/**")).hasAnyRole("USER","ADMIN","OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/address/myAddresses")).hasAnyRole("USER","ADMIN","OWNER")

                .requestMatchers(mvcMatcherBuilder.pattern("/api/cards/create")).hasAnyRole("USER","ADMIN","OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/cards/delete/**")).hasAnyRole("USER","ADMIN","OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/cards/update/**")).hasAnyRole("USER","ADMIN","OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/cards/paymentMethods")).hasAnyRole("USER","ADMIN","OWNER")
                .anyRequest().authenticated().and().addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
