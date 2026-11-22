
package Backend.srcc;

public class User {

    private int userId;
    private String fullName;
    private String email;
    private String password;

    // Constructor
    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setter
    public void setUserId(int userId) {
        this.userId = userId;
    }
}

