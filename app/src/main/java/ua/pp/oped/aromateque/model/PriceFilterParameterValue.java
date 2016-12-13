package ua.pp.oped.aromateque.model;


public class PriceFilterParameterValue extends FilterParameterValue {
    private String name;
    private int id;
    private int minPrice;
    private int maxPrice;

    public PriceFilterParameterValue(int id, int minPrice, int maxPrice) {
        super("price", id);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }
}
