package com.example.skymall.ui.address;

import java.io.Serializable;

public class Address implements Serializable {
    public int id;
    public String name;           // Maps to receiver_name in API
    public String phone;          // Maps to receiver_phone in API
    public String addressLine;    // Maps to address_line in API
    public String ward;
    public String district;
    public String province;       // Maps to province in API (not city)
    public String note;
    public boolean isDefault;

    // Constructor for API compatibility
    public Address() {}

    // Constructor from API DTO
    public Address(com.example.skymall.data.remote.DTO.AddressListResp.AddressDto dto) {
        this.id = dto.id;
        this.name = dto.receiver_name;        // Map receiver_name to name
        this.phone = dto.receiver_phone;      // Map receiver_phone to phone
        this.addressLine = dto.address_line;
        this.ward = dto.ward;
        this.district = dto.district;
        this.province = dto.province;         // Use province from API
        this.isDefault = dto.is_default == 1;
    }

    // Constructor from AddressDetailResp
    public Address(com.example.skymall.data.remote.DTO.AddressDetailResp.AddressData dto) {
        this.id = dto.id;
        this.name = dto.receiver_name;
        this.phone = dto.receiver_phone;
        this.addressLine = dto.address_line;
        this.ward = dto.ward;
        this.district = dto.district;
        this.province = dto.province;
        this.isDefault = dto.is_default == 1;
    }

    public String fullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine != null && !addressLine.isEmpty()) sb.append(addressLine);
        if (ward != null && !ward.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ward);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        if (province != null && !province.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(province);
        }
        return sb.toString();
    }

    // For backward compatibility with existing UI
    public String getLine1() {
        return addressLine;
    }

    public String getProvince() {
        return province;
    }
}
