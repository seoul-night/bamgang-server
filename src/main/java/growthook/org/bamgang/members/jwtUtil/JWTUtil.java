package growthook.org.bamgang.members.jwtUtil;

import growthook.org.bamgang.members.dto.token.MemberToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTUtil {
    @Value("${kakao-key}")
    private String secretKeyPlain;

    private final long EXPIRATION_SECONDS = 60 * 60 * 24 * 30;	//1달

    //application.properties에 등록된 변수
    public SecretKey getSecretKey() {
        byte[] keyBytes = secretKeyPlain.getBytes();
        if(keyBytes.length < 32){
            // 생성된 비밀 키의 길이가 충분히 긴지 확인하고,32바이트보다 짧다면, 예외처리
            throw new IllegalArgumentException("The secret key must be at lest 256 bits (32 bytes) long.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    public String generateToken(MemberToken member) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_SECONDS * 1000);

        return Jwts.builder()
                .claim("id", member.getId())
                .claim("nickName", member.getNickName())
                .claim("profileName", member.getProfile())
                .setExpiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    // 토큰 유효성 검사
    public boolean isValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    // 토큰으로 부터 MemberToken 빌드
    public MemberToken getMemberTokenFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return MemberToken.builder()
                .id(claims.get("id", String.class))
                .nickName(claims.get("nickName", String.class))
                .profile(claims.get("profileName", String.class))
                .build();
    }
}
