package com.dev.TomoAdministration.constant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider implements InitializingBean {@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

//	private Key key;
//	private final String secret;
//	private final long tokenValidityInMilliseconds;
//
//	public JwtProvider(
//			@Value("${jwt.secret}") String secret,
//			@Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
//		this.secret = secret;
//		this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
//	}
//
//	// 빈이 생성되고 주입을 받은 후에 secret값을 Base64 Decode해서 key 변수에 할당하기 위해
//	@Override
//	public void afterPropertiesSet() {
//		byte[] keyBytes = Decoders.BASE64.decode(secret);
//		this.key = Keys.hmacShaKeyFor(keyBytes);
//	}
//
//	public String generateToken(
//			Authentication authentication,
//			String status
//			) {
//		// 권한 가져오기
//		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
//				.collect(Collectors.joining(","));
//
//		long now = (new Date()).getTime();
//		
//		// Access Token 생성
//		Date accessTokenExpiresIn = new Date(now + this.tokenValidityInMilliseconds);
//		String accessToken = Jwts.builder().setSubject(authentication.getName()).claim("auth", authorities)
//				.setExpiration(accessTokenExpiresIn).signWith(key, SignatureAlgorithm.HS256).compact();
//
//		// Refresh Token 생성
//		String refreshToken = Jwts.builder().setExpiration(new Date(now + this.tokenValidityInMilliseconds))
//				.signWith(key, SignatureAlgorithm.HS256).compact();
//
//		return TokenInfo.builder()
//				.accessToken(accessToken)
//				.refreshToken(refreshToken)
//				.status(status).build();
//	}
//
//	// JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
//	public Authentication getAuthentication(String accessToken) {
//		// 토큰 복호화
//		Claims claims = parseClaims(accessToken);
//
//		if (claims.get("auth") == null) {
//			throw new RuntimeException("권한 정보가 없는 토큰입니다.");
//		}
//
//		// 클레임에서 권한 정보 가져오기
//		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
//				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//
//		// UserDetails 객체를 만들어서 Authentication 리턴
//		UserDetails principal = new User(claims.getSubject(), "", authorities);
//		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
//	}
//
//	// 토큰 정보를 검증하는 메서드
//	public boolean validateToken(String token) throws io.jsonwebtoken.security.SignatureException {
//		System.out.println("validateToken");
//		try {
//			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//			return true;
//		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
//			log.info("Invalid JWT Token", e);
//		} catch (ExpiredJwtException e) {
//			log.info("Expired JWT Token", e);
//		} catch (UnsupportedJwtException e) {
//			log.info("Unsupported JWT Token", e);
//		} catch (IllegalArgumentException e) {
//			log.info("JWT claims string is empty.", e);
//		}
//		return false;
//	}
//
//	private Claims parseClaims(String accessToken) {
//		System.out.println("parseClaims");
//		try {
//			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
//		} catch (ExpiredJwtException e) {
//			return e.getClaims();
//		}
//	}
}
