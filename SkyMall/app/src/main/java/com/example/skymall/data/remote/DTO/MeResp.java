package com.example.skymall.data.remote.DTO;

public class MeResp { public boolean success; public User user;
    public static class User {
        public String id;
        public String name;
        public String email;
        public String phone;
        public String avt;
        public String avatar_url;
        public String birthDate;
        public String gender;
        public String role;
    }
}