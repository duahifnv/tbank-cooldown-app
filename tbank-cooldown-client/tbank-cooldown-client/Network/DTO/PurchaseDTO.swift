//
//  PurchaseDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 05.12.2025.
//

import Foundation

struct PurchaseDTO: Codable {
    let id: String
    let name: String
    let cost: Double
    let category: String
    let date: String
    let status: String
}
