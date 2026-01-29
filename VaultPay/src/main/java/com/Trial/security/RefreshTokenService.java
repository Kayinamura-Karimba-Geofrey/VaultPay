package  com.VaultPay.Trial.security;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.Util.Map;
import java.util.UUID;

@Service
public class RefleshTokenService{
    private final Map<String , String> refleshTokens = new HashMap<>();

    public  String generateRefleshToken(String username){
        string token = UUID.randomUUID().toString();
        refreshTokens.put(token,username);
        return token;
    }
    public String validateRefleshToken(string token){
        return refleshTokens.get(token);

    }
    public void deleteRefleshToken(String token){
        refleshTokens.remove(token);

    }
}