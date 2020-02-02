package Draft2;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import io.xpring.xrpl.WalletGenerationResult;
import io.xpring.xrpl.XpringClient;
import io.xpring.xrpl.Wallet;
import io.xpring.xrpl.XpringKitException;
import io.xpring.xrpl.TransactionStatus;

import java.util.Scanner;


public class CentralLogic {

    /**
     * Central Logic is the main app component.
     * It communicates with the different account instances on network and with central database.
     * Missing part: just need to add feature of retrieving Account instances from Database (command db.getAccoutn(...))
     *
     * To do:
     * watch tutorial on google databases
     * figure out how to connect with database
     * fill in database connection methods
     */
    private static final CentralLogic instance = new CentralLogic();
    private XpringClient xpringClient = new XpringClient();
    private static Spanner2 spanner = new Spanner2();

    //Active accounts will be used to aid functionality while database functionality (retrieve account) is completed
    private static HashSet<Account2> activeAccounts = new HashSet<>();





    /**
     * Private constructor. What should go here?
     */
    private CentralLogic() {

    }

    public static void main(String[] args) throws XpringKitException, Exception {
        spanner.initialize();
        Account2 sender = new Account2();
        Account2 receiver = new Account2();

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Demo!");
        System.out.println();
        boolean senderAccountDone = false;
        while (!senderAccountDone) {
            System.out.println("Create sender account");
            System.out.println("Please enter the following information (space separated): [FirstName LastName username]");

            String ans = sc.nextLine();

            String[] answ = ans.split(" ");
            if (answ.length != 3) {
                System.out.println("Please enter information in correct format. ");
                continue;
            } else {
                String firstName = answ[0];
                String lastName = answ[1];
                String username = answ[2];
                boolean usernameGood = false;
                while (!usernameGood) {
                    if (spanner.usernameAvailable(username)) {
                        sender.setFirstName(firstName);
                        sender.setLastName(lastName);
                        sender.setLastName(username);
                        usernameGood = true;
                    } else {
                        System.out.println("Username already taken. Please choose another one: ");
                        String nUs = sc.next();
                        username = nUs;
                        continue;
                        //assume that user will only type one word for now
                    }
                }
            }
            spanner.spannerWriter(spanner.getDbClient(), sender);
            activeAccounts.add(sender);
            System.out.println("Account for " + sender.getFirstName() + " " + sender.getLastName() + " with username " + sender.getUsername() + " has been created succesfully!");
            senderAccountDone = true;

        }

        /** Now create sender account */
        boolean receiverAccountDone = false;
        while (!receiverAccountDone) {
            System.out.println("Create receiver account");
            System.out.println("Please enter the following information (space separated): [FirstName LastName username]");

            String ans2 = sc.nextLine();

            String[] answ2 = ans2.split(" ");
            if (answ2.length != 3) {
                System.out.println("Please enter information in correct format. ");
                continue;
            } else {
                String firstName2 = answ2[0];
                String lastName2 = answ2[1];
                String username2 = answ2[2];
                boolean usernameGood2 = false;
                while (!usernameGood2) {
                    if (spanner.usernameAvailable(username2)) {
                        receiver.setFirstName(firstName2);
                        receiver.setLastName(lastName2);
                        receiver.setLastName(username2);
                        usernameGood2 = true;
                    } else {
                        System.out.println("Username already taken. Please choose another one: ");
                        String nUs = sc.next();
                        username2 = nUs;
                        continue;
                        //assume that user will only type one word for now
                    }
                }
            }
            spanner.spannerWriter(spanner.getDbClient(), receiver);
            activeAccounts.add(receiver);
            System.out.println("Account for " + receiver.getFirstName() + " " + receiver.getLastName() + " with username " + receiver.getUsername() + " has been created succesfully!");
            receiverAccountDone = true;

        }

        /** Now make a sample transfer between the two (up to 100 (initial value for demo) for now) */
        sender.sendPaymentRequest(BigInteger.valueOf(50), receiver.getUsername());
        /**
         * Console prints respective messages form account instances if sent/received succesfully or unsuccesfully
         */
        System.out.println("Payment request fulfilled. ");



    }

