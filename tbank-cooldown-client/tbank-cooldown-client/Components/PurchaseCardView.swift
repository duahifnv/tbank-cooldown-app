//
//  PurchaseCardView.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import SwiftUI

// MARK: - PurchaseCardView
struct PurchaseCardView: View {
    // MARK: - Properties
    private let name: String
    private let date: String
    private let category: String
    private let status: PurchaseStatus
    private let price: String
    private let leftDays: Int?
    private let isWishedPurchase: Bool
    
    var statusColor: Color {
        switch status {
        case .cancalled:
            Color(.systemRed)
        case .purchased:
            Color(.systemGreen)
        default:
            Color(.gray)
        }
    }
    
    var daysLeftColor: Color {
        if let leftDays = leftDays {
            if leftDays <= 1 {
                return Color(.systemRed)
            } else if leftDays <= 3 {
                return Color(.systemOrange)
            } else if leftDays <= 7 || leftDays >= 7 {
                return Color(.systemBlue)
            } else {
                return Color.secondary
            }
        } else {
            return Color.secondary
        }
    }
    
    // MARK: - Init
    init(name: String, date: String, category: String, status: PurchaseStatus, price: String, leftDays: Int? = nil, isWishedPurchase: Bool) {
        self.name = name
        self.date = date
        self.category = category
        self.status = status
        self.price = price
        self.leftDays = leftDays
        self.isWishedPurchase = isWishedPurchase
    }
    
    // MARK: - GUI
    var body: some View {
        VStack {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(name)
                        .font(.body)
                        .lineLimit(2)                             // максимум 2 строки
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)
                        .layoutPriority(1)                        // ⬅️ главное: даём приоритет имени

                    VStack(alignment: .leading, spacing: 8) {
                        Text(date)
                            .foregroundStyle(.secondary)
                            .font(.footnote)
                        Text(category)
                            .foregroundStyle(.secondary)
                            .font(.footnote)
                    }
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 0) {
                    Group {
                        if leftDays == nil, isWishedPurchase {
                            Text("Готово")
                                .foregroundStyle(.white)
                                .font(.footnote)
                                .padding(.vertical, 4)
                                .padding(.horizontal, 16)
                                .background(
                                    LinearGradient(
                                        colors: [
                                            Color(red: 1.0, green: 0.39, blue: 0.33),
                                            Color(red: 1.0, green: 0.69, blue: 0.28)
                                        ],
                                        startPoint: .topLeading,
                                        endPoint: .bottomTrailing
                                    )
                                )
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                        } else if let leftDays = leftDays, isWishedPurchase {
                            HStack(spacing: 2) {
                                Image(systemName: "clock")
                                Text(daysLeftPhrase(for: leftDays))
                            }
                            .font(.footnote)
                            .foregroundStyle(daysLeftColor)
                        } else {
                            Text(status.rawValue)
                                .foregroundStyle(.white)
                                .font(.footnote)
                                .padding(.vertical, 4)
                                .padding(.horizontal, 16)
                                .background(statusColor)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                    }
                    .frame(height: 24, alignment: .top)  // оставь только высоту

                    Spacer(minLength: 0)

                    Text("₽" + price)
                        .font(.title3)

                    Spacer(minLength: 0)
                }
            }
            .padding()
        }
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: Color.black.opacity(0.2), radius: 3, x: 0, y: 1)
    }
    
    func daysLeftPhrase(for days: Int) -> String {
        // падеж для "день"
        let form: String
        let mod100 = days % 100
        let mod10  = days % 10
        
        if mod100 >= 11 && mod100 <= 14 {
            form = "дней"
        } else {
            switch mod10 {
            case 1:
                form = "день"
            case 2...4:
                form = "дня"
            default:
                form = "дней"
            }
        }
        
        // глагол "остался / осталось"
        let verb: String
        if mod100 >= 11 && mod100 <= 14 {
            verb = "осталось"
        } else {
            verb = (mod10 == 1) ? "остался" : "осталось"
        }
        
        return "\(days) \(form) \(verb)"
    }
}
