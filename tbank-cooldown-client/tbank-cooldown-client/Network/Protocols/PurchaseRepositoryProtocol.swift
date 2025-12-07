//
//  PurchaseRepositoryProtocol.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
import Combine

protocol PurchaseRepositoryProtocol: AnyObject {
    func fetchPurchases() -> AnyPublisher<[PurchaseDTO], Never>
    func analyzePurchase(_ body: PurchaseAnalysisRequestDTO) -> AnyPublisher<PurchaseAnalysisResponseDTO?, Never>
    func createPurchase(_ body: CreatePurchaseRequestDTO) -> AnyPublisher<PurchaseDTO?, Never>
    func fetchWishedPurchases() -> AnyPublisher<[WishedPurchaseDTO], Never>
    func updateWishedStatus(purchaseId: String,
                                status: String) -> AnyPublisher<Void, Never>
    func parseProduct(from urlString: String) -> AnyPublisher<ParseProductResponseDTO?, Never>
}
