//
//  UpdateWishedStatusRequestDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//

import Foundation

// PurchaseDTOs.swift (или туда же, где остальные DTO)

struct UpdateWishedStatusRequestDTO: Encodable {
    let status: String   // "PURCHASED" или "CANCELLED"
}
