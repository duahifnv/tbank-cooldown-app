import Foundation
import Combine

// MARK: - PurchaseRepository
final class PurchaseRepository: PurchaseRepositoryProtocol {
    private let baseURL = URL(string: "https://envelope42.ru/api")!
    private let userId  = "35cf7863-8110-46dd-a92d-99175ea9ed38"
    
    // MARK: - Все покупки
    func fetchPurchases() -> AnyPublisher<[PurchaseDTO], Never> {
        var request = URLRequest(url: baseURL.appendingPathComponent("purchases"))
        request.httpMethod = "GET"
        request.setValue("*/*", forHTTPHeaderField: "accept")
        
        let userId = "35cf7863-8110-46dd-a92d-99175ea9ed38" // или UserDefaultsManager.shared.userId
        request.setValue(userId, forHTTPHeaderField: "X-USER-ID")
        
        print("""
        🟦 [PurchaseRepository] fetchPurchases()
            URL: \(request.url?.absoluteString ?? "nil")
            X-USER-ID: \(userId)
        """)
        
        return URLSession.shared.dataTaskPublisher(for: request)
            .tryMap { output in
                guard let http = output.response as? HTTPURLResponse else {
                    print("🟥 [PurchaseRepository] not HTTP response")
                    throw URLError(.badServerResponse)
                }
                
                print("🟦 [PurchaseRepository] statusCode: \(http.statusCode)")
                print("🟦 [PurchaseRepository] data size: \(output.data.count) bytes")
                
                if let text = String(data: output.data, encoding: .utf8) {
                    print("📄 [PurchaseRepository] body:\n\(text)")
                }
                
                guard (200..<300).contains(http.statusCode) else {
                    throw URLError(.badServerResponse)
                }
                
                return output.data
            }
            .decode(type: PurchaseResponseDTO.self, decoder: JSONDecoder())
            .map { $0.purchases }
            .handleEvents(
                receiveOutput: { dtos in
                    print("🟩 [PurchaseRepository] decoded \(dtos.count) PurchaseDTO")
                },
                receiveCompletion: { completion in
                    if case .failure(let error) = completion {
                        print("🟥 [PurchaseRepository] decode error (purchases): \(error)")
                    }
                }
            )
            .replaceError(with: [])
            .eraseToAnyPublisher()
    }
    
    // MARK: - Анализ покупки
    func analyzePurchase(_ body: PurchaseAnalysisRequestDTO) -> AnyPublisher<PurchaseAnalysisResponseDTO?, Never> {
        let url = baseURL.appendingPathComponent("purchases/analysis")
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("*/*", forHTTPHeaderField: "accept")
        
        let userId = "35cf7863-8110-46dd-a92d-99175ea9ed38" // или UserDefaultsManager.shared.userId
        request.setValue(userId, forHTTPHeaderField: "X-USER-ID")
        
        do {
            let data = try JSONEncoder().encode(body)
            request.httpBody = data
            print("""
            🟦 [PurchaseRepository] analyzePurchase() POST
                URL: \(url.absoluteString)
                X-USER-ID: \(userId)
                Body: \(String(data: data, encoding: .utf8) ?? "<encode error>")
            """)
        } catch {
            print("🟥 [PurchaseRepository] encode error: \(error)")
            return Just<PurchaseAnalysisResponseDTO?>(nil).eraseToAnyPublisher()
        }
        
        return URLSession.shared.dataTaskPublisher(for: request)
            .tryMap { output in
                guard let http = output.response as? HTTPURLResponse else {
                    throw URLError(.badServerResponse)
                }
                print("🟦 [PurchaseRepository] analyzePurchase statusCode: \(http.statusCode)")
                
                if let text = String(data: output.data, encoding: .utf8) {
                    print("📄 [PurchaseRepository] analyzePurchase body:\n\(text)")
                }
                
                guard (200..<300).contains(http.statusCode) else {
                    throw URLError(.badServerResponse)
                }
                return output.data
            }
            .decode(type: PurchaseAnalysisResponseDTO.self, decoder: JSONDecoder())
            .map { Optional($0) }
            .handleEvents(receiveOutput: { response in
                print("🟩 [PurchaseRepository] decoded analysis DTO: \(String(describing: response))")
            })
            .replaceError(with: nil)
            .eraseToAnyPublisher()
    }
    
