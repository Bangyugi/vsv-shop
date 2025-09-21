package com.bangvan.dto.response.auth;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenResponse {
    @Builder.Default
    private String tokenType="Bearer";
    private String accessToken;
    private String refreshToken;
    private Timestamp expiredTime;
}
