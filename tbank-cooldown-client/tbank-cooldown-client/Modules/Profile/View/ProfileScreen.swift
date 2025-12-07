//
//  ProfileScreen.swift
//  tbank-cooldown-client
//
//  Created by Александр Павлицкий on 05.12.2025.
//

import SwiftUI

struct ProfileScreen: View {
    @StateObject private var viewModel = ProfileViewModel(purchaseService: PurchaseService(repository: PurchaseRepository()))
    
    var body: some View {
        ZStack {
            Color(.systemGray6)
                .ignoresSafeArea()
            
            ScrollView {
                VStack(spacing: 24) {
                    NavigationLink {
                        WishedPurchasesListView(
                            viewmodel: WishedPurchasesListViewModel(
                                purchaseService: PurchaseService(repository: PurchaseRepository())
                            )
                        )
                        .navigationTitle("Желаемые покупки")
                        .navigationBarTitleDisplayMode(.large)
                    } label: {
                        WishedPurchasesCardView(numberOfItems: $viewModel.wishedPurchases.count)
                    }
                    .buttonStyle(.plain)   // чтобы не было синей подсветки у всей карточки
                    
                    NavigationLink {
                        ReminderSettingsScreen(
                            isNotificationsEnabled: $viewModel.notificationsEnabled,
                            frequency: $viewModel.reminderFrequency,
                            excludedCategories: $viewModel.restrictedCategories
                        )
                        .navigationTitle("Настройки напоминаний")
                        .navigationBarTitleDisplayMode(.inline)
                    } label: {
                        ReminderSettingsCardView()      // та карточка с колокольчиком
                    }
                    .buttonStyle(.plain)
                    
                    AboutMeSection(
                                            title: "Обо мне",
                                            username: $viewModel.username,
                                            personalNote: $viewModel.personalNote
                                        )
                    
                    RestrictedCategoriesSection(
                        title: "Ограниченные категории",
                        categories: restrictedCategories,
                        restricted: $viewModel.restrictedCategories
                    )
                    
                    
                    CoolingSettingsCard(
                        mode: $viewModel.mode,
                        manual: $viewModel.manual,
                        smart: $viewModel.smart,
                        priceRange: 0...100_000,
                        salaryRange: 20_000...200_000
                    )
                
                }
                .padding()
            }
        }
        .navigationTitle("Профиль")
        .navigationBarTitleDisplayMode(.large)
        .onAppear {
            viewModel.didAppear()
        }
    }
}
