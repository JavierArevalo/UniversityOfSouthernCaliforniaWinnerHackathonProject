package io.xpring.xrpl;

import io.xpring.xrpl.javascript.JavaScriptWallet;
import io.xpring.xrpl.javascript.JavaScriptWalletFactory;
import io.xpring.xrpl.javascript.JavaScriptWalletGenerationResult;

/**
 * This class represents an account on the XRP Ledger
 * It also provides the necessary signing and verifying cryptographic function
 */
public class Wallet {

    /**
     * Underlying Java Script wallet
     */
    private JavaScriptWallet javaScriptWallet;

    /**
     * Initialize a new wallet from a seed
     *
     * @param seed: base58check encoded seed for the wallet
     * @throws XpringKitException if the seed is malformed
     */
    public Wallet(String seed) throws XspringKitException {
        this(seed, false);
    }

    /**
     * Initialize a new wallet from a seed
     *
     * @param seed: base58check encoded seed for the wallet
     * @param isTest: whether the address is for use on a test network
     * @throws XpringKitException if the seed is malformed
     */
    public Wallet(String seed, boolean isTest) throws XspringKitException {
        this.javaScriptWallet = JavaScriptWalletFactory.get().walletFromSeed(seed, isTest);
    }

    /**
     * Create a new HD wallet
     *
     * @param mnemonic: a space separated mnemonic.
     * @param derivationPath: a derivation. If null, the default derivation path will be used
     * @throws: XspringKitException if the mnemonic or derivation path are malformed
     */
    public Wallet(String mnemonic, String derivationPath) throws XspringKitException {
        this(mnemonic, derivationPath, false);
    }

    /**
     * Create a new HD wallet
     *
     * @param mnemonic: a space separated mnemonic.
     * @param derivationPath: a derivation. If null, the default derivation path will be used
     * @param isTest: whether the address is for use on a test network
     * @throws: XspringKitException if the mnemonic or derivation path are malformed
     */
    public Wallet(String mnemonic, String derivationPath, boolean isTest) throws XpringKitException {
        this.javaScriptWallet = JavaScriptWalletFactory.get().walletFromMnemonicAndDerivationPath(mnemonic,
                derivationPath, isTest);
    }

    /**
     * Generate a random Wallet
     *
     * @return a {WalletGenerationResult} containing the artifacts of the generation process
     * @throws XpringKitException if the wallet generation fails
     */
    public static WalletGenerationResult generateRandomWallet() throws XpringKitException {
        return generateRandomWallet(false);
    }

    /**
     * Generate a random Wallet.
     *
     * @param isTest Whether the address is for use on a test network.
     * @return A {WalletGenerationResult} containing the artifacts of the generation process.
     * @throws XpringKitException If wallet generation fails.
     */
    public static WalletGenerationResult generateRandomWallet(boolean isTest) throws XpringKitException {
        JavaScriptWalletGenerationResult javaScriptWalletGenerationResult = JavaScriptWalletFactory.get()
                .generateRandomWallet(isTest);

        // TODO(keefertaylor): This should be a direct conversion, rather than recreating a new wallet.
        Wallet newWallet = new Wallet(javaScriptWalletGenerationResult.getMnemonic(),
                javaScriptWalletGenerationResult.getDerivationPath());

        return new WalletGenerationResult(javaScriptWalletGenerationResult.getMnemonic(),
                javaScriptWalletGenerationResult.getDerivationPath(), newWallet);
    }

    /**
     * @return the address of this 'wallet'
     */
    public String getAddress() {
        return javaScriptWallet.getAddress();
    }

    /**
     * @return the public key of this 'wallet'
     */
    public String getPublicKey() {
        return javaScriptWallet.getPublicKey();
    }

    /**
     * @return the private key of this 'wallet'
     */
    public String getPrivateKey() {
        return javaScriptWallet.getPrivateKey();
    }

    /**
     * Sign the given input.
     *
     * @param input The input to sign as a hexadecimal string.
     * @return A hexadecimal encoded signature.
     * @throws XpringKitException If the input is malformed.
     */
    public String sign(String input) throws XpringKitException {
        return javaScriptWallet.sign(input);
    }

    /**
     * Verify that a given signature is valid for the given message.
     *
     * @param message   A message in hexadecimal encoding.
     * @param signature A signature in hexademical encoding.
     * @return A boolean indicating the validity of the signature.
     */
    public boolean verify(String message, String signature) {
        return javaScriptWallet.verify(message, signature);
    }

}
