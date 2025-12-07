//
//  ManualCoolingSettings.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
struct ManualCoolingDTO: Codable {
    var minPrice: Double
    var maxPrice: Double
    var coolingTimeout: Double      // ОДНО число дней
}
