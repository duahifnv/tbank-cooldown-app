package org.svids.tbankcooldownapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.svids.tbankcooldownapi.dto.purchase.CompletedPurchases;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseRequest;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedPurchases;
import org.svids.tbankcooldownapi.entity.Purchase;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {
    @Mapping(source = "purchasedAt", target = "date")
    CompletedPurchases.PurchaseDto toDto(Purchase purchase);
    List<CompletedPurchases.PurchaseDto> toDtos(List<Purchase> purchases);

    @Mapping(source = "purchasedAt", target = "wishedDate")
    @Mapping(target = "coolingTimeout", expression = "java(new java.util.Random().nextInt(0, 10000))") // todo: временно
    WishedPurchases.PurchaseDto toWishedDto(Purchase purchase);
    List<WishedPurchases.PurchaseDto> toWishedDtos(List<Purchase> purchases);

    Purchase toEntity(PurchaseRequest request);
}
