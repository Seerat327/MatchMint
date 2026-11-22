
package Backend.srcc;

public class LoginTest {

    public static void main(String[] args) {

        LoginDAO dao = new LoginDAO();

        User user = dao.loginUser(
            "test@matchmint.com",
            "Test12345"
        );

        if (user != null) {
            System.out.println("Login Successful!");
            System.out.println("Welcome, " + user.getFullName());
        } else {
            System.out.println("Invalid Email or Password!");
        }
    }
}

