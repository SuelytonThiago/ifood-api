package com.example.msstore.security;
import lombok.AllArgsConstructor;
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
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
@AllArgsConstructor
public class WebSecurityConfig {


    private final JWTFilter jwtFilter;

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

                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/create")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/pharmacy")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/supermarket")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/restaurant")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/delete/**")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/update/**")).hasRole("OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/reviews/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/menu/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/store/search/**")).permitAll()

                .requestMatchers(mvcMatcherBuilder.pattern("/api/categories/create")).hasRole("ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/categories")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/categories/delete/**")).hasRole("ADMIN")

                .requestMatchers(mvcMatcherBuilder.pattern("/api/foods/category/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/foods/create/**")).hasRole("OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/foods/delete/**")).hasRole("OWNER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/foods/update/**")).hasRole("OWNER")

                .requestMatchers(mvcMatcherBuilder.pattern("/api/reviews/create/**")).hasRole("USER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/reviews/delete/**")).hasAnyRole("USER","ADMIN")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/reviews/update/**")).hasRole("USER")

                .anyRequest().authenticated().and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
