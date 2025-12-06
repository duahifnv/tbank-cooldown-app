package org.svids.tbankcooldownapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.analyze.request.AutoCoolingRequest;
import org.svids.tbankcooldownapi.dto.analyze.request.ManualCoolingRequest;
import org.svids.tbankcooldownapi.entity.AutoCooling;
import org.svids.tbankcooldownapi.entity.ManualCooling;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.repository.AutoCoolingRepo;
import org.svids.tbankcooldownapi.repository.UserRepo;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final ManualCoolingService manualCoolingService;

    private final AutoCoolingRepo autoCoolingRepo;
    private final AutoCoolingService autoCoolingService;

    public UUID authenticateUser() {
        User user = userRepo.save(new User());

        manualCoolingService.initializeCooling(user);
        autoCoolingService.initializeCooling(user);
        return user.getId();
    }

    public Optional<User> findById(UUID userId) {
        return userRepo.findById(userId);
    }

    @Transactional
    public void setManualCooling(User user, ManualCoolingRequest request) {
        ManualCooling manualCooling = manualCoolingService.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        manualCoolingService.updateCooling(manualCooling, request);

        user.setAutoCooling(false);
        userRepo.save(user);
    }

    public void setAutoCooling(User user, AutoCoolingRequest request) {
        AutoCooling autoCooling = autoCoolingRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        user.setAutoCooling(true);
        userRepo.save(user);

        autoCooling.setMonthBudget(request.monthBudget());
        autoCooling.setMonthSalary(request.monthSalary());
        autoCooling.setTotalSavings(request.totalSavings());
        autoCoolingRepo.save(autoCooling);
    }
}
