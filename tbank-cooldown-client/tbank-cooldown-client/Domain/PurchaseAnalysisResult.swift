//
//  PurchaseAnalysisResult.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

struct PurchaseAnalysisResult {
    let isCooling: Bool
    let coolingTimeout: Int
    let autoCoolingEnabled: Bool
    let bannedCategory: Bool
    
    // smart-режим (auto)
    let monthBudget: Double?
    let totalSavings: Double?
    let monthSalary: Double?
    
    // manual-режим
    let minCost: Double?
    let maxCost: Double?
}
