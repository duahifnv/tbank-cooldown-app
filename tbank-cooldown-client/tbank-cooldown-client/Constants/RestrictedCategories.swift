//
//  RestrictedCategories.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

let restrictedCategories = PurchaseCategory
    .allCases
    .filter { $0 != .all }
