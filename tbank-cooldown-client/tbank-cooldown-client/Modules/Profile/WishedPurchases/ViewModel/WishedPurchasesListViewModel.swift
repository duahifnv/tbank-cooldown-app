//
//  WishedPurchasesListViewModel.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
import Combine

// MARK: - WishedPurchasesListViewModel
final class WishedPurchasesListViewModel: ObservableObject {
    @Published var selectedCategory: PurchaseCategory = .all
    @Published var selectedSort: Sort = .date
    
    /// Все «желанные» покупки как пришли с сервиса
    @Published var purchases: [WishedPurchase] = []
    
    /// Покупки после применения фильтра по категории и сортировки
    var filteredPurchases: [WishedPurchase] {
        var result = purchases
        
        // Фильтр по категории
        if selectedCategory != .all {
            result = result.filter { $0.category == selectedCategory }
        }
        
        switch selectedSort {
        case .date:
            // 1) сортируем по дате (новые сверху)
            result.sort { lhs, rhs in
                let lDate = Self.parseDate(lhs.wishedDate)
                let rDate = Self.parseDate(rhs.wishedDate)
                return lDate > rDate
            }
            
            // 2) поверх этого поднимаем "остывшие" (coolingTimeout == nil)
            let finished = result.filter { $0.coolingTimeout == nil }
            let cooling  = result.filter { $0.coolingTimeout != nil }
            return finished + cooling
            
        case .price:
            // просто по цене по убыванию
            result.sort { $0.price > $1.price }
            return result
            
        case .category:
            // по категории, внутри — по имени
            result.sort { lhs, rhs in
                if lhs.category == rhs.category {
                    return lhs.name < rhs.name
                } else {
                    return lhs.category.rawValue < rhs.category.rawValue
                }
            }
            return result
        }
    }
    
    private var cancellables = Set<AnyCancellable>()
    
    let categories = PurchaseCategory.allCases
    let sorts = Sort.allCases
    
    // MARK: - Services
    private let purchaseService: PurchaseServiceProtocol
    
    // MARK: - Init
    init(purchaseService: PurchaseServiceProtocol) {
        self.purchaseService = purchaseService
    }
    
    // MARK: - Methods
    func didAppear() {
        purchaseService
            .getWishedPurchases()                 // <-- теперь берём только WISHED
            .receive(on: DispatchQueue.main)
            .sink { [weak self] purchases in
                self?.purchases = purchases
            }
            .store(in: &cancellables)
    }
    
    func didTapBuy(_ purchase: WishedPurchase) {
        purchaseService
            .updateWishedPurchaseStatus(id: purchase.id, to: .purchased)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.purchases.removeAll { $0.id == purchase.id }
            }
            .store(in: &cancellables)
    }
    
    func didTapCancel(_ purchase: WishedPurchase) {
        purchaseService
            .updateWishedPurchaseStatus(id: purchase.id, to: .cancalled)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.purchases.removeAll { $0.id == purchase.id }
            }
            .store(in: &cancellables)
    }
    
    // MARK: - Helpers
    
    private static func parseDate(_ string: String) -> Date {
        if let date = iso8601Formatter.date(from: string) {
            return date
        }
        return .distantPast
    }
    
    private static let iso8601Formatter: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f
    }()
}
