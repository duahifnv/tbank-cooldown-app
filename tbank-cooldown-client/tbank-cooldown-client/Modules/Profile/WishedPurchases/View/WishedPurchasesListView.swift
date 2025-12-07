//
//  WishedPurchasesListView.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 06.12.2025.
//

import SwiftUI

// MARK: - WishedPurchasesListView
struct WishedPurchasesListView: View {
    // MARK: - Properties
    @ObservedObject private var viewModel: WishedPurchasesListViewModel

    // MARK: - Init
    init(viewmodel: WishedPurchasesListViewModel) {
        self.viewModel = viewmodel
    }

    // MARK: - GUI
    var body: some View {
        ZStack {
            Color(.systemGray6)
                .ignoresSafeArea()

            VStack(alignment: .leading, spacing: 20) {
                categorySection
                sortSection
                listSection
                Spacer(minLength: 0)
            }
            .padding()
        }
        .onAppear {
            viewModel.didAppear()
        }
    }
}

// MARK: - Subviews
private extension WishedPurchasesListView {
    // Категория
    var categorySection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Категория")
                .font(.footnote)
                .foregroundStyle(.secondary)

            ScrollView(.horizontal, showsIndicators: false) {
                CustomSegmentedControlView(
                    items: viewModel.categories,
                    selection: $viewModel.selectedCategory,
                    titleProvider: { category in
                        category.rawValue
                    }
                )
            }
        }
    }

    // Сортировка
    var sortSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Сортировать по")
                .font(.footnote)
                .foregroundStyle(.secondary)

            CustomSegmentedControlView(
                items: viewModel.sorts,
                selection: $viewModel.selectedSort,
                equalWidth: true,
                titleProvider: { sort in
                    sort.rawValue
                }
            )
        }
    }

    // Список
    var listSection: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                let purchases = viewModel.filteredPurchases

                ForEach(purchases, id: \.id) { purchase in
                    Row(
                        purchase: purchase,
                        onBuy: { viewModel.didTapBuy(purchase) },
                        onCancel: { viewModel.didTapCancel(purchase) }
                    )
                }
            }
            .padding(.vertical, 8)
        }
        .scrollIndicators(.hidden)
    }
}

// MARK: - Row (карточка + кнопки)
private extension WishedPurchasesListView {
    struct Row: View {
        let purchase: WishedPurchase
        let onBuy: () -> Void
        let onCancel: () -> Void
        
        @State private var didSendNotification = false
        
        /// Осталось дней (если nil — время вышло)
        private var daysLeft: Int? {
            purchase.coolingTimeout
        }

        /// Время охлаждения вышло → показываем кнопки
        private var isCoolingFinished: Bool {
            purchase.coolingTimeout == nil
        }

        var body: some View {
            VStack(spacing: 8) {
                PurchaseCardView(
                    name: purchase.name,
                    date: purchase.wishedDate,
                    category: purchase.category.rawValue,
                    status: .all,
                    price: "\(Int(purchase.price))",
                    leftDays: daysLeft,
                    isWishedPurchase: true
                )
                .frame(height: 110)
                .applyShadow()

                if isCoolingFinished {
                    HStack(spacing: 12) {
                        Button(action: onBuy) {
                            Text("Купить")
                                .font(.subheadline.bold())
                                .foregroundStyle(.white)
                                .padding(.vertical, 8)
                                .frame(maxWidth: .infinity)
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
                                .clipShape(RoundedRectangle(cornerRadius: 10))
                        }

                        Button(action: onCancel) {
                            Text("Отменить")
                                .font(.subheadline.bold())
                                .foregroundStyle(.white)
                                .padding(.vertical, 8)
                                .frame(maxWidth: .infinity)
                                .background(Color(.systemGray4))
                                .clipShape(RoundedRectangle(cornerRadius: 10))
                        }
                    }
                }
            }
            .onAppear {
                // как только карточка с законченной заморозкой появилась,
                // один раз шлём локальное уведомление
                if isCoolingFinished, !didSendNotification {
                    NotificationManager.shared.notifyCoolingFinished(for: purchase)
                    didSendNotification = true
                }
            }
        }
    }
}
