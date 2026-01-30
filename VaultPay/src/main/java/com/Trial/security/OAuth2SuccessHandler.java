package com.Trial.security;
import com.Trial.user.UserEntity;
import com.Trial.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.Servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AutheenticationSuccessHandler{
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response ,
                                      Authentication authentication )throws IOException {
        OAuth2User oAuth2User= (OAuth2User) authentication.getPrinciple();
        String email = oAuth2User.getAttribute("email");

        UserEntity user = userRepository.findByUsername(email)
                .orElseGet(()->{
                    UserEntity newUser = new UserEntity();
                    newUser.setUsername(email);
                    newUser.setPassword("OAUTH2_USER");
                    newUser.setRole("USER");
                    return userRepository.save(newUser);
                });
        String token = jwtService.generateToken(user);

        response.sendRedirect("http://localhost:3000/oauth2/success?token=" + token);

    }
}

