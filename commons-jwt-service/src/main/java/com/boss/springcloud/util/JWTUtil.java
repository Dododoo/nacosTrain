package com.boss.springcloud.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.boss.springcloud.exception.TokenAuthenticationException;
import java.util.Date;

public class JWTUtil {
    /**
    * 过期时间
    * */
    public static final long TOKEN_EXPIRE_TIME = 7200 * 1000;

    /**
     *
     * */
    private static final String ISSUER = "samuel";



    /**
     * 生成TOKEN
     * @param username 用户标识
     * @param secreteKey
     * @return
     */
    public static String generateToken(String username, String secreteKey) {
        Algorithm algorithm = Algorithm.HMAC256(secreteKey);

        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + TOKEN_EXPIRE_TIME);
        String Token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(expireTime)
                .withClaim("username",  username)
                .sign(algorithm);
        return Token;
    }

    /**
     * 验证TOKEN
     * @param token
     * @param secreteKey
     * @throws TokenAuthenticationException
     */
    public static void verifyToken(String token, String secreteKey) throws TokenAuthenticationException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secreteKey);
            JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            jwtVerifier.verify(token);
        }catch (JWTDecodeException jwtDecodeException) {
             throw new TokenAuthenticationException(ResponseCodeEnum.TOKEN_INVALID.getCode(), ResponseCodeEnum.TOKEN_INVALID.getMessage());
        } catch (SignatureVerificationException signatureVerificationException) {
             throw new TokenAuthenticationException(ResponseCodeEnum.TOKEN_SIGNATURE_INVALID.getCode(), ResponseCodeEnum.TOKEN_SIGNATURE_INVALID.getMessage());
         } catch (TokenExpiredException tokenExpiredException) {
             throw new TokenAuthenticationException(ResponseCodeEnum.TOKEN_EXPIRED.getCode(), ResponseCodeEnum.TOKEN_INVALID.getMessage());
         } catch (Exception ex) {
             throw new TokenAuthenticationException(ResponseCodeEnum.UNKNOWN_ERROR.getCode(), ResponseCodeEnum.UNKNOWN_ERROR.getMessage());
         }
    }

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     */
    public static String getUserInfo(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String username = decodedJWT.getClaim("username").asString();
        return username;
    }
}
