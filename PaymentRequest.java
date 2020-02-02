package Draft2;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.xpring.xrpl.TransactionStatus;

public class PaymentRequest {

    private Account2 sender;
    private BigInteger amount;
    private String destinationUsername;
    private TransactionStatus transactionStatus;

    public PaymentRequest(Account2 sender, BigInteger amount, String destinationUsername, TransactionStatus transactionStatus) {
        this.sender = sender;
        this.amount = amount;
        this.destinationUsername = destinationUsername;
        this.transactionStatus = transactionStatus;
    }

    public Account2 getSender() {
        return sender;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public String getDestinationUsername() {
        return destinationUsername;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public void setDestinationUsername(String destinationUsername) {
        this.destinationUsername = destinationUsername;
    }

    public void setSender(Account2 sender) {
        this.sender = sender;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

}
