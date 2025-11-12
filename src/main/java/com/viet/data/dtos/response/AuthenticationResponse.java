package com.viet.data.dtos.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {
    private boolean authenticated;
    private String token;
}
