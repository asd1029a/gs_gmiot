package com.danusys.web.commons.auth.util;

import com.danusys.web.commons.auth.model.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtUtil {
    //키 수정 예정

    @Value("${secret.key}")
    private String SECRET_KEY ="";


    //private static final String AUTHORITIES_KEY = "auth";
    //private static final String BEARER_TYPE = "bearer";
    //private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    @Value("${access.token.expire.time}")
    private long ACCESS_TOKEN_EXPIRE_TIME = 0  ;            // 10seconds
    @Value("${refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME  =0 ;  // 7일

    public String extractUsername(String token) {


       // log.info("a1");
        return extractClaim(token, Claims::getSubject);
        //자바의 더블콜론은 위 람다식을 아래와 같이 더욱 간결하게 해준다
        //
        //즉 람다식이 이미 존재하는 메소드와 동일한 기능이면 메소드 레퍼런스로 람다식을 대체할 수 있다.
    }

    public Date extractExpiration(String token)   {


       // log.info("extractExpiration={}",extractClaim(token,Claims::getExpiration));
       // log.info("now={}",new Date());
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token)  {

        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        //log.info("extractExpiration(token).before(new Date())={}",extractExpiration(token).before(new Date()));
        return extractExpiration(token).before(new Date());
    }

    public TokenDto generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private TokenDto createToken(Map<String, Object> claims, String subject) {

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken= Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY)
                .compact();
        // tokenDto 타입으로 리턴
        return TokenDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();

/*
        return  Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

*/

    }
    //토큰 유효기간 검증 로직
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}