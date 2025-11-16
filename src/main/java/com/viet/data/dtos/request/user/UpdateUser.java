package com.viet.data.dtos.request.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUser {
    private String username;
    private String email;
    private String password;
    private List<String> roles;
}
