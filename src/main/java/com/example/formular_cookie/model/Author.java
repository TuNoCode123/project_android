package com.example.formular_cookie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

// Đại diện cho một tác giả với các thông tin như tên, hình ảnh, số lượng người theo dõi và email.
public class Author implements Parcelable {
    private String id;
    private String name;
    private String imageUrl;
    private int followers;
    private String email;

    // Constructor mặc định để khởi tạo giá trị mặc định.
    public Author() {
    }

    // Constructor với tham số để khởi tạo một tác giả.
    public Author(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getter và Setter cho các thuộc tính của tác giả.
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getFollowers() {
        return followers;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Phương thức chuyển đổi từ Map Firestore sang đối tượng Author.
    public static Author fromMap(String id, Map<String, Object> map) {
        Author author = new Author();
        author.id = id;

        if (map.containsKey("name"))
            author.name = (String) map.get("name");
        if (map.containsKey("imageUrl"))
            author.imageUrl = (String) map.get("imageUrl");
        if (map.containsKey("email"))
            author.email = (String) map.get("email");

        // Lấy số lượng người theo dõi dưới dạng Long và chuyển đổi sang int.
        if (map.containsKey("followers")) {
            Object followers = map.get("followers");
            if (followers instanceof Long) {
                author.followers = ((Long) followers).intValue();
            } else if (followers instanceof Integer) {
                author.followers = (Integer) followers;
            }
        }

        return author;
    }

    // Parcelable implementation để cho phép truyền đối tượng Author giữa các thành
    // phần.
    protected Author(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        followers = in.readInt();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeInt(followers);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}