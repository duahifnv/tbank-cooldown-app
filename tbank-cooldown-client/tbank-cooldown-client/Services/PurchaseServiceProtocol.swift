//
//  PurchaseServiceProtocol.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 05.12.2025.
//

import Combine
import Foundation

// MARK: - PurchaseServiceProtocol
protocol PurchaseServiceProtocol: AnyObject {
    func getAllPurchases() -> AnyPublisher<[Purchase], Never>
    func analyzePurchase(
        name: String,
        price: Double,
        category: PurchaseCategory
    ) -> AnyPublisher<PurchaseAnalysisResult?, Never>
    
    func createPurchase(
        name: String,
        price: Double,
        category: PurchaseCategory,
        status: PurchaseStatus,
        coolingTimeout: Int?
    ) -> AnyPublisher<Purchase?, Never>
    
    
    func getWishedPurchases() -> AnyPublisher<[WishedPurchase], Never>
    
    func updateWishedPurchaseStatus(id: String,
                                        to status: PurchaseStatus) -> AnyPublisher<Void, Never>
    
    func parseProductLink(_ url: String) -> AnyPublisher<ParsedProduct?, Never>
}
