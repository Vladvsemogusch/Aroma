package ua.pp.oped.aromateque.model;


public class ShortProduct {
    private int id;
    private String imageUrl;
    private String brand;
    private String name;
    private String typeAndVolume;
    private String price;
    private String oldPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeAndVolume() {
        return typeAndVolume;
    }

    public void setTypeAndVolume(String typeAndVolume) {
        this.typeAndVolume = typeAndVolume;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }
}
