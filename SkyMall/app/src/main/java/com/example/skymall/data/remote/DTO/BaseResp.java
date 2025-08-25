package com.example.skymall.data.remote.DTO;

public class BaseResp<T> {
    public boolean success;
    public String message;
    public T data;
}
