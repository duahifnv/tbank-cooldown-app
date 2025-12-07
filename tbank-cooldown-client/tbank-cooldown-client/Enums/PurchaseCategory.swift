//
//  PurchaseCategory.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

// MARK: - PurchaseCategory
enum PurchaseCategory: String, CaseIterable, Hashable {
    case all = "Все"
    case electronics = "Электронника"
    case clothing = "Одежда"
    case food = "Еда"
    case home = "Дом"
    case other = "Другое"
}
