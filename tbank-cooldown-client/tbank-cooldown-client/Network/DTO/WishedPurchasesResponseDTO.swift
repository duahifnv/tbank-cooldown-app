//
//  WishedPurchasesResponseDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

struct WishedPurchasesResponseDTO: Codable {
    let purchases: [WishedPurchaseDTO]
}
