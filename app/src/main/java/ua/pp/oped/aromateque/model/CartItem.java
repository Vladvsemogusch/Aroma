package ua.pp.oped.aromateque.model;

public class CartItem {
    private int productId;
    private int qty;


    public CartItem(int productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
