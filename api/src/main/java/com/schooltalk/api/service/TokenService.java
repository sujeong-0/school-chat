package com.schooltalk.api.service;

import com.schooltalk.core.entity.User;

/**
 * 이 클래스는 토큰 서비스를 담당합니다.
 */
public interface TokenService {

	/**
	 * 토큰 생성
	 *
	 * @param userInfo  유저 정보
	 * @return JWT 토큰
	 */
	String generateToken(User userInfo);

	/**
	 * 로그아웃 처리
	 *
	 * @param token jwt
	 */
	void logout(String token);


	/**
	 * 토큰 유효성 검증
	 * @param token jwt
	 * @return 결과
	 */
	boolean validation(String token);

	/**
	 * 토큰에서 이메일 정보 조회
	 * @param token jwt
	 * @return email
	 */
	String getUserEmail(String token);
}
