//
//  PurchaseStatus.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation

// MARK: - PurchaseStatus
enum PurchaseStatus: String, CaseIterable, Hashable {
    case all = "Все"
    case purchased = "Куплено"
    case cancalled = "Отменено"
    case wished = "Желаемое"
}
