//
//  PurchaseAnalysisResponseDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

// MARK: - PurchaseAnalysisResponseDTO
struct PurchaseAnalysisResponseDTO: Decodable {
    let isCooling: Bool
    let coolingTimeout: Int
    let autoCoolingEnabled: Bool
    let bannedCategory: Bool
    let coolingData: CoolingDataDTO?   // ВАЖНО: теперь optional
}