    // MARK: - Создать покупку
        func createPurchase(_ body: CreatePurchaseRequestDTO) -> AnyPublisher<PurchaseDTO?, Never> {
            let url = baseURL.appendingPathComponent("purchases")
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            request.setValue("*/*", forHTTPHeaderField: "accept")
            
            let userId = "35cf7863-8110-46dd-a92d-99175ea9ed38" // или твой UserDefaultsManager
            request.setValue(userId, forHTTPHeaderField: "X-USER-ID")
            
            do {
                let data = try JSONEncoder().encode(body)
                request.httpBody = data
                print("""
                🟦 [PurchaseRepository] createPurchase() POST
                    URL: \(url.absoluteString)
                    X-USER-ID: \(userId)
                    Body: \(String(data: data, encoding: .utf8) ?? "<encode error>")
                """)
            } catch {
                print("🟥 [PurchaseRepository] createPurchase encode error: \(error)")
                return Just<PurchaseDTO?>(nil).eraseToAnyPublisher()
            }
            
            return URLSession.shared.dataTaskPublisher(for: request)
                .tryMap { output in
                    guard let http = output.response as? HTTPURLResponse else {
                        print("🟥 [PurchaseRepository] createPurchase not HTTP response")
                        throw URLError(.badServerResponse)
                    }
                    
                    print("🟦 [PurchaseRepository] createPurchase statusCode: \(http.statusCode)")
                    print("🟦 [PurchaseRepository] createPurchase data size: \(output.data.count) bytes")
                    
                    if let text = String(data: output.data, encoding: .utf8) {
                        print("📄 [PurchaseRepository] createPurchase body:\n\(text)")
                    }
                    
                    guard (200..<300).contains(http.statusCode) else {
                        throw URLError(.badServerResponse)
                    }
                    
                    return output.data
                }
                .decode(type: PurchaseDTO.self, decoder: JSONDecoder())
                .map { Optional($0) }
                .handleEvents(
                    receiveOutput: { dto in
                        print("🟩 [PurchaseRepository] created purchase DTO: \(String(describing: dto))")
                    },
                    receiveCompletion: { completion in
                        if case .failure(let error) = completion {
                            print("🟥 [PurchaseRepository] createPurchase decode error: \(error)")
                        }
                    }
                )
                .replaceError(with: nil)
                .eraseToAnyPublisher()
        }
    
    
    func fetchWishedPurchases() -> AnyPublisher<[WishedPurchaseDTO], Never> {
            let url = baseURL.appendingPathComponent("purchases/wished")
            var request = URLRequest(url: url)
            request.httpMethod = "GET"
            request.setValue("*/*", forHTTPHeaderField: "accept")
            
            let userId = "35cf7863-8110-46dd-a92d-99175ea9ed38" // или из UserDefaultsManager
            request.setValue(userId, forHTTPHeaderField: "X-USER-ID")
            
            print("""
            🟦 [PurchaseRepository] fetchWishedPurchases()
                URL: \(url.absoluteString)
                X-USER-ID: \(userId)
            """)
            
            return URLSession.shared.dataTaskPublisher(for: request)
                .tryMap { output in
                    guard let http = output.response as? HTTPURLResponse else {
                        print("🟥 [PurchaseRepository] fetchWishedPurchases: not HTTP response")
                        throw URLError(.badServerResponse)
                    }
                    
                    print("🟦 [PurchaseRepository] fetchWishedPurchases statusCode: \(http.statusCode)")
                    
                    if let text = String(data: output.data, encoding: .utf8) {
                        print("📄 [PurchaseRepository] fetchWishedPurchases body:\n\(text)")
                    }
                    
                    guard (200..<300).contains(http.statusCode) else {
                        throw URLError(.badServerResponse)
                    }
                    
                    return output.data
                }
                .decode(type: WishedPurchasesResponseDTO.self, decoder: JSONDecoder())
                .map { $0.purchases }
                .handleEvents(receiveOutput: { dtos in
                    print("🟩 [PurchaseRepository] decoded \(dtos.count) wished purchases")
                })
                .replaceError(with: [])
                .eraseToAnyPublisher()
        }
    
