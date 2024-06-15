package growthook.org.bamgang.global.auth.config;

import growthook.org.bamgang.members.dto.token.MemberToken;
import growthook.org.bamgang.members.jwtUtil.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "Bearer";

    private final JWTUtil jwtUtil;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if(authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_TYPE)){
            throw new IllegalArgumentException("로그인이 필요한 서비스입니다. 로그인 후 진행해 주세요. ");
        }

        String token = authorizationHeader.substring(TOKEN_TYPE.length()).trim();

        try{
            // 토큰 유효성 검증
            MemberToken memberToken = jwtUtil.getMemberTokenFromToken(token);
            request.setAttribute("authority", memberToken);
        } catch (Exception e){
            log.error("토큰 파싱 오류: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.", e);
        }
        return true;
    }
}
