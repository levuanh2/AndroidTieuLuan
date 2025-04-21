package appnghenhac.com.model;

public class User {
    private String email;
    private String name;
    private String role;
    private boolean isSelected; // Để theo dõi lựa chọn

    public User(String email, String name, String role) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.isSelected = false;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}