    func updateWishedStatus(purchaseId: String,
                                status: String) -> AnyPublisher<Void, Never> {
            let url = baseURL
                .appendingPathComponent("purchases")
                .appendingPathComponent("wished")
                .appendingPathComponent(purchaseId)
                .appendingPathComponent("status")
            
            var request = URLRequest(url: url)
            request.httpMethod = "PUT"
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            request.setValue("*/*", forHTTPHeaderField: "accept")
            request.setValue(userId, forHTTPHeaderField: "X-USER-ID")
            
            let body = UpdateWishedStatusRequestDTO(status: status)
            
            do {
                let data = try JSONEncoder().encode(body)
                request.httpBody = data
                
                print("""
                🟦 [PurchaseRepository] updateWishedStatus
                    URL: \(url.absoluteString)
                    X-USER-ID: \(userId)
                    Body: \(String(data: data, encoding: .utf8) ?? "<encode error>")
                """)
            } catch {
                print("🟥 [PurchaseRepository] encode error: \(error)")
                return Just(())
                    .eraseToAnyPublisher()
            }
            
            return URLSession.shared.dataTaskPublisher(for: request)
                .tryMap { output in
                    guard let http = output.response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    
                    print("🟦 [PurchaseRepository] updateWishedStatus statusCode: \(http.statusCode)")
                    if let text = String(data: output.data, encoding: .utf8) {
                        print("📄 [PurchaseRepository] updateWishedStatus body:\n\(text)")
                    }
                    
                    guard (200..<300).contains(http.statusCode) else {
                        throw URLError(.badServerResponse)
                    }
                    
                    return ()
                }
                .replaceError(with: ())
                .eraseToAnyPublisher()
        }
    
    func parseProduct(from urlString: String) -> AnyPublisher<ParseProductResponseDTO?, Never> {
            let url = baseURL.appendingPathComponent("parser/parse")
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            request.setValue("*/*", forHTTPHeaderField: "accept")
            request.setValue(userId, forHTTPHeaderField: "X-USER-ID")
            
            do {
                let body = ParseProductRequestDTO(url: urlString)
                let data = try JSONEncoder().encode(body)
                request.httpBody = data
                
                print("""
                🟦 [PurchaseRepository] parseProduct() POST
                    URL: \(url.absoluteString)
                    X-USER-ID: \(userId)
                    Body: \(String(data: data, encoding: .utf8) ?? "<encode error>")
                """)
            } catch {
                print("🟥 [PurchaseRepository] parseProduct encode error: \(error)")
                return Just<ParseProductResponseDTO?>(nil).eraseToAnyPublisher()
            }
            
            return URLSession.shared.dataTaskPublisher(for: request)
                .tryMap { output in
                    guard let http = output.response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    
                    print("🟦 [PurchaseRepository] parseProduct statusCode: \(http.statusCode)")
                    if let text = String(data: output.data, encoding: .utf8) {
                        print("📄 [PurchaseRepository] parseProduct body:\n\(text)")
                    }
                    
                    guard (200..<300).contains(http.statusCode) else {
                        throw URLError(.badServerResponse)
                    }
                    
                    return output.data
                }
                .decode(type: ParseProductResponseDTO.self, decoder: JSONDecoder())
                .map { Optional($0) }
                .handleEvents(receiveOutput: { dto in
                    print("🟩 [PurchaseRepository] parsed product DTO: \(String(describing: dto))")
                })
                .replaceError(with: nil)
                .eraseToAnyPublisher()
        }
}
