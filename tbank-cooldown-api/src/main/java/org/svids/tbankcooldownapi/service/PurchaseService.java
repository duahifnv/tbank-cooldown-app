package org.svids.tbankcooldownapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.analyze.data.AutoCoolingData;
import org.svids.tbankcooldownapi.dto.analyze.data.CoolingData;
import org.svids.tbankcooldownapi.dto.analyze.data.ManualCoolingData;
import org.svids.tbankcooldownapi.dto.analyze.data.PurchaseAnalysisResult;
import org.svids.tbankcooldownapi.dto.analyze.request.PurchaseAnalysisRequest;
import org.svids.tbankcooldownapi.entity.*;
import org.svids.tbankcooldownapi.repository.AutoCoolingRepo;
import org.svids.tbankcooldownapi.repository.ManualCoolingRepo;
import org.svids.tbankcooldownapi.repository.PurchaseRepo;
import org.svids.tbankcooldownapi.repository.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepo purchaseRepo;
    private final UserRepo userRepo;

    private final AutoCoolingRepo autoCoolingRepo;
    private final ManualCoolingRepo manualCoolingRepo;
    private final AutoCoolingService autoCoolingService;

    public List<Purchase> getPurchases(UUID userId, Set<PurchaseStatus> statuses) {
        return purchaseRepo.findAllByStatusInAndUser_Id(statuses, userId);
    }

    public UUID savePurchase(UUID userId, Purchase entity) {
        User user = userRepo.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)); // Прошел мимо фильтра без userId
        entity.setUser(user);
        return purchaseRepo.save(entity).getId();
    }

    public Optional<Purchase> getPurchase(UUID purchaseId, UUID userId) {
        return purchaseRepo.findByIdAndUser_Id(userId, purchaseId);
    }

    public void updatePurchaseStatus(Purchase purchase, PurchaseStatus purchaseStatus) {
        purchase.setStatus(purchaseStatus);
        purchaseRepo.save(purchase);
    }

    public PurchaseAnalysisResult analyzePurchase(UUID userId, PurchaseAnalysisRequest request) {
        boolean isAutoCoolingEnabled = isAutoCoolingEnabled(userId);

        if (isAutoCoolingEnabled) {
            AutoCooling autoCooling = autoCoolingRepo.findByUser_Id(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

            int comfortableDays = autoCoolingService.calculateComfortableDays(autoCooling, request.cost());
            CoolingData coolingData = new AutoCoolingData(autoCooling.getMonthBudget(), autoCooling.getTotalSavings(), autoCooling.getMonthSalary());

            return comfortableDays != 0 ?
                    PurchaseAnalysisResult.withCooling(true, coolingData, comfortableDays) :
                    PurchaseAnalysisResult.withoutCooling(true, coolingData);
        } else {
            ManualCooling manualCooling = manualCoolingRepo.findByUser_Id(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

            CoolingData coolingData = new ManualCoolingData(manualCooling.getMinCost(), manualCooling.getMaxCost());

            return isManualCooling(request, manualCooling) ?
                    PurchaseAnalysisResult.withCooling(false, coolingData, manualCooling.getCoolingTimeout()) :
                    PurchaseAnalysisResult.withoutCooling(false, coolingData);
        }
    }

    private boolean isAutoCoolingEnabled(UUID userId) {
        return userRepo.findById(userId).map(User::isAutoCoolingEnabled).orElse(false);
    }

    private boolean isManualCooling(PurchaseAnalysisRequest request, ManualCooling manualCooling) {
        return request.cost().compareTo(manualCooling.getMinCost()) >= 0 && request.cost().compareTo(manualCooling.getMaxCost()) < 0;
    }
}
