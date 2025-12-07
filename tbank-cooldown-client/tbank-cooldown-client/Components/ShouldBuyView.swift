//
//  ShouldBuyView.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import SwiftUI

struct ShouldBuyView: View {
    var body: some View {
        HStack(spacing: 12) {
            Circle()
                .fill(Color(.systemGreen).opacity(0.15))
                .overlay {
                    Image(systemName: "checkmark.circle")
                        .resizable()
                        .scaledToFit()
                        .foregroundStyle(Color(.systemGreen))
                        .scaleEffect(0.55)
                }
                .frame(width: 40, height: 40)

            VStack(alignment: .leading, spacing: 8) {
                Text("Хороший момент для \nпокупки")
                    .font(.system(size: 20))
                    .foregroundStyle(Color(.systemGreen))

                Text("Покупка вписывается в ваш бюджет")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(                      // белый фон по радиусу
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color.white)
        )
        .overlay(                         // зелёная обводка по тому же радиусу
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .stroke(Color(.systemGreen), lineWidth: 1.5)
        )
    }
}

#Preview {
    ZStack {
        Color(.systemGray6).ignoresSafeArea()
        ShouldBuyView()
    }
}
