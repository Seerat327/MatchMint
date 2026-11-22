
package Backend.srcc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/MatchMint";

    private static final String USER =
        System.getenv("MATCHMINT_DB_USER");

    private static final String PASSWORD =
        System.getenv("MATCHMINT_DB_PASSWORD");

    public static Connection getConnection() {

        if (USER == null || PASSWORD == null) {
            System.out.println("Database credentials not configured!");
            return null;
        }

        try {
            Connection con = DriverManager.getConnection(
                URL, USER, PASSWORD
            );

            System.out.println(
                "MatchMint Database Connected Successfully!"
            );

            return con;

        } catch (SQLException e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();

            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}