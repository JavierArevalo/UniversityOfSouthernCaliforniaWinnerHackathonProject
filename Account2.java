package Draft2;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import java.math.BigInteger;
import java.math.BigDecimal;

import io.xpring.xrpl.WalletGenerationResult;
import io.xpring.xrpl.XpringClient;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringKitException;
import io.xpring.xrpl.TransactionStatus;

public class Account2 extends MasterAccount {

    private String firstName;
    private String lastName;
    private String username;
    private String uniqueID;
    private BigDecimal balance;

    private HashSet<Account2> friends;
    private HashMap<String, PaymentRequest> completePaymentRequests;
    private HashMap<String, PaymentRequest> incompletePaymentRequests;

    private Wallet wallet;

    /**
     * Default construtor.
     * Will be used by "Create account" UI page and attributes of instance created will be filled as user inputs the
     * information.
     */
    public Account2() {
        balance = new BigDecimal(100);
        friends = new HashSet<>();
        completePaymentRequests = new HashMap<>();
        incompletePaymentRequests = new HashMap<>();
        try {
            wallet = createRandomWallet();
        } catch (XpringKitException e) {
            System.out.println("Error when creating random wallet. ");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Complete constructor.
     */
    public Account2(String firstName, String lastName, String username) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //Optimizations for later:
    //use uniqueId. If username available, generate new uniqueId for that username.
    //return this value and add to hash map in database

    /**
     * Method creates random wallet
     * @return wallet instance
     * @throws XpringKitException if validWallet is false
     */
    public Wallet createRandomWallet() throws XpringKitException {
        WalletGenerationResult generationResult = Wallet.generateRandomWallet();
        Wallet curr = generationResult.getWallet();
        if (validWallet(curr)) {
            return curr;
        }
        XpringKitException exception = new XpringKitException("Error in creating random wallet. ");
        throw exception;
    }

    /**
     * To do: find better way to see if addresses and keys are valid
     * Restriction: for now it will just check if it exists and if length is valid (>20 for now)
     * @param wallet to be checked if it has key attributes required
     * @return true if wallet has valid address, publicKey, and privateKey (strings of length > 20)
     * @return false otherwise
     */
    public boolean validWallet(Wallet wallet) {
        if (wallet.getAddress() == null || wallet.getAddress().length() < 20) {
            return false;
        }
        if (wallet.getPublicKey() == null || wallet.getPublicKey().length() < 20) {
            return false;
        }
        if (wallet.getPrivateKey() == null || wallet.getPrivateKey().length() < 20) {
            return false;
        }
        return true;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    /**
     *
     * @param submittedEarlier the paymentRequest submitted earlier once it has been processed by central logic
     * @param paymentIdInDB unique name given to PaymentTransaction in central database
     * If payment was succesful: add to completePaymentRequests map
     */
    public void receiveSentConfirmation(PaymentRequest submittedEarlier, String paymentIdInDB, String message) {
        if (submittedEarlier.getTransactionStatus() == TransactionStatus.FAILED) {
            this.incompletePaymentRequests.put(paymentIdInDB, submittedEarlier);
            System.out.println("Payment to user: " + submittedEarlier.getDestinationUsername() + " of amount " + submittedEarlier.getAmount() + " was unsuccesful. Please verify information and try again. ");
            System.out.println(message);
        } else if (submittedEarlier.getTransactionStatus() == TransactionStatus.SUCCEEDED) {
            System.out.println("Payment to user: " + submittedEarlier.getDestinationUsername() + " of amount " + submittedEarlier.getAmount() + " was succesful.");
            this.completePaymentRequests.put(paymentIdInDB, submittedEarlier);
            System.out.println("Payment has id number " + paymentIdInDB + " and has been added to completed Payments history. ");
        } else {
            System.out.println("Payment " + paymentIdInDB + "'s status is unknown. ");
        }
    }

    /**
     * Message the account receives when someone sends him a payment
     * Will only receive a notification if payment was succesful
     * @param received
     * @param paymentIdInDB
     */
    public void receivePayment(PaymentRequest received, String paymentIdInDB) throws XpringKitException {
        if (received.getTransactionStatus() != TransactionStatus.SUCCEEDED) {
            return;
        }
        System.out.println("Received a payment from: " + received.getSender().getUsername() + " of " + received.getAmount() + ".");
        //Assume balance is automatically updated in wallet ledger
        System.out.println("Now your balance is: " + this.getBalance());
    }

    /**
     *
     * @param amount XRP amount to be sent to destinationUsername
     * @param destinationUsername username of receiver of payment
     * @return true if payment was received succesfully by central logic
     * @return false if there was an error sending Payment request
     */
    public boolean sendPaymentRequest(BigInteger amount, String destinationUsername) {
        PaymentRequest request = new PaymentRequest(this, amount, destinationUsername, TransactionStatus.PENDING);
        try {
            boolean curr = centralLogic.sendPayment(request);
            return curr;
        } catch (Exception e) {
            System.out.println("Error sending payment request to server (central logic).");
            System.out.println(e.getMessage());
            return false;
        }
    }

    public BigInteger getBalance() throws XpringKitException {
        String walletAddressSender = this.getWallet().getAddress();
        BigInteger balanceC = centralLogic.getBalance(walletAddressSender);
    }

}
