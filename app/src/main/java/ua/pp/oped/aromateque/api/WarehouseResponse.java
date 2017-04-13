package ua.pp.oped.aromateque.api;


import java.util.List;

public class WarehouseResponse {
    private boolean success;
    private List<WarehouseItem> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<WarehouseItem> getData() {
        return data;
    }

    public void setData(List<WarehouseItem> data) {
        this.data = data;
    }

    public static class WarehouseItem {
        String DescriptionRu;

        public String getDescriptionRu() {
            return DescriptionRu;
        }
    }
}
