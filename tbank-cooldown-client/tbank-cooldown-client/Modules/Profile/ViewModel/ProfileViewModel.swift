//
//  ProfileViewModel.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
import Combine

final class ProfileViewModel: ObservableObject {
    
    
    @Published var wishedPurchases: [WishedPurchase] = []
    @Published var notificationsEnabled: Bool = false
    @Published var reminderFrequency: ReminderFrequency = .daily
    @Published var excludedCategories: [PurchaseCategory] = []
    
    
    
    // MARK: - SERVICES
    private let purchaseService: PurchaseServiceProtocol
    
    
    init(purchaseService: PurchaseServiceProtocol) {
        self.purchaseService = purchaseService
        
        NotificationManager.shared.requestAuthorization { granted in
            print("🔔 permission granted: \(granted)")
        }
    }
    
    // MARK: - COOLING
    @Published var mode: CoolingMode = .manual
    @Published var manual = ManualCoolingDTO(
        minPrice: 5_000,
        maxPrice: 50_000,
        coolingTimeout: 7
    )
    @Published var smart = SmartCoolingSettings(
        monthBudget: 20_000,
        totalSavings: 120_000,
        monthSalary: 80_000
    )
    
    var sortedPriceRange: ClosedRange<Double> {
        let low  = min(manual.minPrice, manual.maxPrice)
        let high = max(manual.minPrice, manual.maxPrice)
        return low...high
    }
    
    
    // MARK: - RESTRICTED CATEGORIES
    @Published var restrictedCategories: Set<PurchaseCategory> = []
    
    
    // MARK: - ABOUT ME
    @Published var username: String = ""
    @Published var personalNote: String = ""
    
    // MARK: - Computed properties
    
    /// Готовая модель для ручного режима охлаждения
    var manualCoolingModel: ManualCoolingDTO {
        let minPrice = min(manual.minPrice, manual.maxPrice)
        let maxPrice = max(manual.minPrice, manual.maxPrice)
        
        return ManualCoolingDTO(
            minPrice: minPrice,
            maxPrice: maxPrice,
            coolingTimeout: round(manual.coolingTimeout)
        )
    }
    
    /// Готовая модель для умного режима охлаждения
    var smartCoolingModel: SmartCoolingSettings {
        SmartCoolingSettings(
            monthBudget: round(smart.monthBudget),
            totalSavings: round(smart.totalSavings),
            monthSalary: round(smart.monthSalary)
        )
    }
    
    private var cancellables = Set<AnyCancellable>()
    
    func didAppear() {
        purchaseService
            .getWishedPurchases()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] purchases in
                guard let self = self else { return }

                self.wishedPurchases = purchases

                NotificationManager.shared.cancelAllPurchaseReminders()

                let activePurchases = purchases.filter { $0.coolingTimeout != nil }

                activePurchases.forEach { purchase in
                    NotificationManager.shared.scheduleReminder(
                        for: purchase,
                        frequency: .daily,
                        debug: true
                    )
                }
            }
            .store(in: &cancellables)
    }
}
