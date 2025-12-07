//
//  WishedPurchaseDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

struct WishedPurchaseDTO: Codable {
    let id: String
    let name: String
    let cost: Double
    let category: String
    let wishedDate: String
    let coolingTimeout: Int?   // null, если охлаждение завершено
}
