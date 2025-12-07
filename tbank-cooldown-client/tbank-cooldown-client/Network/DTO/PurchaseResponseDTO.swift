//
//  PurchaseResponseDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

// MARK: - PurchaseResponseDTO
struct PurchaseResponseDTO: Codable {
    let purchases: [PurchaseDTO]
}
