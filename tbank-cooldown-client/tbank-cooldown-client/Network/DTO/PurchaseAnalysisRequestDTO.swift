//
//  PurchaseAnalysisRequestDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

// MARK: - DTO для запроса анализа
struct PurchaseAnalysisRequestDTO: Encodable {
    let name: String
    let cost: Double
    let category: String
}
