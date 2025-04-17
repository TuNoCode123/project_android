package com.example.formular_cookie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

// Đại diện cho một công thức nấu ăn với các thông tin như tiêu đề, hình ảnh, nguyên liệu và các bước.
public class Recipe implements Parcelable {
    private String id;
    private String title;
    private String imageUrl;
    private String summary;
    private List<Ingredient> ingredients;
    private List<String> steps;
    private String category;
    private String authorID;
    private Author author;
    private String authorImageUrl;

    // Constructor để khởi tạo các giá trị mặc định.
    public Recipe() {
        // ingredients = new ArrayList<>();
        steps = new ArrayList<>();
    }

    // Getter và Setter cho các thuộc tính của công thức.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getAuthorName() {
        if (author != null && author.getName() != null) {
            return author.getName();
        }
        return "Unknown";
    }

    public String getAuthorImageUrl() {
        return authorImageUrl;
    }

    public void setAuthorImageUrl(String authorImageUrl) {
        this.authorImageUrl = authorImageUrl;
    }

    // Parcelable implementation để cho phép truyền đối tượng Recipe giữa các thành
    // phần.
    protected Recipe(Parcel in) {
        id = in.readString();
        title = in.readString();
        imageUrl = in.readString();
        summary = in.readString();
        category = in.readString();
        authorID = in.readString();
        author = in.readParcelable(Author.class.getClassLoader());
        authorImageUrl = in.readString();
        ingredients = new ArrayList<>();
        in.readList(ingredients, Ingredient.class.getClassLoader());
        steps = new ArrayList<>();
        in.readStringList(steps);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeString(summary);
        dest.writeString(category);
        dest.writeString(authorID);
        dest.writeParcelable(author, flags);
        dest.writeString(authorImageUrl);
        dest.writeList(ingredients);
        dest.writeList(steps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
