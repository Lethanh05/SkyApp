package com.example.skymall.ui.address;

import java.io.Serializable;
import java.util.UUID;

public class Address implements Serializable {
    public String id = UUID.randomUUID().toString();
    public String name;
    public String phone;
    public String line1;     // số nhà, đường
    public String ward;      // phường/xã
    public String district;  // quận/huyện
    public String province;  // tỉnh/thành
    public String note;      // ghi chú
    public boolean isDefault;

    public String fullAddress() {
        return (line1 == null ? "" : line1) + ", " +
                (ward == null ? "" : ward) + ", " +
                (district == null ? "" : district) + ", " +
                (province == null ? "" : province);
    }
}
