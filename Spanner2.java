package Draft2;
import com.google.cloud.spanner.*;
import io.xpring.xrpl.XpringKitException;

import java.util.ArrayList;
import java.util.List;

public class Spanner2 {

    private DatabaseClient dbClient;

    public void initialize() {
        SpannerOptions options = SpannerOptions.newBuilder().build();
        Spanner spanner = options.getService();

        String instanceId = "centralDatabase";
        String databaseId = "database";

        try {
             dbClient = spanner.getDatabaseClient(DatabaseId.of(options.getProjectId(), instanceId, databaseId));

        } catch (Exception e) {
            System.out.println("Error while loading Cloud Spanenr Database " + e.getMessage());
        }
    }

    public DatabaseClient getDbClient() {
        return this.dbClient;
    }

    /**
     *
     * @param username
     * @return true if parameter passed in is available
     */
    public boolean usernameAvailable(String username) {
        ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of("SELECT username from users"));

        while (resultSet.next()) {
            String currUsername = resultSet.getString(0);
            if (currUsername.equals(username)) {
                return false;
            }
        }
        return true;
    }

    public static void spannerWriter(DatabaseClient dbClient, Account2 userToAdd) throws XpringKitException, Exception {
        List<Mutation> mutations = new ArrayList<>();
        mutations.add(
                Mutation.newInsertBuilder("users")
                        .set("balance").to(userToAdd.getBalance().floatValue())
                        .set("firstName").to(userToAdd.getFirstName())
                        .set("lastName").to(userToAdd.getLastName())
                        .set("uniqueID").to(userToAdd.getUniqueID())
                        .set("walletAddress").to(userToAdd.getWallet().getAddress())
                        .build());
        dbClient.write(mutations);
    }

    public static void spannerUpdater(DatabaseClient dbClient, Account2 userToAdd) throws XpringKitException, Exception {
        List<Mutation> mutations = new ArrayList<>();
        mutations.add(
                Mutation.newUpdateBuilder("users")
                        .set("balance").to(userToAdd.getBalance().floatValue())
                        .set("firstName").to(userToAdd.getFirstName())
                        .set("lastName").to(userToAdd.getLastName())
                        .set("uniqueID").to(userToAdd.getUniqueID())
                        .set("walletAddress").to(userToAdd.getWallet().getAddress())
                        .build());
        dbClient.write(mutations);
    }


}