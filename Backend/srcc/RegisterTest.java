package Backend.srcc;

public class RegisterTest {

    public static void main(String[] args) {

        User user = new User(
            "Test User",
            "test@matchmint.com",
            "Test12345"
        );

        UserDAO dao = new UserDAO();

        boolean result = dao.registerUser(user);

        if (result) {
            System.out.println("User Registered Successfully!");
        } else {
            System.out.println("User Registration Failed!");
        }
    }
}
