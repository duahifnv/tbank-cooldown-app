package org.svids.tbankcooldownapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.entity.Purchase;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;
import org.svids.tbankcooldownapi.entity.User;
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
}
