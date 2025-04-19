package com.example.formular_cookie;

public interface UploadCallback {
    void onUploadSuccess(String imageUrl);
    void onUploadFailure(String error);
}
