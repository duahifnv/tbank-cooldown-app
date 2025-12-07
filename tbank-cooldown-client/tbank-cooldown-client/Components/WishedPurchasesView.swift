//
//  WishedPurchasesView.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import SwiftUI

struct WishedPurchasesCardView: View {
    let numberOfItems: Int
    
    init(numberOfItems: Int) {
        self.numberOfItems = numberOfItems
    }
    
    var body: some View {
        HStack(spacing: 16) {
            // Иконка слева
            ZStack {
                Circle()
                    .fill(Color.white.opacity(0.2))
                    .frame(width: 40, height: 40)
                
                Image(systemName: "heart.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 18, height: 18)
                    .foregroundStyle(.white)
            }
            
            // Текст
            VStack(alignment: .leading, spacing: 4) {
                Text("Желаемые покупки")
                    .font(.headline)
                    .foregroundStyle(.white)
                
                Text("\(numberOfItems) товаров ждут")
                    .font(.subheadline)
                    .foregroundStyle(.white.opacity(0.85))
            }
            
            Spacer()
            
            // Стрелка справа
            Image(systemName: "chevron.right")
                .font(.system(size: 16, weight: .semibold))
                .foregroundStyle(.white)
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 16)
        .background(
            LinearGradient(
                colors: [
                    Color(red: 1.0, green: 0.39, blue: 0.33), // верхний красный/оранжевый
                    Color(red: 1.0, green: 0.69, blue: 0.28)  // нижний оранжевый
                ],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.15), radius: 10, x: 0, y: 4)
    }
}

#Preview {
    ZStack {
        Color(.systemGray6).ignoresSafeArea()
        WishedPurchasesCardView(numberOfItems: 8)
            .padding()
    }
}
