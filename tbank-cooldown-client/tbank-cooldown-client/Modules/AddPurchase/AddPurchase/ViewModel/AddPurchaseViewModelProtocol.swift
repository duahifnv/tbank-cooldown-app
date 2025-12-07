//
//  AddPurchaseViewModelProtocol.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 05.12.2025.
//

import Foundation

// MARK: - AddPurchaseViewModelProtocol
protocol AddPurchaseViewModelProtocol: ObservableObject {
    var textLink: String { get set }
    
    func didTapParseLink()
    func didTapFillManually()
}
