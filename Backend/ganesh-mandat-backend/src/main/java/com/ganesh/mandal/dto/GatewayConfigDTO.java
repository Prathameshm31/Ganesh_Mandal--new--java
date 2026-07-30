package com.ganesh.mandal.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayConfigDTO {

    private String provider;
    private String keyId;
    private String keySecret;
    private String webhookSecret;
    private boolean enabled;
}
