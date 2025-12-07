import SwiftUI

// MARK: - Tabs
enum AppTab: Hashable {
    case history
    case add
    case profile
}

// MARK: - MainTabView
struct MainTabView: View {
    // MARK: - Properties
    @State private var selection: AppTab = .history

    // Отдельный navigationPath для каждого таба
    @State private var historyPath = NavigationPath()
    @State private var addPath     = NavigationPath()
    @State private var profilePath = NavigationPath()
    
    // 🔸 ID для принудительного пересоздания стека "Добавить"
    @State private var addStackID = UUID()
    
    // MARK: - GUI
    var body: some View {
        ZStack {
            tabBar
            
            // Плавающая кнопка видна только когда мы НЕ на вкладке "Добавить"
            if selection != .add {
                floatingCenterButton
            }
        }
    }
}

// MARK: - Subviews
private extension MainTabView {
    
    // Основной TabView
    var tabBar: some View {
        TabView(selection: $selection) {
            // MARK: - Left tab (История)
            Tab(value: AppTab.history) {
                historyTabContent
            } label: {
                historyTabLabel
            }
            
            // MARK: - Center tab (контент под кнопкой)
            Tab(value: AppTab.add) {
                addTabContent
            } label: {
                addTabLabelHidden   // полностью пустой слот
            }
            
            // MARK: - Right tab (Профиль)
            Tab(value: AppTab.profile) {
                profileTabContent
            } label: {
                profileTabLabel
            }
        }
        .tint(.blue) // цвет системных элементов таббара
    }
    
    // MARK: - Tab contents
    
    /// Контент вкладки "История"
    var historyTabContent: some View {
        NavigationStack(path: $historyPath) {
            HistoryScreen(
                viewmodel: HistoryViewModel(
                    purchaseService: PurchaseService(repository: PurchaseRepository())
                )
            )
            .navigationTitle("История товаров")
            .navigationBarTitleDisplayMode(.large)
        }
    }
    
    /// Контент вкладки "Добавить" (под центральной кнопкой)
    var addTabContent: some View {
        NavigationStack(path: $addPath) {
            AddPurchaseView(
                selection: $selection,
                viewModel: AddPurchaseViewModel(
                    purchaseService: PurchaseService(repository: PurchaseRepository())
                ),
                onFlowFinished: {
                    // 🔹 1. очищаем свой path (на будущее, если перейдёшь на navigationDestination)
                    addPath = NavigationPath()
                    // 🔹 2. форсим пересоздание NavigationStack, чтобы выкинуть все пуши
                    addStackID = UUID()
                    // 🔹 3. переходим на вкладку История
                    selection = .history
                }
            )
        }
        .id(addStackID)   // <-- ключевая строка: новый ID = новый стек
    }
    
    /// Контент вкладки "Профиль"
    var profileTabContent: some View {
        NavigationStack(path: $profilePath) {
            ProfileScreen()
                .navigationTitle("Профиль")
                .navigationBarTitleDisplayMode(.large)
        }
    }
    
    // MARK: - Tab labels
    
    /// Лейбл левой вкладки "История"
    var historyTabLabel: some View {
        VStack(spacing: 4) {
            Image(systemName: "clock")
                .font(.system(size: 18, weight: .semibold))
                .frame(width: 32, height: 32)
                .overlay(
                    Circle()
                        .stroke(
                            selection == .history ? Color.blue : .clear,
                            lineWidth: 2
                        )
                )
                .foregroundStyle(selection == .history ? Color.blue : .gray)
            
            Text("История")
                .font(.caption2)
                .foregroundStyle(selection == .history ? Color.blue : .gray)
        }
    }
    
    /// Полностью скрытый лейбл центральной вкладки
    var addTabLabelHidden: some View {
        Color.clear
            .frame(width: 0, height: 0)
            .allowsHitTesting(false) // никакого клика по слоту
    }
    
    /// Лейбл правой вкладки "Профиль"
    var profileTabLabel: some View {
        VStack(spacing: 4) {
            Image(systemName: "person")
                .font(.system(size: 18, weight: .semibold))
                .frame(width: 32, height: 32)
                .foregroundStyle(selection == .profile ? Color.blue : .gray)
            
            Text("Профиль")
                .font(.caption2)
                .foregroundStyle(selection == .profile ? Color.blue : .gray)
        }
    }
    
    // MARK: - Floating center button
    var floatingCenterButton: some View {
        VStack {
            Spacer()
            
            Button {
                selection = .add
            } label: {
                Image(systemName: "plus")
                    .font(.system(size: 26, weight: .bold))
                    .frame(width: 64, height: 64)
                    .background(Color.blue)
                    .foregroundStyle(.white)
                    .clipShape(Circle())
                    .shadow(radius: 8, y: 4)
            }
            .offset(y: -30) // насколько выступает над таббаром
        }
        .ignoresSafeArea(edges: .bottom)
    }
}

// MARK: - Preview
#Preview {
    MainTabView()
}
