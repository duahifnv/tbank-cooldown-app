package org.svids.tbankcooldownapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.AuthDto;
import org.svids.tbankcooldownapi.dto.analyze.request.AutoCoolingRequest;
import org.svids.tbankcooldownapi.dto.analyze.request.ManualCoolingRequest;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthDto> authenticate() {
        return ResponseEntity.ok(new AuthDto(userService.authenticateUser()));
    }

    @PutMapping("/cooling/mode/manual")
    public ResponseEntity<?> setManualCooling(@RequestHeader(value = AuthDto.AUTH_HEADER, defaultValue = "35cf7863-8110-46dd-a92d-99175ea9ed38") UUID userId,
                                              ManualCoolingRequest manualCoolingRequest) {
        User user = userService.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        userService.setManualCooling(user, manualCoolingRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cooling/mode/auto")
    public ResponseEntity<?> setAutoCooling(@RequestHeader(value = AuthDto.AUTH_HEADER, defaultValue = "35cf7863-8110-46dd-a92d-99175ea9ed38") UUID userId,
                                            AutoCoolingRequest autoCoolingRequest) {
        User user = userService.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        userService.setAutoCooling(user, autoCoolingRequest);
        return ResponseEntity.ok().build();
    }
}
