//
//  HistoryViewModel.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
import Combine

// MARK: - HistoryViewModel
final class HistoryViewModel: ObservableObject {
    @Published var selectedCategory: PurchaseCategory = .all
    @Published var selectedStatus: PurchaseStatus = .all
    @Published var selectedSort: Sort = .date
    
    /// Все покупки как пришли с сервиса
    @Published var purchases: [Purchase] = []
    
    /// Покупки после применения фильтров + сортировки
    var filteredPurchases: [Purchase] {
        var result = purchases
        
        // Фильтр по категории
        if selectedCategory != .all {
            result = result.filter { $0.category == selectedCategory }
        }
        
        // Фильтр по статусу
        if selectedStatus != .all {
            result = result.filter { $0.status == selectedStatus }
        }
        
        // Сортировка
        switch selectedSort {
        case .date:
            // сортируем по дате (новые сверху)
            result.sort { lhs, rhs in
                let lDate = Self.parseDate(lhs.date)
                let rDate = Self.parseDate(rhs.date)
                return lDate > rDate
            }
            
        case .price:
            // по цене по убыванию (дороже выше)
            result.sort { $0.price > $1.price }
            
        case .category:
            // сначала по категории (по алфавиту), внутри — по имени
            result.sort { lhs, rhs in
                if lhs.category == rhs.category {
                    return lhs.name < rhs.name
                } else {
                    return lhs.category.rawValue < rhs.category.rawValue
                }
            }
        }
        
        return result
    }
    
    var cancellables = Set<AnyCancellable>()
    
    let categories = PurchaseCategory.allCases
    let statuses = PurchaseStatus.allCases
    let sorts = Sort.allCases
    
    // MARK: - Services
    private let purchaseService: PurchaseServiceProtocol
    
    // MARK: - Init
    init(purchaseService: PurchaseServiceProtocol) {
        self.purchaseService = purchaseService
        
        //self.purchases = Purchase.mockData
    }
    
    // MARK: - Methods
    func didAppear() {
        purchaseService
            .getAllPurchases()
            .sink { [weak self] purchases in
                self?.purchases = purchases
            }
            .store(in: &cancellables)
    }
    
    // MARK: - Helpers
    
    /// Парсим дату из строки. Подстрой формат под свой бэк.
    private static func parseDate(_ string: String) -> Date {
        // если у тебя ISO8601: "2025-12-06T12:34:56Z" — можно использовать ISO8601DateFormatter
        if let date = iso8601Formatter.date(from: string) {
            return date
        }
        
        // сюда можно добавить DateFormatter с кастомным форматом, если нужно
        return .distantPast
    }
    
    private static let iso8601Formatter: ISO8601DateFormatter = {
        let f = ISO8601DateFormatter()
        f.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        return f
    }()
}
