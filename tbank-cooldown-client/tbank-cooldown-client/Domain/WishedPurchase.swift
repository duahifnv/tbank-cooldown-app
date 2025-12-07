//
//  WishedPurchase.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

struct WishedPurchase: Hashable {
    let id: String
    let name: String
    let price: Double
    let category: PurchaseCategory
    let wishedDate: String
    let coolingTimeout: Int?
}
