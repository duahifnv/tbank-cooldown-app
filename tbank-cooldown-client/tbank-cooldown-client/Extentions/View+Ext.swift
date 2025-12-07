//
//  View+Ext.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import SwiftUI

extension View {
    func applyShadow() -> some View {
        self.shadow(color: Color.black.opacity(0.05), radius: 2, x: 0, y: 3)
    }
}
