package by.nguyencongson.quiz_app.model;

public class Categories {
    private String id;
    private Category category;

    public Categories(String id, Category category) {
        this.id = id;
        this.category = category;
    }

    public Categories() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "id='" + id + '\'' +
                ", category=" + category +
                '}';
    }
}

