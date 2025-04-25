package com.example.formular_cookie.utils;

public interface UploadCallback {
    void onUploadSuccess(String imageUrl);
    void onUploadFailure(String error);
}
