package com.VaultPay.auth;
import com.example.security.JwtService;
import com.VaultPay.user.UserRepository;
import Lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.authentication.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    private final UserRepository userRepository;
    private final PasswordEncorder passwordEncorder;

    @PostMapping("/login")
    public String login(@RequestBody UserEntity request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername().get();
        return JwtService.generateToken(user);
    }

    @PostMapping("/register")
    public String register(@RequestBody UserEntity request){
        user.setPassword(passwordEncorder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";

    }
    @PostMapping("/refresh")
    public String refresh(@RequestParam String refreshToken){
        String username= refreshTokenService.validateRefreshToken(refreshToken);
        if(username==null){
            throw new RuntimeException("Invalid refresh token");

        }
        var user= userRepository.findByUsername(username).get();
        return jwtService.generateToken(user);

    }
    @PostMapping("/logout")
    public  string logout(HttpServletRequest request){
        String authHeader= request.getHeader("Authorisation");
        if(authHeader!=null && authHeader.startsWith("Bearer")){
            tokenBlacklistService.blacklistToken(authHeader.subString(7));
        }
        return "Logged out successfully";
    }
}