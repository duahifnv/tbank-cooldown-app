//
//  UserProfileDTO.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//

import Foundation

struct UserProfileDTO: Decodable {
    let nickname: String?
    let about: String?
    let bannedCategories: [String]
    let autoCoolingEnabled: Bool
    let manualCooling: ManualCoolingDTO?
    let autoCooling: AutoCoolingDTO?
}
