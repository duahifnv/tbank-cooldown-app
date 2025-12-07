import Foundation
import Combine

// MARK: - PurchaseService
final class PurchaseService: PurchaseServiceProtocol {
    
    // MARK: - Dependencies
    private let repository: PurchaseRepositoryProtocol
    
    // MARK: - Init
    init(repository: PurchaseRepositoryProtocol) {
        self.repository = repository
    }
    
    
    /// маппинг категории из строки бэка в твой enum
    private static func categoryFromBackend(_ raw: String) -> PurchaseCategory? {
        switch raw {
        case "ELECTRONICS": return .electronics
        case "CLOTHING":    return .clothing
        case "FOOD":        return .food
        case "HOME":        return .home
        case "OTHER":       return .other
        default:            return nil
        }
    }
    
    // MARK: - Public API
    
    /// Загрузить все покупки (domain-модель для VM)
    func getAllPurchases() -> AnyPublisher<[Purchase], Never> {
        print("🟦 [PurchaseService] getAllPurchases() called")
        
        return repository
            .fetchPurchases() // AnyPublisher<[PurchaseDTO], Never>
            .map { dtos in
                dtos.map { dto in
                    let category = Self.mapCategory(dto.category)
                    let status   = Self.mapStatus(dto.status)
                    
                    print("""
                    🟩 [PurchaseService] map DTO -> Purchase:
                        id=\(dto.id)
                        name=\(dto.name)
                        categoryRaw=\(dto.category) -> \(category)
                        statusRaw=\(dto.status) -> \(status)
                        cost=\(dto.cost)
                        date=\(dto.date)
                    """)
                    
                    return Purchase(
                        name: dto.name,
                        price: dto.cost,
                        category: category,
                        date: dto.date,
                        status: status
                    )
                }
            }
            .handleEvents(receiveOutput: { purchases in
                print("🟩 [PurchaseService] mapped \(purchases.count) purchases for VM")
            })
            .eraseToAnyPublisher()
    }
    
    func analyzePurchase(
        name: String,
        price: Double,
        category: PurchaseCategory
    ) -> AnyPublisher<PurchaseAnalysisResult?, Never> {
        
        let backendCategory = Self.backendCategory(from: category)
        let requestDTO = PurchaseAnalysisRequestDTO(
            name: name,
            cost: price,
            category: backendCategory
        )
        
        print("""
        🟦 [PurchaseService] analyzePurchase() called
            name: \(name)
            price: \(price)
            category: \(category) -> \(backendCategory)
        """)
        
        return repository
            .analyzePurchase(requestDTO)          // AnyPublisher<PurchaseAnalysisResponseDTO?, Never>
            .map { dto -> PurchaseAnalysisResult? in
                guard let dto = dto else {
                    print("🟠 [PurchaseService] analyzePurchase: response DTO is nil")
                    return nil
                }
                
                let cd = dto.coolingData      // может быть nil
                
                let result = PurchaseAnalysisResult(
                    isCooling: dto.isCooling,
                    coolingTimeout: dto.coolingTimeout,
                    autoCoolingEnabled: dto.autoCoolingEnabled,
                    bannedCategory: dto.bannedCategory,
                    
                    // smart-режим (autoCoolingEnabled == true / isCooling == true)
                    monthBudget: dto.isCooling ? cd?.monthBudget : nil,
                    totalSavings: dto.isCooling ? cd?.totalSavings : nil,
                    monthSalary: dto.isCooling ? cd?.monthSalary : nil,
                    
                    // manual-режим (isCooling == false)
                    minCost: dto.isCooling ? nil : cd?.minCost,
                    maxCost: dto.isCooling ? nil : cd?.maxCost
                )
                
                print("""
                🟩 [PurchaseService] analyzePurchase mapped to domain:
                    isCooling: \(result.isCooling)
                    coolingTimeout: \(result.coolingTimeout)
                    autoCoolingEnabled: \(result.autoCoolingEnabled)
                    bannedCategory: \(result.bannedCategory)
                    monthBudget: \(String(describing: result.monthBudget))
                    totalSavings: \(String(describing: result.totalSavings))
                    monthSalary: \(String(describing: result.monthSalary))
                    minCost: \(String(describing: result.minCost))
                    maxCost: \(String(describing: result.maxCost))
                """)
                
                return result
            }
            .eraseToAnyPublisher()
    }
    
    func parseProductLink(_ url: String) -> AnyPublisher<ParsedProduct?, Never> {
        repository
            .parseProduct(from: url)
            .map { dto -> ParsedProduct? in
                guard let dto else {
                    print("🟠 [PurchaseService] parseProductLink: DTO is nil")
                    return nil
                }

                let category = Self.categoryFromBackend(dto.category)

                let model = ParsedProduct(
                    name: dto.name,
                    price: dto.price,
                    category: category,
                    store: dto.store
                )

                print("""
                🟩 [PurchaseService] parseProductLink mapped:
                    name: \(model.name)
                    price: \(model.price)
                    category: \(String(describing: model.category))
                    store: \(model.store)
                """)

                return model
            }
            .eraseToAnyPublisher()
    }
    
