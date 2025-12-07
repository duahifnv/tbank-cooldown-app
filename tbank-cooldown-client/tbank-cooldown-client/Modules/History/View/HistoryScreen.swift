//
//  HistoryScreen.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 05.12.2025.
//

import SwiftUI

// MARK: - HistoryScreen
struct HistoryScreen: View {
    // MARK: - Properties
    @ObservedObject private var viewmodel: HistoryViewModel
    
    // MARK: - Init
    init(viewmodel: HistoryViewModel) {
        self.viewmodel = viewmodel
    }
    
    // MARK: - GUI
    var body: some View {
        ZStack {
            Color(.systemGray6)
            
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 8,) {
                    Text("Статус")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                    CustomSegmentedControlView(items: viewmodel.statuses, selection: $viewmodel.selectedStatus, equalWidth: true, titleProvider: { status in
                        status.rawValue})
                }
                VStack(alignment: .leading, spacing: 8,) {
                    Text("Категория")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                    ScrollView(.horizontal) {
                        CustomSegmentedControlView(items: viewmodel.categories, selection: $viewmodel.selectedCategory, titleProvider: { category in
                            category.rawValue})
                    }
                    .scrollIndicators(.hidden)
                }
                VStack(alignment: .leading, spacing: 8,) {
                    Text("Сортировать по")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                    CustomSegmentedControlView(items: viewmodel.sorts, selection: $viewmodel.selectedSort, equalWidth: true, titleProvider: {sort in
                        sort.rawValue})
                }
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(viewmodel.filteredPurchases, id: \.id   ) { purchase in
                            PurchaseCardView(
                                name: purchase.name,
                                date: purchase.date,
                                category: purchase.category.rawValue,
                                status: purchase.status,
                                price: "\(purchase.price)", isWishedPurchase: false
                            )
                            .frame(height: 110)
                            .applyShadow()
                        }
                    }
                    .padding(.vertical, 8)
                }
                Spacer()
            }
            .padding()
        }
        .onAppear(perform: {
            self.viewmodel.didAppear()
        })
    }
    
}

