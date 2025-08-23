package com.example.skymall.ui.address;

import java.util.ArrayList;
import java.util.List;

public class AddressRepository {
    private static AddressRepository INSTANCE;
    private final List<Address> data = new ArrayList<>();

    private AddressRepository() {
        // Seed demo
        Address a = new Address();
        a.name = "Nguyễn Văn A";
        a.phone = "0909123456";
        a.line1 = "12/34 Lê Lợi";
        a.ward = "Phường Bến Nghé";
        a.district = "Quận 1";
        a.province = "TP.HCM";
        a.note = "Giao giờ hành chính";
        a.isDefault = true;
        data.add(a);
    }

    public static AddressRepository get() {
        if (INSTANCE == null) INSTANCE = new AddressRepository();
        return INSTANCE;
    }

    public List<Address> list() {
        return data;
    }

    public void add(Address addr) {
        if (addr.isDefault) clearDefault();
        data.add(0, addr);
    }

    public void update(Address addr) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).id.equals(addr.id)) {
                if (addr.isDefault) clearDefault();
                data.set(i, addr);
                return;
            }
        }
    }

    public void remove(String id) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).id.equals(id)) {
                data.remove(i);
                return;
            }
        }
    }

    public void setDefault(String id) {
        clearDefault();
        for (Address a : data) {
            if (a.id.equals(id)) {
                a.isDefault = true;
                break;
            }
        }
    }

    private void clearDefault() {
        for (Address a : data) a.isDefault = false;
    }
}
