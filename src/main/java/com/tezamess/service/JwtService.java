
package com.tezamess.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
public class JwtService {

    @Autowired
    private Environment env;

    public static final String PHONE = "phone";

    public String generateTokenLogin(String phone) {
        String token = null;
        try {
            JWSSigner signer = new MACSigner(generateShareSecret());

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim(PHONE, phone);
            builder.expirationTime(generateExpirationDate());

            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            signedJWT.sign(signer);

            token = signedJWT.serialize();
        } catch (KeyLengthException ex) {
            System.out.println(ex.toString());
        } catch (JOSEException ex) {
            System.out.println(ex.toString());
        }
        return token;
    }

    private JWTClaimsSet getClaimsFromToken(String token) {
        JWTClaimsSet claims = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(generateShareSecret());
            if (signedJWT.verify(verifier)) {
                claims = signedJWT.getJWTClaimsSet();
            }
        } catch (JOSEException | ParseException e) {
            System.out.println(e.toString());
            return claims;
        }
        return claims;
    }

    private Date generateExpirationDate() {
        int expireTime = Integer.parseInt(env.getProperty("expire_time"));
        return new Date(System.currentTimeMillis() + expireTime);
    }

    private Date getExpirationDateFromToken(String token) {
        Date expiration = null;
        JWTClaimsSet claims = getClaimsFromToken(token);
        expiration = claims.getExpirationTime();
        return expiration;
    }

    public String getPhoneFromToken(String token) {
        
        String phone = null;
        try {
            JWTClaimsSet claims = getClaimsFromToken(token);
            if(claims == null){
                return phone;
            }
            phone = claims.getStringClaim(PHONE);      
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
        return phone;
    }

    private byte[] generateShareSecret() {
        // Generate 256-bit (32-byte) shared secret
        byte[] sharedSecret = new byte[32];
        String secret_key = env.getProperty("secret_key");
        sharedSecret = secret_key.getBytes();
        return sharedSecret;
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateTokenLogin(String token) {
        if (token == null || token.trim().length() == 0) {
            return false;
        }
        String phone = getPhoneFromToken(token);
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        if (isTokenExpired(token)) {
            return false;
        }
        return true;
    }

}
