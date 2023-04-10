package by.nguyencongson.quiz_app.model;

public class Category {

    private String Image;
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public Category(String name, String image) {
        this.Name = name;
        this.Image = image;
    }

    public Category() {
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + Name + '\'' +
                ", image='" + Image + '\'' +
                '}';
    }
}
