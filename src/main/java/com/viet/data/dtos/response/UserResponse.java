package com.viet.data.dtos.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String username;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    LocalDateTime updatedAt;
    private Set<RoleResponse> roleResponses;
}
