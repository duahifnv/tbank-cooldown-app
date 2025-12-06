package org.svids.tbankcooldownapi.dto;

import java.util.UUID;

public record AuthDto(UUID id) {
    public static final String AUTH_HEADER = "X-USER-ID";
}
