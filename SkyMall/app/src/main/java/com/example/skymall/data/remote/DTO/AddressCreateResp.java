package com.example.skymall.data.remote.DTO;

public class AddressCreateResp {
    public boolean success;
    public String error;
    public String message;
    public AddressData data;

    public static class AddressData {
        public int id;
    }
}
