package org.svids.tbankcooldownapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.svids.tbankcooldownapi.dto.profile.ManualCoolingDto;
import org.svids.tbankcooldownapi.entity.ManualCooling;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.repository.ManualCoolingRepo;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManualCoolingService {
    private final ManualCoolingRepo manualCoolingRepo;

    public void initializeCooling(User user) {
        ManualCooling entity = new ManualCooling();
        entity.setUser(user);
        manualCoolingRepo.save(entity);
    }

    public Optional<ManualCooling> findByUserId(UUID userId) {
        return manualCoolingRepo.findByUser_Id(userId);
    }

    public void updateCooling(ManualCooling manualCooling, ManualCoolingDto request) {
        manualCooling.setMinCost(request.minCost());
        manualCooling.setMaxCost(request.maxCost());
        manualCooling.setCoolingTimeout(request.coolingTimeout());
        manualCoolingRepo.save(manualCooling);
    }
}
