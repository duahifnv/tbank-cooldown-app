//
//  ReminderSettingsCardView.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 07.12.2025.
//
    

import SwiftUI

struct ReminderSettingsCardView: View {
    var body: some View {
        HStack(spacing: 16) {
            // Кружок с иконкой
            ZStack {
                Circle()
                    .fill(
                        LinearGradient(
                            colors: [
                                Color(red: 1.0, green: 0.40, blue: 0.33),
                                Color(red: 1.0, green: 0.70, blue: 0.30)
                            ],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                Image(systemName: "bell")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundStyle(.white)
            }
            .frame(width: 40, height: 40)

            // Текст
            VStack(alignment: .leading, spacing: 4) {
                Text("Настройки напоминаний")
                    .font(.headline)
                    .foregroundStyle(.primary)

                Text("Push-уведомления")
                    .font(.footnote)
                    .foregroundStyle(.secondary)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(.secondary)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
        .shadow(color: .black.opacity(0.06), radius: 6, x: 0, y: 3)
    }
}
