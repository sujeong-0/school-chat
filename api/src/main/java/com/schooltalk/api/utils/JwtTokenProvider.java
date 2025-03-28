package com.schooltalk.api.utils;

import com.schooltalk.core.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 이 클래스는 JWT 관련된 유틸 제공을 담당합니다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

	/**
	 * 시크릿 키
	 */
	private final SecretKey SECRET_KEY;
	/**
	 * JWT 유효 시간
	 */
	private final long EXPIRATION_TIME;

	public JwtTokenProvider(@Value("${jwt.password}") String password, @Value("${jwt.expiration-time}") long expirationTime) {
		this.SECRET_KEY = Keys.hmacShaKeyFor(password.getBytes(StandardCharsets.UTF_8));
		this.EXPIRATION_TIME = expirationTime;
	}

	/**
	 * 생성
	 *
	 * @param userInfo 유저 정보
	 * @return JWT 토큰
	 */
	public String generateToken(User userInfo) {
		return Jwts.builder()
			.subject(userInfo.getEmail())
			.claim("email", userInfo.getEmail())
			.claim("role", userInfo.getRole())
			.claim("username", userInfo.getName())
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
			.signWith(SECRET_KEY)
			.compact();
	}

	/**
	 * 사용자 이메일 추출
	 *
	 * @param token JWT 토큰
	 * @return 사용자 이메일
	 */
	public String getUserEmailFromToken(String token) {
		Optional<Claims> payload = getPayload(token);
		return payload.map(Claims::getSubject).orElse(null);
	}

	/**
	 * 만료시간 추출
	 *
	 * @param token JWT 토큰
	 * @return 만료시간
	 */
	public long getExpiration(String token) {
		Optional<Claims> payload = getPayload(token);
		Date expiration = payload.map(Claims::getExpiration).orElse(null);
		return Objects.requireNonNull(expiration).getTime() - System.currentTimeMillis();
	}

	/**
	 * payload 추출
	 *
	 * @param token JWT 토큰
	 * @return Payload
	 */
	private Optional<Claims> getPayload(String token) {
		try {
			return Optional.of(Jwts.parser()
				.verifyWith(SECRET_KEY)
				.build()
				.parseSignedClaims(token)// 서명 검증
				.getPayload());
		} catch (Exception e) {
			log.error("Error while parsing token. message = {}", e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * 유효성 검사
	 *
	 * @param token JWT 토큰
	 * @return 유효한지 결과
	 */
	public boolean validateToken(String token) {
		Optional<Claims> payload = getPayload(token);
		return payload.filter(value -> !value.getExpiration().before(new Date())).isPresent();
	}
}
