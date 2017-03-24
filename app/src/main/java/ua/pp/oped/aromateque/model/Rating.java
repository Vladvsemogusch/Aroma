package ua.pp.oped.aromateque.model;


public class Rating {
    private int stars;
    private String name;

    public Rating(int stars, String name) {
        this.stars = stars;
        this.name = name;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
