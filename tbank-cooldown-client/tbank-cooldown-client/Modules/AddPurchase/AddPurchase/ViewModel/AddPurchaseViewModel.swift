import Foundation
import Combine

final class AddPurchaseViewModel: AddPurchaseViewModelProtocol, ObservableObject {
    @Published var textLink: String = ""
    @Published var isTextLinkInvalid: Bool = false
    
    // 👉 сюда будем класть результат парсинга
    @Published var parsedProduct: ParsedProduct?
    
    private let purchaseService: PurchaseServiceProtocol
    private var cancellables = Set<AnyCancellable>()
    
    init(purchaseService: PurchaseServiceProtocol) {
        self.purchaseService = purchaseService
    }
    
    func didTapParseLink() {
        let trimmed = textLink.trimmingCharacters(in: .whitespacesAndNewlines)
        
        guard
            !trimmed.isEmpty,
            let url = URL(string: trimmed),
            let scheme = url.scheme,
            scheme == "http" || scheme == "https"
        else {
            isTextLinkInvalid = true
            return
        }
        
        isTextLinkInvalid = false
        
        purchaseService
            .parseProductLink(trimmed)      // AnyPublisher<ParsedProduct?, Never>
            .receive(on: DispatchQueue.main)
            .sink { [weak self] parsed in
                guard let self else { return }
                guard let parsed = parsed else {
                    self.isTextLinkInvalid = true
                    return
                }
                
                self.isTextLinkInvalid = false
                self.parsedProduct = parsed   // 👈 триггерим переход
            }
            .store(in: &cancellables)
    }
    
    func didTapFillManually() { }
}
