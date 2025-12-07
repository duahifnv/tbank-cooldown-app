//
//  ParseProductResponseDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//

import Foundation

struct ParseProductResponseDTO: Decodable {
    let name: String
    let price: Double
    let category: String
    let store: String
}
