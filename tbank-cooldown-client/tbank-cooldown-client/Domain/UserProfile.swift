//
//  UserProfile.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//

import Foundation

struct UserProfile {
    let nickname: String
    let about: String
    let autoCoolingEnabled: Bool
    let manualCooling: ManualCoolingDTO?
    let autoCooling: AutoCoolingDTO?
    let bannedCategories: Set<PurchaseCategory>
}
