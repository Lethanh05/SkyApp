package com.example.skymall.data.remote.DTO;

public class OrderStatusEventDto {
    public String old_status;  // nullable
    public String new_status;  // NOT NULL
    public String changed_at;  // timestamp
    public String note;        // nullable
}
