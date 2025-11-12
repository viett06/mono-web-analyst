package com.viet.data.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private int code = 1000;
    @JsonInclude(JsonInclude.Include.NON_NULL) // ẩn nếu null
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL) // ẩn nếu null
    private T result;
}