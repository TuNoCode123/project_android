package com.example.formular_cookie;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CloudinaryUploader {

    private static final String BASE_URL = "https://api.cloudinary.com/v1_1/ddzviqgwt/";  // Thay YOUR_CLOUD_NAME bằng tên cloud của bạn

    private static final String UPLOAD_PRESET = "android_studio"; // Thay YOUR_UPLOAD_PRESET bằng upload preset của bạn

    private Retrofit retrofit;
    private CloudinaryService cloudinaryService;

    public CloudinaryUploader() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        cloudinaryService = retrofit.create(CloudinaryService.class);
    }

    public void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);

            // Tạo RequestBody từ byte array
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

            RequestBody preset = RequestBody.create(MediaType.parse("text/plain"), UPLOAD_PRESET);

            Call<CloudinaryResponse> call = cloudinaryService.uploadImage(preset, body);
            call.enqueue(new Callback<CloudinaryResponse>() {
                @Override
                public void onResponse(Call<CloudinaryResponse> call, Response<CloudinaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String imageUrl = response.body().getSecure_url();
                        Log.d("Cloudinary", "Image uploaded successfully: " + imageUrl);
                        if (callback != null) {
                            callback.onUploadSuccess(imageUrl);
                        }
                    } else {
                        Log.e("Cloudinary", "Upload failed: " + response.message());
                        if (callback != null) {
                            callback.onUploadFailure("Upload failed: " + response.message());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CloudinaryResponse> call, Throwable t) {
                    Log.e("Cloudinary", "Upload failed: " + t.getMessage());
                    if (callback != null) {
                        callback.onUploadFailure("Upload failed: " + t.getMessage());
                    }
                }
            });

        } catch (IOException e) {
            Log.e("Cloudinary", "Lỗi đọc ảnh từ URI: " + e.getMessage());
            if (callback != null) {
                callback.onUploadFailure("Lỗi đọc ảnh: " + e.getMessage());
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

}
