package org.svids.tbankcooldownapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.svids.tbankcooldownapi.dto.purchase.CompletedPurchases;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseRequest;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedPurchases;
import org.svids.tbankcooldownapi.entity.Purchase;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    @Mapping(source = "purchasedAt", target = "date")
    CompletedPurchases.PurchaseDto toDto(Purchase purchase);
    List<CompletedPurchases.PurchaseDto> toDtos(List<Purchase> purchases);

    @Mapping(source = "purchasedAt", target = "wishedDate")
    @Mapping(target = "coolingTimeout", expression = "java(calculateTimeout(purchase))")
    WishedPurchases.PurchaseDto toWishedDto(Purchase purchase);
    List<WishedPurchases.PurchaseDto> toWishedDtos(List<Purchase> purchases);

    Purchase toEntity(PurchaseRequest request);

    default Integer calculateTimeout(Purchase purchase) {
        if (purchase.getStatus() != PurchaseStatus.WISHED) {
            return null;
        }

        if (purchase.getCoolingTimeout() == null || purchase.getCoolingTimeout() <= 0) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime purchaseTime = purchase.getPurchasedAt();

        // Если purchasedAt в будущем (например, запланированная покупка)
        if (purchaseTime.isAfter(now)) {
            return purchase.getCoolingTimeout();
        }

        // Рассчитываем, сколько времени прошло с момента создания желаемой покупки
        Duration timePassed = Duration.between(purchaseTime, now);
        long daysPassed = timePassed.toDays();

        // Если прошло больше времени, чем coolingTimeout, то охлаждение закончилось
        if (daysPassed >= purchase.getCoolingTimeout()) {
            return null;
        }

        int remainingDays = purchase.getCoolingTimeout() - (int) daysPassed;
        return Math.max(0, remainingDays);
    }
}
