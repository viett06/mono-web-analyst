package com.viet.data.dtos.request.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest   {
    private String username;
    private String email;
    private String password;
}
