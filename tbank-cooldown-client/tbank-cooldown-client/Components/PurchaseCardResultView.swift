//
//  PurchaseCardResultView.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import SwiftUI

// MARK: - PurchaseCardResultView
struct PurchaseCardResultView: View {
    // MARK: - Properties
    private let name: String
    private let category: String
    private let coolingMode: CoolingMode
    private let price: String
    
    private var coolingColor: Color {
        switch coolingMode {
        case .smart:
            return Color(.systemBlue)
        case .manual:
            return Color(.systemOrange)
        }
    }
    
    // MARK: - Init
    init(name: String, category: String, coolingMode: CoolingMode, price: String) {
        self.name = name
        self.category = category
        self.coolingMode = coolingMode
        self.price = price
    }
    
    // MARK: - GUI
    var body: some View {
        ZStack {
            HStack {
                VStack(alignment: .leading) {
                    Text(name)
                        .font(.title3)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    
                    Spacer(minLength: 0)
                    
                    Text(category)
                        .font(.footnote)
                    
                    Spacer(minLength: 0)
                    
                    Text(price + " ₽")
                        .font(.title)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                
                VStack {
                    Text(coolingMode.rawValue)
                        .font(.caption)
                        .foregroundStyle(coolingColor)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 2)
                        .background(coolingColor.opacity(0.15))
                        .clipShape(RoundedRectangle(cornerRadius: 5))
                    
                    Spacer(minLength: 0)
                
                }
            }
            .padding()
        }
        .frame(maxWidth: .infinity)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

#Preview {
    PurchaseCardResultView(name: "Наушники Sony WH-1000XM5", category: "Электроника", coolingMode: .manual, price: "24 900")
        .frame(height: 145)
        .applyShadow()
}
