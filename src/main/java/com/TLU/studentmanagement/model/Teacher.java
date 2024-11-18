package main.java.com.TLU.studentmanagement.model;


public class Teacher {
    private String id;
    private String mgv;
    private String fullName;
    private String email;  // Nếu bạn cần trường email, thêm vào đây
    private boolean isGV;
    private boolean isAdmin;



    public Teacher() {
    }

    public Teacher(String id, String mgv, String fullName) {
        this.id = id;
        this.mgv = mgv;
        this.fullName = fullName;
    }

    public Teacher(String id, String mgv, String fullName, String email, boolean isGV) {
        this.id = id;
        this.mgv = mgv;
        this.fullName = fullName;
        this.email = email;
        this.isGV = isGV;
    }

    // Getter và Setter cho tất cả các trường
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMgv() { return mgv; }
    public void setMgv(String mgv) { this.mgv = mgv; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isGV() { return isGV; }
    public void setGV(boolean isGV) { this.isGV = isGV; }


    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + id + '\'' +
                ", mgv='" + mgv + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", isGV='" + isGV + '\'' +
                ", isAdmin='" + isAdmin +
                "'}";
    }

}

