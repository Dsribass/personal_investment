package com.example.personal_investment.application.data.sql;

import com.example.personal_investment.domain.entities.stock.Stock;
import com.example.personal_investment.domain.entities.stock_transaction.StockTransaction;
import com.example.personal_investment.domain.entities.stock_transaction.TransactionType;
import com.example.personal_investment.domain.entities.user.User;
import com.example.personal_investment.domain.entities.wallet.Wallet;
import com.example.personal_investment.domain.usecases.stock_transaction.BrokerageNoteDAO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.personal_investment.application.main.Main.searchStockUC;
import static com.example.personal_investment.application.main.Main.searchWalletUC;


public class SqliteStockTransactionDAO implements BrokerageNoteDAO {

    private StockTransaction resultSetToEntity(ResultSet rs) throws SQLException{

        String id = rs.getString("id");

        String walletId = rs.getString("wallet");
        Wallet wallet = searchWalletUC.findById(walletId).isPresent() ? searchWalletUC.findById(walletId).get() : null;

        String stockId = rs.getString("stock");
        Stock stock = searchStockUC.findById(stockId).isPresent() ? searchStockUC.findById(stockId).get() : null;

        LocalDate transactionDate = LocalDate.parse(rs.getString("transactionDate"));
        Integer quantity = rs.getInt("quantity");
        BigDecimal unitaryValue = rs.getBigDecimal("unitaryValue");

        return new StockTransaction(id, stock, wallet, transactionDate, quantity,
                unitaryValue, TransactionType.toEnum(rs.getString("transactionType")));
    }

    @Override
    public Optional<StockTransaction> findById(String id) {
        return findOneByAttribute("id", id);
    }

    @Override
    public Optional<StockTransaction> findByStock(String stockId) {
        return findOneByAttribute("stock", stockId);
    }

    private Optional<StockTransaction> findOneByAttribute(String attribute, String value) {

        String sql = "SELECT * FROM StockTransaction WHERE " + attribute + " = ?";
        StockTransaction stockTransaction = null;

        try (PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql)){

            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                stockTransaction = resultSetToEntity(rs);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return Optional.ofNullable(stockTransaction);

    }

    @Override
    public List<StockTransaction> findTransactionsBetween(User user, LocalDate initialDate, LocalDate finalDate) {

        List<StockTransaction> transactions = new ArrayList<>();

        String sql = "SELECT w.user, s.id, s.wallet, s.stock, s.transactionDate, s.quantity, s.unitaryValue, s.transactionType " +
                "FROM Wallet w, StockTransaction s WHERE w.id = s.wallet AND w.user = ? AND s.transactionDate BETWEEN "
                       + "'"+initialDate.toString()+"'" + " AND " + "'"+finalDate.toString()+"'";

        try(PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql)){
            stmt.setInt(1,Integer.parseInt(user.getId()));
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                StockTransaction transaction = resultSetToEntity(resultSet);
                transactions.add(transaction);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return transactions;

    }

    @Override
    public String insert(StockTransaction transaction) {

        String sql = "INSERT INTO StockTransaction(id, stock, wallet, transactionDate, quantity," +
                " unitaryValue, transactionType) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql)){

            stmt.setString(1, transaction.getId());
            stmt.setString(2, String.valueOf(transaction.getStock().getId()));
            stmt.setString(3, transaction.getWallet().getId());
            stmt.setString(4, transaction.getTransactionDate().toString());
            stmt.setInt(5, transaction.getQuantity());
            stmt.setBigDecimal(6, transaction.getUnitaryValue());
            stmt.setString(7, String.valueOf(transaction.getTransactionType()));

            stmt.execute();
            return transaction.getId();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Optional<StockTransaction> findOne(String key) {
        return findOneByAttribute("id", key);
    }

    @Override
    public List<StockTransaction> findAll() {
        List<StockTransaction> stockTransactions = new ArrayList<>();
        String sql = "SELECT * FROM StockTransaction";

        try (PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StockTransaction st = resultSetToEntity(rs);
                stockTransactions.add(st);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockTransactions;
    }

    public List<StockTransaction> findAllByWallet(String walletId) {
        List<StockTransaction> stockTransactions = new ArrayList<>();
        String sql = "SELECT * FROM StockTransaction WHERE wallet = ?";

        try (PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql)) {
            stmt.setString(1, walletId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StockTransaction st = resultSetToEntity(rs);
                stockTransactions.add(st);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockTransactions;
    }

    @Override
    public void update(StockTransaction transaction) {
        String sql = "UPDATE StockTransaction SET stock = ?, wallet = ?, transactionDate = ?," +
                " quantity = ?, unitaryValue = ?, transactionType = ? WHERE id = ?";

        try(PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql)){

            stmt.setString(1, String.valueOf(transaction.getStock().getId()));
            stmt.setString(2, transaction.getWallet().getId());
            stmt.setString(3, transaction.getTransactionDate().toString());
            stmt.setInt(4, transaction.getQuantity());
            stmt.setBigDecimal(5, transaction.getUnitaryValue());
            stmt.setString(6, String.valueOf(transaction.getTransactionType()));
            stmt.setString(7, transaction.getId());
            stmt.execute();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(StockTransaction transaction) {
        if(transaction == null || transaction.getId() == null){
            throw new IllegalArgumentException("Transaction or transaction ID cannot be null.");
        }
        deleteByKey(transaction.getId());
    }

    private void deleteByKey(String id) {
        String sql = "DELETE FROM StockTransaction WHERE id = ?";

        try (PreparedStatement stmt = ConnectionFactory.createPreparedStatement(sql)) {
            stmt.setString(1, id);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
