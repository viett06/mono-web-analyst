package com.viet.data.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.viet.data.dtos.request.user.AuthenticationRequest;
import com.viet.data.dtos.request.token.IntrospectRequest;
import com.viet.data.dtos.request.user.LogoutRequest;
import com.viet.data.dtos.response.AuthenticationResponse;
import com.viet.data.dtos.response.IntrospectResponse;
import com.viet.data.dtos.request.token.RefreshTokenRequest;
import com.viet.data.entity.InvalidatedToken;
import com.viet.data.entity.User;
import com.viet.data.exception.AppException;
import com.viet.data.exception.ErrorCode;
import com.viet.data.repository.InvalidatedTokenRepository;
import com.viet.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import static com.viet.data.exception.ErrorCode.UNAUTHENTICATED;

@Slf4j
@Service
public class AuthenticationService {

    @Value("${jwt.signerKey}")
    private String SINGER_KEY;
    @Value("${jwt.valid-duration}")
    private long VALID_DURATION;
    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    //@Autowired
    //private PasswordEncoder passwordEncoder;
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("SignKey: {}", SINGER_KEY);
        User user = userRepository.findByUsername(request.getName()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassWord(), user.getPassword());

        if (!authenticated) {
            throw new AppException(UNAUTHENTICATED);
        }
        return AuthenticationResponse.builder()
                .authenticated(authenticated)
                .token(generateToken(user))
                .build();


    }


    private String generateToken(User user) {

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("vanviet")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }
    // ham logout
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(),true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        }catch (AppException exception){
            log.info("Token already expired");
        }


    }
    // ham refreshToken
    public AuthenticationResponse refreshToken(RefreshTokenRequest request)
            throws ParseException, JOSEException {
        var signJwt = verifyToken(request.getToken(), true);
        var jit = signJwt.getJWTClaimsSet().getJWTID();
        var expiryTime = signJwt.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        var username = signJwt.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(()-> new AppException(UNAUTHENTICATED));
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(generateToken(user))
                .build();

    }
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());
        // read token
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        // xac thuc token nay theo thuat toan ma hoa khi gennerate
        var verified = signedJWT.verify(verifier);
        if(!(verified && expiryTime.after(new Date()))) {
            throw new AppException(UNAUTHENTICATED);}
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(UNAUTHENTICATED);
        }
        return signedJWT;
    }

    // ham valid thu cong token
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
        boolean isValid = true;
        try {
            var jwtToken = verifyToken(token, false);
        }catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    // ham build scope
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if ((!CollectionUtils.isEmpty(user.getRoles()))) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });
        }
        return stringJoiner.toString();

    }
}
