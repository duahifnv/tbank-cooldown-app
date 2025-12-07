//
//  RestrictedCategoriesSection.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
import SwiftUI

struct RestrictedCategoriesSection: View {
    // Заголовок секции (например: "ОГРАНИЧЕННЫЕ КАТЕГОРИИ")
    let title: String
    
    // Список всех категорий, которые показываем
    let categories: [PurchaseCategory]
    
    // Набор выбранных (ограниченных) категорий
    @Binding var restricted: Set<PurchaseCategory>
    
    var body: some View {
        VStack(spacing: 0) {
            header
            
            ForEach(categories, id: \.self) { category in
                row(for: category)
                
                if category != categories.last {
                    Divider()
                }
            }
        }
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
        .shadow(color: .black.opacity(0.05), radius: 6, x: 0, y: 2)
    }
}

// MARK: - Subviews

private extension RestrictedCategoriesSection {
    var header: some View {
        HStack {
            Text(title.uppercased())
                .font(.caption)
                .fontWeight(.semibold)
                .foregroundStyle(.secondary)
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
    }
    
    func row(for category: PurchaseCategory) -> some View {
        HStack {
            Text(category.rawValue)
                .font(.body)
            
            Spacer()
            
            Toggle("", isOn: binding(for: category))
                .labelsHidden()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
    }
    
    // биндинг для конкретной категории
    func binding(for category: PurchaseCategory) -> Binding<Bool> {
        Binding(
            get: { restricted.contains(category) },
            set: { isOn in
                if isOn {
                    restricted.insert(category)
                } else {
                    restricted.remove(category)
                }
            }
        )
    }
}
