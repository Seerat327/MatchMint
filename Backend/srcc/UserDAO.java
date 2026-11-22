
package Backend.srcc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(User user) {

        String query = "INSERT INTO users (full_name, email, password) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());

            ps.setString(3, hashedPassword);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Registration Failed!");
            e.printStackTrace();
            return false;
        }
    }
}

