package com.example.personal_investment.application.controllers;

import com.example.personal_investment.application.common.Routes;
import com.example.personal_investment.application.common.UserSingleton;
import com.example.personal_investment.application.view.Window;
import com.example.personal_investment.application.viewmodel.InvestmentVM;
import com.example.personal_investment.domain.entities.investment.Investment;
import com.example.personal_investment.domain.entities.stock.Stock;
import com.example.personal_investment.domain.entities.stock_transaction.TransactionType;
import com.example.personal_investment.domain.entities.user.User;
import com.example.personal_investment.domain.entities.wallet.Wallet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.personal_investment.application.main.Main.searchInvestmentUC;
import static com.example.personal_investment.application.main.Main.searchStockUC;

public class InvestmentsManagementController {

    @FXML
    private Label systemMessage;

    @FXML
    private Label username;

    @FXML
    private TableView<InvestmentVM> tbInvestments;

    @FXML
    private TableColumn<InvestmentVM, String> cTicker;

    @FXML
    private TableColumn<InvestmentVM, String> cAverageValue;

    @FXML
    private TableColumn<InvestmentVM, String> cStockType;

    @FXML
    private TableColumn<InvestmentVM, String> cAmount;

    private ObservableList<InvestmentVM> snapshot;
    private Wallet wallet;

    @FXML
    private void initialize() throws IOException {
        User user = UserSingleton.getInstance().getUser();
        if(user == null) {
            Window.setRoot(Routes.loginPage);
        }else{
        username.setText(user.getUsername());

        }
        bindTableViewToItemsList();
        bindColumnsToValueSources();
    }

    public void setDataWallet(Wallet wallet) throws IOException {
        if (wallet == null) {
            Window.setRoot(Routes.loginPage);
        }
        this.wallet = wallet;
        loadList();
    }

    private void loadList() {
        List<InvestmentVM> investments = searchInvestmentUC.findAllInvestmentsByWallet(wallet)
                .stream()
                .map(InvestmentVM::new)
                .collect(Collectors.toList());
        snapshot.clear();
        snapshot.addAll(investments);
    }

    private void bindColumnsToValueSources() {
        cTicker.setCellValueFactory(new PropertyValueFactory<>("ticker"));
        cAverageValue.setCellValueFactory(new PropertyValueFactory<>("averageValue"));
        cStockType.setCellValueFactory(new PropertyValueFactory<>("stockType"));
        cAmount.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void bindTableViewToItemsList() {
        snapshot = FXCollections.observableArrayList();
        tbInvestments.setItems(snapshot);
    }

    public void buyInvestment(ActionEvent actionEvent) throws IOException {
        getInvestmentAndGoToTransactionPage(TransactionType.PURCHASE);
    }

    public void sellInvestment(ActionEvent actionEvent) throws IOException {
        getInvestmentAndGoToTransactionPage(TransactionType.SALE);
    }

    private void getInvestmentAndGoToTransactionPage(TransactionType sale) throws IOException {
        if (isInvestmentSelect()) {
            InvestmentVM investmentVM = tbInvestments.getSelectionModel().getSelectedItem();

            Optional<Stock> stock = searchStockUC.findByTicker(investmentVM.getTicker());
            if (stock.isPresent()) {
                Window.setRoot(Routes.stockTransactionPage);
                Investment investment = investmentVM.toInvestmentEntity(wallet, stock.get());

                setDataInInvestmentPage(investment, sale);
            } else {
                systemMessage.setText("Algum erro aconteceu");
            }
        }
    }

    public void backPreviousScreen(ActionEvent actionEvent) throws IOException {
        Window.setRoot(Routes.walletManagementPage);
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        Window.setRoot(Routes.loginPage);
    }

    private Boolean isInvestmentSelect() {
        return !tbInvestments.getSelectionModel().isEmpty();
    }

    private void setDataInInvestmentPage(Investment investment, TransactionType type) throws IOException {
        StockTransactionController controller = (StockTransactionController) Window.getController();
        controller.setData(investment.getWallet(), investment.getStock(), type);
    }

}