    public static CentralLogic getInstance() {
        return instance;
    }

    /**
     * Key method
     * @param request
     * @return
     */
    public boolean sendPayment(PaymentRequest request) throws XpringKitException, InterruptedException {

        /**Step 1: check if user has enough funds */

        Account2 sender = request.getSender();
        BigInteger amountRequest = request.getAmount();
        String walletAddressSender = sender.getWallet().getAddress();
        BigInteger balanceC = xpringClient.getBalance(walletAddressSender);
        if (amountRequest.compareTo(balanceC) < 0) {
            //send message to sender that transaction failed
            request.setTransactionStatus(TransactionStatus.FAILED);
            sender.receiveSentConfirmation(request, null, "Not enough funds for specified transaction. ");
            return false;
        }



        /** If it got to this point, user has enough funds to complete transaction: transaction verification process continues */
        /** Find destination account */

        //Original code once db is fully functional:
        // Account2 receiver = db.findAccount(request.getDestinationUsername());
        Account2 receiver = findAccount(request.getDestinationUsername());
        //if not found in DB, return null
        if (receiver == null) {
            request.setTransactionStatus(TransactionStatus.FAILED);
            sender.receiveSentConfirmation(request, null, "No account exists with that username. Please verify destination username and try again. ");
            return false;
        }
        Wallet destinationWallet = receiver.getWallet();
        String destinationAddress = destinationWallet.getAddress();
        String transactionHash = xpringClient.send(amountRequest, destinationAddress, sender.getWallet());

        /** Transaction being confirmed in blockchain... */
        TimeUnit.SECONDS.sleep(5);

        TransactionStatus transactionStatusRequest = xpringClient.getTransactionStatus(transactionHash);

        /** Now transaction has been either succesfully or unsuccesfully added to blockchain ledger. Notify users respectively */
        if (transactionStatusRequest == TransactionStatus.SUCCEEDED) {
            //add transaction to DB. Original code once DB is fully functional:
            //String paymentIdInDB = db.addTransaction(request);
            //receiver.receivePayment(request, paymentIdInDB);
            //sender.receiveSentConfirmation(request, paymentIdInDB, null);

            //Demo version:
            receiver.receivePayment(request, "Example1");
            sender.receiveSentConfirmation(request, "Example1", null);
            return true;
        } else {
            request.setTransactionStatus(TransactionStatus.FAILED);
            System.out.println("There was an error while adding the transaction to the blockchain ledger :(.");
            sender.receiveSentConfirmation(request, null, "There was an error while uploading the transaction to the blockchain ledger. Please try again. ");
        }
        return false;
    }

    /**
     * "Create account" screen will check if username entered by user is available
     * If available, the DB will immediately add it to its central repository and return uniqueId of this account added
     * @param username username to be checked if available in DB
     * @param currentUser account instance that will be added if username is available
     * @return uniqueStringID if available (assigned by DB after added to central DB)
     * @return null if username is taken
     */
    public String usernameAvailable(Account2 currentUser, String username) {

    }

    /**
     * Method that returns the balance on a specific wallet
     * @param walletAddress where method will check balance
     * @return balance of wallet as a BigInteger
     * @throws XpringKitException
     */
    public BigInteger getBalance(String walletAddress) throws XpringKitException {
        return xpringClient.getBalance(walletAddress);
    }

    /**
     * Finds account in static list of active users (for demo purposes)
     * @param username of account being searched
     * @return account with matching username or null if none
     */
    public Account2 findAccount(String username) {
        for (Account2 current: activeAccounts) {
            if (current.getUsername().equals(username)) {
                return current;
            }
        }
        return null;
    }


}
