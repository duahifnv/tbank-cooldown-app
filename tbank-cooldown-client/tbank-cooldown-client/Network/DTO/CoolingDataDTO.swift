//
//  CoolingDataDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

struct CoolingDataDTO: Decodable {
    // когда isCooling == true
    let monthBudget: Double?
    let totalSavings: Double?
    let monthSalary: Double?
    
    // когда isCooling == false
    let minCost: Double?
    let maxCost: Double?
}
