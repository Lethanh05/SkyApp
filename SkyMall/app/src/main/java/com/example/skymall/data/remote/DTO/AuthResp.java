package com.example.skymall.data.remote.DTO;


public class AuthResp { public boolean success; public String token; public User user;
    public static class User { public String id, name, email; } }