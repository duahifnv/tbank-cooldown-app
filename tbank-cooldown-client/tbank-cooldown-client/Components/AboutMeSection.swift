//
//  AboutMeSection.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import Foundation
import SwiftUI

struct AboutMeSection: View {
    let title: String 
    
    @Binding var username: String
    @Binding var personalNote: String
    
    var body: some View {
        VStack(spacing: 0) {
            header
            Divider()
            usernameRow
            Divider()
            noteRow
        }
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
        .shadow(color: .black.opacity(0.05), radius: 6, x: 0, y: 2)
    }
}

// MARK: - Subviews

private extension AboutMeSection {
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
    
    var usernameRow: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Имя пользователя")
                .font(.footnote)
                .foregroundStyle(.secondary)
            TextField("Введите имя", text: $username)
                .font(.body)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
    }
    
    var noteRow: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("О себе")
                .font(.footnote)
                .foregroundStyle(.secondary)
            TextField("Расскажите немного о себе", text: $personalNote, axis: .vertical)
                .font(.body)
                .lineLimit(1...3)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
    }
}
