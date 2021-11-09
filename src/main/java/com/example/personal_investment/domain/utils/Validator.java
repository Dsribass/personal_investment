package com.example.personal_investment.domain.utils;

import com.example.personal_investment.domain.entities.stock.Stock;
import com.example.personal_investment.domain.entities.stock_transaction.StockTransaction;
import com.example.personal_investment.domain.entities.user.User;
import com.example.personal_investment.domain.entities.wallet.Wallet;
import com.example.personal_investment.domain.exceptions.TypeNotMatchException;

public class Validator {
    public static void validateStock(Stock stock) {
        if(stock.getId() == null){
            throw new IllegalArgumentException("Stock id cannot be null");
        }
        if(stock.getType() == null){
            throw new IllegalArgumentException("Stock type cannot be null");
        }
        if(stock.getTicker() == null){
            throw new IllegalArgumentException("Stock Ticker cannot be null ");
        }
        if(stock.getCnpj() == null){
            throw new IllegalArgumentException("Stock CNPJ cannot be null ");
        }
        if(stock.getCompanyName() == null){
            throw new IllegalArgumentException("Stock Company name cannot be null ");
        }
    }
    public static void validateWallet(Wallet wallet){
        if(wallet.getType() == null){
            throw new IllegalArgumentException("Wallet type cannot be null");
        }

        if(wallet.getName() == null){
            throw new IllegalArgumentException("Wallet name cannot be null");
        }
    }

    public static void validateTransaction(StockTransaction transaction) {
        if (transaction.getStock() == null) {
            throw new IllegalArgumentException("Stock cannot be null");
        }
        if (transaction.getWallet() == null) {
            throw new IllegalArgumentException("Wallet cannot be null");
        }
        if (transaction.getTransactionDate() == null) {
            throw new IllegalArgumentException("Transaction Date cannot be null");
        }
        if (transaction.getUnitaryValue() == null) {
            throw new IllegalArgumentException("Stock unitary value cannot be null");
        }
        if (transaction.getTransactionType() == null) {
            throw new IllegalArgumentException("Transaction Type cannot be null");
        }

        Validator.validateStock(transaction.getStock());
        Validator.validateWallet(transaction.getWallet());

        if (transaction.getStock().getType() != transaction.getWallet().getType()) {
            throw new TypeNotMatchException("The stock type does not match with wallet type");
        }
    }

    public static void validateUser(User user){
        if(user == null){
            throw new IllegalArgumentException("User cannot be null.");
        }
        if(user.getUsername() == null){
            throw new IllegalArgumentException("User name cannot be null.");
        }
        // TODO: se usuario tiver atributo senha, verificar se null
        /* como vcs preferem fazer a validação do usuario e senha?
            colocamos senha como atributo de usuário? ou apenas processamos
            essa informação no banco?
         */
    }
}
