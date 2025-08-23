package com.example.skymall.utils;

public class UserRole {
    public static final String CUSTOMER = "customer";
    public static final String STORE = "store";
    public static final String ADMIN = "admin";

    /**
     * Kiểm tra xem user có phải là customer không
     */
    public static boolean isCustomer(String role) {
        return CUSTOMER.equals(role);
    }

    /**
     * Kiểm tra xem user có phải là store owner không
     */
    public static boolean isStoreOwner(String role) {
        return STORE.equals(role);
    }

    /**
     * Kiểm tra xem user có phải là admin không
     */
    public static boolean isAdmin(String role) {
        return ADMIN.equals(role);
    }

    /**
     * Kiểm tra xem user có quyền quản lý store không (store owner hoặc admin)
     */
    public static boolean canManageStore(String role) {
        return isStoreOwner(role) || isAdmin(role);
    }

    /**
     * Kiểm tra xem user có quyền admin không
     */
    public static boolean hasAdminPermission(String role) {
        return isAdmin(role);
    }

    /**
     * Lấy tên hiển thị của role
     */
    public static String getRoleDisplayName(String role) {
        switch (role) {
            case CUSTOMER:
                return "Khách hàng";
            case STORE:
                return "Người bán";
            case ADMIN:
                return "Quản trị viên";
            default:
                return "Không xác định";
        }
    }
}
