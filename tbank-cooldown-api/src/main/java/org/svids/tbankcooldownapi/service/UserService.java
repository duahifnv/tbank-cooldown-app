package org.svids.tbankcooldownapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.profile.AutoCoolingDto;
import org.svids.tbankcooldownapi.dto.profile.ManualCoolingDto;
import org.svids.tbankcooldownapi.dto.profile.UserProfileDto;
import org.svids.tbankcooldownapi.entity.AutoCooling;
import org.svids.tbankcooldownapi.entity.ManualCooling;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.mapper.UserMapper;
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
    private final UserMapper userMapper;

    public UUID authenticateUser() {
        User user = userRepo.save(new User());

        manualCoolingService.initializeCooling(user);
        autoCoolingService.initializeCooling(user);
        return user.getId();
    }

    public Optional<User> findById(UUID userId) {
        return userRepo.findById(userId);
    }

    public Optional<UserProfileDto> findProfileById(UUID userId) {
        ManualCooling manualCooling = getManualCooling(userId);
        ManualCoolingDto manualCoolingDto = new ManualCoolingDto(manualCooling.getMinCost(), manualCooling.getMaxCost(), manualCooling.getCoolingTimeout());

        AutoCooling autoCooling = getAutoCooling(userId);
        AutoCoolingDto autoCoolingDto = new AutoCoolingDto(autoCooling.getMonthBudget(), autoCooling.getTotalSavings(), autoCooling.getMonthSalary());

        return userRepo.findById(userId).map(u -> userMapper.toDto(u, manualCoolingDto, autoCoolingDto));
    }

    @Transactional
    public void setManualCooling(User user, ManualCoolingDto request) {
        ManualCooling manualCooling = getManualCooling(user.getId());
        manualCoolingService.updateCooling(manualCooling, request);

        user.setAutoCoolingEnabled(false);
        userRepo.save(user);
    }

    private ManualCooling getManualCooling(UUID userId) {
        return manualCoolingService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private AutoCooling getAutoCooling(UUID userId) {
        return autoCoolingRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public void setAutoCooling(User user, AutoCoolingDto request) {
        AutoCooling autoCooling = getAutoCooling(user.getId());

        user.setAutoCoolingEnabled(true);
        userRepo.save(user);

        autoCooling.setMonthBudget(request.monthBudget());
        autoCooling.setMonthSalary(request.monthSalary());
        autoCooling.setTotalSavings(request.totalSavings());
        autoCoolingRepo.save(autoCooling);
    }

    @Transactional
    public void updateProfile(User user, UserProfileDto request) {
        user.setNickname(request.nickname());
        user.setAbout(request.about());
        user.setBannedCategories(request.bannedCategories());
        user.setAutoCoolingEnabled(request.autoCoolingEnabled());

        setAutoCooling(user, request.autoCooling());
        setManualCooling(user, request.manualCooling());

        userRepo.save(user);
    }
}
