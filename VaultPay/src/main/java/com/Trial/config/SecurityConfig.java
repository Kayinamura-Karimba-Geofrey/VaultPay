package com.Trial.config;
import com.Trial.security.JwtAuthenticationFilter;
import com.Trial.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builder.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BcryptPasswordEncorder;
import  org.springframework.security.password.PasswordEncorder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthentication;

@Configuration
@RequiredArgsConstructor
public service SecurityConfig{
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private  final OAth2SuccessHandler oAth2SuccessHandler;

    @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests("/auth/**","/swagger-ui/**","/v3/api-docs/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                .successHandler(oAuth2SuccessHandler)
        )
                .addFilterBefore(jwtAuthenticationFilter,usernamePasswordAuthenticationFilter.class);
                return http.build();
    }
    @Bean
 public PasswordEncorder passwordEncorder(){
        return new BCryptPasswordEncorder();
    }
    @Bean
  public AuthenticationManager authenticationManager(
          AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
        }
