//
//  ProfileRepository.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//

import Foundation
import Combine

final class ProfileRepository: ProfileRepositoryProtocol {
    private let baseURL = URL(string: "https://envelope42.ru/api")!
    
    /// откуда берём userId — подставь свой менеджер
    private var userId: String {
        ""
    }
    
    func fetchProfile() -> AnyPublisher<UserProfileDTO?, Never> {
        var request = URLRequest(url: baseURL.appendingPathComponent("user/profile"))
        request.httpMethod = "GET"
        request.setValue("*/*", forHTTPHeaderField: "accept")
        request.setValue(userId, forHTTPHeaderField: "X-USER-ID")
        
        print("""
        🟦 [ProfileRepository] fetchProfile()
            URL: \(request.url?.absoluteString ?? "nil")
            X-USER-ID: \(userId)
        """)
        
        return URLSession.shared.dataTaskPublisher(for: request)
            .tryMap { output in
                guard let http = output.response as? HTTPURLResponse else {
                    print("🟥 [ProfileRepository] not HTTP response")
                    throw URLError(.badServerResponse)
                }
                
                print("🟦 [ProfileRepository] statusCode: \(http.statusCode)")
                
                if let text = String(data: output.data, encoding: .utf8) {
                    print("📄 [ProfileRepository] body:\n\(text)")
                }
                
                guard (200..<300).contains(http.statusCode) else {
                    throw URLError(.badServerResponse)
                }
                
                return output.data
            }
            .decode(type: UserProfileDTO.self, decoder: JSONDecoder())
            .map { Optional($0) }          // UserProfileDTO -> UserProfileDTO?
            .catch { error -> Just<UserProfileDTO?> in
                print("🟥 [ProfileRepository] decode error: \(error)")
                return Just(nil)
            }
            .eraseToAnyPublisher()
    }
}
