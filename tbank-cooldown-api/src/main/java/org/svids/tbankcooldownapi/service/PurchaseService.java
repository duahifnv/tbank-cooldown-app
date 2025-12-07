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
import org.svids.tbankcooldownapi.repository.PurchaseRepo;
import org.svids.tbankcooldownapi.service.calculator.BudgetData;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepo purchaseRepo;

    private final UserService userService;
    private final AutoCoolingService autoCoolingService;
    private final ManualCoolingService manualCoolingService;

    public List<Purchase> getPurchases(UUID userId, Set<PurchaseStatus> statuses) {
        return purchaseRepo.findAllByStatusInAndUser_Id(statuses, userId);
    }

    public UUID savePurchase(UUID userId, Purchase entity) {
        User user = userService.findById(userId).orElseThrow(
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

    public PurchaseAnalysisResult analyzePurchase(User user, PurchaseAnalysisRequest request) {
        UUID userId = user.getId();
        boolean isAutoCoolingEnabled = isAutoCoolingEnabled(userId);

        // Выбрана одна из забаненных категорий
        if (user.getBannedCategories().contains(request.category())) {
            return PurchaseAnalysisResult.withBannedCategory();
        }

        if (isAutoCoolingEnabled) {
            AutoCooling autoCooling = autoCoolingService.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
            var budgetData = new BudgetData(autoCooling.getMonthBudget(), autoCooling.getTotalSavings(), autoCooling.getMonthSalary());

            int comfortableDays = autoCoolingService.calculateDays(budgetData, request.cost());
            CoolingData coolingData = new AutoCoolingData(autoCooling.getMonthBudget(), autoCooling.getTotalSavings(), autoCooling.getMonthSalary());

            return comfortableDays != 0 ?
                    PurchaseAnalysisResult.withCooling(true, coolingData, comfortableDays) :
                    PurchaseAnalysisResult.withoutCooling(true, coolingData);
        } else {
            ManualCooling manualCooling = manualCoolingService.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

            CoolingData coolingData = new ManualCoolingData(manualCooling.getMinCost(), manualCooling.getMaxCost());

            return isManualCooling(request, manualCooling) ?
                    PurchaseAnalysisResult.withCooling(false, coolingData, manualCooling.getCoolingTimeout()) :
                    PurchaseAnalysisResult.withoutCooling(false, coolingData);
        }
    }

    private boolean isAutoCoolingEnabled(UUID userId) {
        return userService.findById(userId).map(User::isAutoCoolingEnabled).orElse(false);
    }

    private boolean isManualCooling(PurchaseAnalysisRequest request, ManualCooling manualCooling) {
        return request.cost().compareTo(manualCooling.getMinCost()) >= 0 && request.cost().compareTo(manualCooling.getMaxCost()) < 0;
    }
}
