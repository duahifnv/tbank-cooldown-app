//
//  PurchaseResultViewModel.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
import Combine

// MARK: - PurchaseResultViewModel
final class PurchaseResultViewModel {
    var result: PurchaseAnalysisResult
    var purchase: Purchase
    
    private let purchaseService: PurchaseServiceProtocol
    
    var cancellables = Set<AnyCancellable>()
    
    init(result: PurchaseAnalysisResult, purchase: Purchase, purchaseService: PurchaseServiceProtocol) {
        self.result = result
        self.purchase = purchase
        self.purchaseService = purchaseService
        
        print(purchase)
    }
    
    func didTapCancel() {
        
    }
    
    func didTapAddToWishlist() {
        purchaseService.createPurchase(
            name: purchase.name,
            price: purchase.price,
            category: purchase.category,
            status: .wished,          // если добавишь такой кейс
            coolingTimeout: result.isCooling ? result.coolingTimeout : nil
        )
        .receive(on: DispatchQueue.main)
        .sink { [weak self] created in
            // обработать результат / закрыть экран
        }
        .store(in: &cancellables)
    }
    
    func didTapAddToPurchased() {
        purchaseService.createPurchase(
            name: purchase.name,
            price: purchase.price,
            category: purchase.category,
            status: .purchased,          // если добавишь такой кейс
            coolingTimeout: result.isCooling ? result.coolingTimeout : nil
        )
        .receive(on: DispatchQueue.main)
        .sink { [weak self] created in
            // обработать результат / закрыть экран
        }
        .store(in: &cancellables)
    }
}
