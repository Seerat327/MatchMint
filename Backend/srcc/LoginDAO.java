
package Backend.srcc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    public User loginUser(String email, String password) {

        String query = "SELECT * FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String storedPassword = rs.getString("password");

                if (PasswordUtil.verifyPassword(password, storedPassword)) {

                    User user = new User(
                        rs.getString("full_name"),
                        rs.getString("email"),
                        storedPassword
                    );

                    user.setUserId(rs.getInt("user_id"));

                    return user;
                }
            }

        } catch (SQLException e) {
            System.out.println("Login Failed!");
            e.printStackTrace();
        }

        return null;
    }
}
