package com.example.skymall.data.remote.DTO;

public class UploadResp {
    public boolean success;
    public String avatar_url;
    public String error;
    public User user;

    public static class User {
        public String id;
        public String name;
        public String email;
        public String phone;
        public String avt;
        public String birthDate;
        public String gender;
        public String avatar_url;
    }
}
