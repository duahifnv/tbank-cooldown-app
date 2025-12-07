//
//  CreatePurchaseRequestDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
struct CreatePurchaseRequestDTO: Encodable {
    let name: String
    let cost: Double
    let category: String
    let status: String
    let coolingTimeout: Int   // НЕ optional
}
