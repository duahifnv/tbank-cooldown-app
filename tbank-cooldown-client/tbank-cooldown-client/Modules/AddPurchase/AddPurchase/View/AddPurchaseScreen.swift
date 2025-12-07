import SwiftUI

struct AddPurchaseView: View {
    // MARK: - Properties
    @Binding var selection: AppTab
    @StateObject private var viewModel: AddPurchaseViewModel
    let onFlowFinished: () -> Void
    @State private var isParsedFlowActive: Bool = false 

    // MARK: - Init
    init(
        selection: Binding<AppTab>,
        viewModel: AddPurchaseViewModel = AddPurchaseViewModel(
            purchaseService: PurchaseService(repository: PurchaseRepository())
        ),
        onFlowFinished: @escaping () -> Void
    ) {
        _selection = selection
        _viewModel = StateObject(wrappedValue: viewModel)
        self.onFlowFinished = onFlowFinished
    }

    // MARK: - GUI
    var body: some View {
        ZStack {
            dimBackground
            mainLayout

            // Скрытый линк на экран с уже заполненными полями
            NavigationLink(
                destination: parsedDestination,
                isActive: $isParsedFlowActive,
                label: { EmptyView() }
            )
            .hidden()
        }
        .alert(
            "Ошибка",
            isPresented: $viewModel.isTextLinkInvalid
        ) {
            Button("Ок", role: .cancel) { }
        } message: {
            Text("Не удалось получить данные о товаре по указанной ссылке. Пожалуйста, проверьте корректность ссылки и повторите попытку или заполните данные вручную.")
        }
        // как только в VM появился parsedProduct — открываем следующий экран
        .onReceive(viewModel.$parsedProduct) { product in
            isParsedFlowActive = (product != nil)
        }
    }
}

// MARK: - Subviews
private extension AddPurchaseView {

    // Тёмный фон вокруг модалки
    var dimBackground: some View {
        Color.black.opacity(0.6)
            .ignoresSafeArea()
    }

    var mainLayout: some View {
        VStack {
            Spacer(minLength: 24)
            mainCard
            Spacer(minLength: 24)
        }
    }

    // Белая "карточка" экрана
    var mainCard: some View {
        VStack(spacing: 0) {
            header
            contentScroll
        }
        .background(
            RoundedRectangle(cornerRadius: 32, style: .continuous)
                .fill(Color(.systemGray6))   // как на скрине – чуть серый фон
        )
        .padding(.horizontal, 24)
    }

    // MARK: - Header
    var header: some View {
        HStack {
            Button {
                // Закрыть модалку: перейти на вкладку История
                selection = .history
            } label: {
                Image(systemName: "xmark")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(.blue)
            }

            Spacer()

            Text("Добавить покупку")
                .font(.headline)
                .foregroundColor(.primary)

            Spacer()

            // Пустая иконка чтобы заголовок был по центру
            Image(systemName: "xmark")
                .font(.system(size: 18, weight: .semibold))
                .opacity(0)
        }
        .padding(.horizontal, 24)
        .padding(.top, 16)
    }

    var contentScroll: some View {
        ScrollView {
            VStack(spacing: 24) {
                productLinkCard
                orSeparator
                fillManuallyCard
                descriptionText
            }
            .padding(.horizontal, 24)
            .padding(.top, 24)
            .padding(.bottom, 24)
        }
    }

    // MARK: - Product Link card
    var productLinkCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Ссылка на товар")
                .font(.footnote)
                .foregroundColor(.secondary)

            HStack(spacing: 8) {
                Image(systemName: "link")
                    .foregroundColor(.secondary)

                TextField(
                    "httрs://example.com/product",
                    text: $viewModel.textLink   // ← биндим к VM
                )
                .font(.subheadline)
                .foregroundColor(.secondary)   // цвет текста
                .tint(.blue)                   // цвет курсора
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(Color(.systemGray6))
            )

            Divider()

            Button {
                viewModel.didTapParseLink()
            } label: {
                Text("Заполнить автоматически")
                    .font(.system(size: 16))
                    .foregroundColor(.blue)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 4)
            }
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(Color.white)
        )
    }

    // MARK: - OR separator
    var orSeparator: some View {
        HStack {
            Rectangle()
                .frame(height: 1)
                .foregroundColor(Color(.systemGray4))

            Text("или")
                .font(.caption)
                .foregroundColor(.secondary)

            Rectangle()
                .frame(height: 1)
                .foregroundColor(Color(.systemGray4))
        }
    }

    // MARK: - Fill Manually card
    var fillManuallyCard: some View {
        NavigationLink {
            AddingPurchaseScreen(
                viewModel: AddingPurchaseViewModel(
                    purchase: nil,
                    purchaseService: PurchaseService(repository: PurchaseRepository())
                ),
                onFlowFinished: onFlowFinished
            )
            .navigationTitle("Добавление покупки")
        } label: {
            HStack(spacing: 8) {
                Spacer()
                Image(systemName: "pencil.line")
                Text("Заполнить вручную")
                    .font(.system(size: 16))
                Spacer()
            }
            .foregroundColor(.blue)
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(Color.white)
            )
        }
        .buttonStyle(.plain)
    }

    // MARK: - Description text
    var descriptionText: some View {
        Text("Вставьте ссылку на товар, чтобы заполнить данные автоматически, или укажите их вручную.")
            .font(.footnote)
            .foregroundColor(.secondary)
            .multilineTextAlignment(.center)
            .padding(.top, 8)
    }
}


// MARK: - Parsed destination
private extension AddPurchaseView {
    @ViewBuilder
    var parsedDestination: some View {
        if let parsed = viewModel.parsedProduct {
            // собираем доменную модель Purchase из распарсенных данных
            let purchase = Purchase(
                name: parsed.name,
                price: parsed.price,
                category: parsed.category ?? .other,
                date: "",          // при желании поставь текущую дату
                status: .all       // здесь статус не критичен, нужен только для VM
            )
            
            AddingPurchaseScreen(
                viewModel: AddingPurchaseViewModel(
                    purchase: purchase,
                    purchaseService: PurchaseService(repository: PurchaseRepository())
                ),
                onFlowFinished: onFlowFinished
            )
            .navigationTitle("Покупка по ссылке")
        } else {
            // на всякий случай, если parsedProduct всё-таки nil
            Text("Не удалось загрузить данные товара")
        }
    }
}
