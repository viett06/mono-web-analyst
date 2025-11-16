package com.viet.data.controller;

import com.nimbusds.jose.JOSEException;
import com.viet.data.dtos.request.token.IntrospectRequest;
import com.viet.data.dtos.request.token.RefreshTokenRequest;
import com.viet.data.dtos.request.user.AuthenticationRequest;
import com.viet.data.dtos.request.user.LogoutRequest;
import com.viet.data.dtos.response.ApiResponse;
import com.viet.data.dtos.response.AuthenticationResponse;
import com.viet.data.dtos.response.IntrospectResponse;
import com.viet.data.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var response = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var response = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(response)
                .build();
    }
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder()
                .build();
    }
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws ParseException, JOSEException {
        var response = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(response)
                .build();
    }
}
