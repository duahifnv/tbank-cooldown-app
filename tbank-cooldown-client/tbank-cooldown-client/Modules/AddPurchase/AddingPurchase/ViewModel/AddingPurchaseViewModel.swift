import Foundation
import Combine

// MARK: - AddingPurchaseViewModel
final class AddingPurchaseViewModel: ObservableObject {
    // MARK: - Properties
    @Published var textNamePurchase: String
    @Published var textPricePurchase: String
    @Published var categoryPurchase: PurchaseCategory?
    
    /// Результат анализа (для экрана результата)
    @Published var analysisResult: PurchaseAnalysisResult?
    
    /// Можно использовать для показа прогресса
    @Published var isLoading: Bool = false
    
    /// Текст ошибки валидации, если нужно показать пользователю
    @Published var validationError: String?
    
    /// Категории, доступные для выбора (без .all)
    let categories: [PurchaseCategory] = PurchaseCategory.allCases.filter { $0 != .all }
    
    private var cancellables = Set<AnyCancellable>()
    
    // MARK: - Services
    private let purchaseService: PurchaseServiceProtocol
    
    // MARK: - Init
    init(purchase: Purchase?, purchaseService: PurchaseServiceProtocol) {
        self.textNamePurchase = purchase?.name ?? ""
        
        if let purchase = purchase {
            self.textPricePurchase = "\(purchase.price)"
            self.categoryPurchase = purchase.category
        } else {
            self.textPricePurchase = ""
            self.categoryPurchase = nil        // <-- вместо .none
        }
        
        self.purchaseService = purchaseService
    }
    
    // MARK: - Methods
    func didTapCategory(_ category: PurchaseCategory) {
        categoryPurchase = category
        validationError = nil
    }
    
    func didTapAddPurchase() {
        // Валидация
        let trimmedName = textNamePurchase.trimmingCharacters(in: .whitespacesAndNewlines)
        
        guard !trimmedName.isEmpty else {
            validationError = "Введите название покупки"
            return
        }
        
        // Заменяем запятую на точку, чтобы не падать на русской раскладке
        let priceString = textPricePurchase
            .replacingOccurrences(of: " ", with: "")
            .replacingOccurrences(of: ",", with: ".")
        
        guard let price = Double(priceString), price > 0 else {
            validationError = "Введите корректную сумму покупки"
            return
        }
        
        guard let category = categoryPurchase else {
            validationError = "Выберите категорию"
            return
        }
        
        // Всё ок — чистим ошибку и старый результат
        validationError = nil
        analysisResult = nil
        isLoading = true
        
        purchaseService
            .analyzePurchase(name: trimmedName, price: price, category: category)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self else { return }
                self.isLoading = false
                self.analysisResult = result
                print("🟩 [AddingPurchaseViewModel] got analysis result: \(String(describing: result))")
            }
            .store(in: &cancellables)
    }
}

