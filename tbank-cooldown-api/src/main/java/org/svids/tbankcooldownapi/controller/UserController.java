package org.svids.tbankcooldownapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.svids.tbankcooldownapi.dto.AuthDto;
import org.svids.tbankcooldownapi.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthDto> authenticate() {
        return ResponseEntity.ok(new AuthDto(userService.authenticateUser()));
    }
}
