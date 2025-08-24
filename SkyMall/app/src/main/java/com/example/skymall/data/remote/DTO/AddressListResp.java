package com.example.skymall.data.remote.DTO;

import java.util.List;

public class AddressListResp {
    public boolean success;
    public String error;
    public List<AddressDto> data; // Changed from 'addresses' to 'data'

    public static class AddressDto {
        public int id;
        public int userId;
        public String receiver_name;   // Changed field name to match API
        public String receiver_phone;  // Changed field name to match API
        public String address_line;
        public String ward;
        public String district;
        public String province;        // Changed back to 'province' from API
        public int is_default;
        public String created_at;
        public String updated_at;
    }
}
