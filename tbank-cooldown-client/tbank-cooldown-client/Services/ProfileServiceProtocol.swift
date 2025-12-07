//
//  ProfileServiceProtocol.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//

import Combine

protocol ProfileServiceProtocol {
    func loadProfile() -> AnyPublisher<UserProfileDTO?, Never>
}