    // MARK: - Создать покупку (domain-уровень)
    func createPurchase(
        name: String,
        price: Double,
        category: PurchaseCategory,
        status: PurchaseStatus,
        coolingTimeout: Int?
    ) -> AnyPublisher<Purchase?, Never> {
        
        let backendCategory = Self.backendCategory(from: category)
        let backendStatus   = Self.backendStatus(from: status)
        
        // Если статус WISHED — используем переданный timeout (или 0),
        // для остальных статусов просто отправляем 0.
        let timeoutToSend: Int
        if status == .wished {
            timeoutToSend = coolingTimeout ?? 0
        } else {
            timeoutToSend = 0
        }
        
        let body = CreatePurchaseRequestDTO(
            name: name,
            cost: price,
            category: backendCategory,
            status: backendStatus,
            coolingTimeout: timeoutToSend
        )
        
        print("""
        🟦 [PurchaseService] createPurchase() called
            name: \(name)
            price: \(price)
            category: \(category) -> \(backendCategory)
            status: \(status) -> \(backendStatus)
            coolingTimeout: \(timeoutToSend)
        """)
        
        return repository
            .createPurchase(body)  // AnyPublisher<PurchaseDTO?, Never>
            .map { dto -> Purchase? in
                guard let dto = dto else { return nil }
                
                return Purchase(
                    name: dto.name,
                    price: dto.cost,
                    category: Self.mapCategory(dto.category),
                    date: dto.date,
                    status: Self.mapStatus(dto.status)
                )
            }
            .eraseToAnyPublisher()
    }
    
    // MARK: - Helpers для статусов
    private static func backendStatus(from status: PurchaseStatus) -> String {
        switch status {
        case .all:
            return "ALL"          // если вообще нужен
        case .purchased:
            return "PURCHASED"
        case .cancalled:
            return "CANCELLED"
        case .wished:
            return "WISHED"
        }
    }
    
    // MARK: - Mapping helpers
    
    /// Маппинг категорий из raw-строки бэка в доменный enum
    private static func mapCategory(_ raw: String) -> PurchaseCategory {
        let upper = raw.uppercased()
        
        switch upper {
        case "CLOTHING":
            return .clothing
        case "FOOD":
            return .food
        case "HOME":
            return .home
        case "ELECTRONICS":
            // если хочешь отдельную категорию — добавь в enum;
            // пока кладём электронику в .home или .other — на твой вкус
            return .home
        case "OTHER":
            return .other
        default:
            print("🟠 [PurchaseService] Unknown category raw='\(raw)', fallback .other")
            return .other
        }
    }
    
    /// Маппинг статуса из raw-строки бэка в доменный enum
    private static func mapStatus(_ raw: String) -> PurchaseStatus {
        let upper = raw.uppercased()
        
        switch upper {
        case "PURCHASED":
            return .purchased
        case "CANCELLED", "CANCELED":
            return .cancalled
        case "ALL":
            return .all
        default:
            print("🟠 [PurchaseService] Unknown status raw='\(raw)', fallback .all")
            return .all
        }
    }
    
    /// Маппинг доменной категории → строка, которую ждёт бэк в /analysis
    private static func backendCategory(from category: PurchaseCategory) -> String {
        switch category {
        case .clothing:
            return "CLOTHING"
        case .electronics:
            return "ELECTRONICS"
        case .food:
            return "FOOD"
        case .home:
            return "HOME"        // или "ELECTRONICS" — подстрой под схему бэка
        case .other, .all:
            return "OTHER"
        }
    }
    
    func getWishedPurchases() -> AnyPublisher<[WishedPurchase], Never> {
            repository
                .fetchWishedPurchases()                 // [WishedPurchaseDTO]
                .map { dtos in
                    dtos.map { dto in
                        WishedPurchase(
                            id: dto.id,
                            name: dto.name,
                            price: dto.cost,
                            category: Self.mapCategory(dto.category),
                            wishedDate: dto.wishedDate,
                            coolingTimeout: dto.coolingTimeout
                        )
                    }
                }
                .eraseToAnyPublisher()
        }
    
    func updateWishedPurchaseStatus(id: String,
                                        to status: PurchaseStatus) -> AnyPublisher<Void, Never> {
            let backendStatus: String
            switch status {
            case .purchased:
                backendStatus = "PURCHASED"
            case .cancalled:
                backendStatus = "CANCELLED"
            default:
                assertionFailure("Unsupported status for wished update: \(status)")
                backendStatus = "PURCHASED"
            }
            
            print("""
            🟦 [PurchaseService] updateWishedPurchaseStatus
                id: \(id)
                status: \(backendStatus)
            """)
            
            return repository.updateWishedStatus(purchaseId: id, status: backendStatus)
        }
}
