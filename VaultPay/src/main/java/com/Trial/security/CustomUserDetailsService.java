package Com.VaultPay.security;

import com.VaultPay.user.UserRepository;
import lombok.RequiredArgsConstructor;
import  org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotfoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return userRepository.findByUsername(username) throws usernameNotFoundException{
            return userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found "));
            
        }
    }
}