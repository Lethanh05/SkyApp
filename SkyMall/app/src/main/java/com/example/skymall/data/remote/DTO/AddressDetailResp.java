package com.example.skymall.data.remote.DTO;

public class AddressDetailResp {
    public boolean success;
    public String error;
    public AddressData data;

    public static class AddressData {
        public int id;
        public int userId;
        public String receiver_name;
        public String receiver_phone;
        public String address_line;
        public String ward;
        public String district;
        public String province;
        public int is_default;
        public String created_at;
        public String updated_at;
    }
}
