package io.xpring.xrpl;

/**
 * Class contains artifacts of generating a new Wallet
 */
public class WalletGenerationResult {

    //Mnemonic of newly generated Wallet
    private String mnemonic;

    //Newly generated Wallet
    private Wallet wallet;

    //Derivation path of newly generated Wallet
    private String derivationPath;


    public String getMnemonic() {
        return mnemonic;
    }

    public Wallet getWallet() {
        return wallet;
    }


    public WalletGenerationResult(String mnemonic, String derivationPath, Wallet wallet) {
        this.mnemonic = mnemonic;
        this.derivationPath = derivationPath;
        this.wallet = wallet;
    }
}
