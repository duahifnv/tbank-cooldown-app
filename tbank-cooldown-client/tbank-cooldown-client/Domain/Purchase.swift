//
//  Purchase.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 05.12.2025.
//

import Foundation

struct Purchase: Hashable {
    let id = UUID().uuidString
    let name: String
    let price: Double
    let category: PurchaseCategory
    let date: String
    let status: PurchaseStatus
}
