//
//  ProfileRepositoryProtocol.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//

import Combine

protocol ProfileRepositoryProtocol {
    func fetchProfile() -> AnyPublisher<UserProfileDTO?, Never>
}
