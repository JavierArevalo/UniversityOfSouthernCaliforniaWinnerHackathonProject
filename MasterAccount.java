package Draft2;

public class MasterAccount {

    /**
     * Should contain Central Logic instance running
     * All Account2 classes extend Master Account so they all share the same CentralLogic instance
     */
    public static CentralLogic centralLogic;

    public CentralLogic initializeCentralLogic() {
        if (centralLogic == null) {
            this.centralLogic = CentralLogic.getInstance();
        }
        return this.centralLogic;
    }

}
