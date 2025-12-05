package org.svids.tbankcooldownapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.repository.UserRepo;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;

    public UUID authenticateUser() {
        return userRepo.save(new User()).getId();
    }
}
