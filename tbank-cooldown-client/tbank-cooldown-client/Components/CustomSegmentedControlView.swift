//
//  CustomSegmentedControlView.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import SwiftUI

struct CustomSegmentedControlView<Item: Hashable>: View {
    // MARK: - Properties
    let items: [Item]
    @Binding var selection: Item
    let titleProvider: (Item) -> String
    let equalWidth: Bool      // если true — равномерно по ширине
    
    // MARK: - Init
    init(
        items: [Item],
        selection: Binding<Item>,
        equalWidth: Bool = false,
        titleProvider: @escaping (Item) -> String
    ) {
        self.items = items
        self._selection = selection
        self.equalWidth = equalWidth
        self.titleProvider = titleProvider
    }

    // MARK: - GUI
    var body: some View {
        HStack(spacing: 8) {
            ForEach(items, id: \.self) { item in
                let isSelected = (item == selection)
                
                if equalWidth {
                    equalWidthButton(item: item, isSelected: isSelected)
                } else {
                    naturalWidthButton(item: item, isSelected: isSelected)
                }
            }
        }
        .padding(4)
        .frame(maxWidth: equalWidth ? .infinity : nil,
               alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 10, style: .continuous)
                .fill(Color(.systemGray5))
        )
    }
}

// MARK: - Private
extension CustomSegmentedControlView {
    
    /// Вариант, когда сегменты растягиваются равномерно
    func equalWidthButton(item: Item, isSelected: Bool) -> some View {
        Button {
            withAnimation(.easeInOut(duration: 0.15)) {
                selection = item
            }
        } label: {
            ZStack {
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(isSelected ? Color.white : .clear)

                Text(titleProvider(item))
                    .font(.system(size: 16))
                    .foregroundColor(isSelected ? .black : .gray)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
            }
            .frame(maxWidth: .infinity)
            .frame(height: 36)
        }
        .buttonStyle(.plain)
    }
    
    /// Старое поведение "как было": ширина по контенту
    func naturalWidthButton(item: Item, isSelected: Bool) -> some View {
        Button {
            withAnimation(.easeInOut(duration: 0.15)) {
                selection = item
            }
        } label: {
            Text(titleProvider(item))
                .font(.system(size: 16))
                .foregroundColor(isSelected ? .black : .gray)
                .padding(.vertical, 8)
                .padding(.horizontal, 16)
                .background(
                    RoundedRectangle(cornerRadius: 10, style: .continuous)
                        .fill(isSelected ? Color.white : .clear)
                )
        }
        .buttonStyle(.plain)
    }
}
