package com.viet.data.dtos.request.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest   {
    private String userName;
    private String email;
    private String password;
}
