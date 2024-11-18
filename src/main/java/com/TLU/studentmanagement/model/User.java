package main.java.com.TLU.studentmanagement.model;

import java.util.List;

public class User {
    private String id;
    private String fullname;
    private String msv;
    private String year;
    private String gvcn;
    private String gvcnName;
    private String gender;
    private String className;
    private String email;
    private String majorId;
    private String major;
    private boolean isAdmin;
    private boolean isGV;
    private boolean deleted;
    private String dob; // Ngày sinh
    private String phone; // Số điện thoại
    private String country; // Quốc gia
    private String address; // Địa chỉ
    private Parent parent; // Thông tin phụ huynh

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getGvcn() {
        return gvcn;
    }

    public void setGvcn(String gvcn) {
        this.gvcn = gvcn;
    }

    public String getGvcnName() {
        return gvcnName;
    }

    public void setGvcnName(String gvcnName) {
        this.gvcnName = gvcnName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public String getMajorName() {
        return major;
    }

    public void setMajorName(String major) {
        this.major = major;
    }


    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isGv() {
        return isGV;
    }

    public void setGv(boolean GV) {
        isGV = GV;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullname='" + fullname + '\'' +
                ", msv='" + msv + '\'' +
                ", gvcn='" + gvcn + '\'' +
                ", majorId='" + majorId + '\'' +
                ", year='" + year + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", dob='" + dob + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public static class Parent {
        private String fatherName;
        private String motherName;
        private String fatherJob;
        private String motherJob;
        private String parentPhone;
        private String nation;
        private String presentAddress;
        private String permanentAddress;

        // Getters and Setters
        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(String motherName) {
            this.motherName = motherName;
        }

        public String getFatherJob() {
            return fatherJob;
        }

        public void setFatherJob(String fatherJob) {
            this.fatherJob = fatherJob;
        }

        public String getMotherJob() {
            return motherJob;
        }

        public void setMotherJob(String motherJob) {
            this.motherJob = motherJob;
        }

        public String getParentPhone() {
            return parentPhone;
        }

        public void setParentPhone(String parentPhone) {
            this.parentPhone = parentPhone;
        }

        public String getNation() {
            return nation;
        }

        public void setNation(String nation) {
            this.nation = nation;
        }

        public String getPresentAddress() {
            return presentAddress;
        }

        public void setPresentAddress(String presentAddress) {
            this.presentAddress = presentAddress;
        }

        public String getPermanentAddress() {
            return permanentAddress;
        }

        public void setPermanentAddress(String permanentAddress) {
            this.permanentAddress = permanentAddress;
        }
    }
}
