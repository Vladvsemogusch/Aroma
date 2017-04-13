package ua.pp.oped.aromateque.api;


import java.util.List;

public class SearchSettlementResponse {

    String success;
    List<Wrapper> data;

    public String getSuccess() {
        return success;
    }

    public List<SearchSettlementItem> getCities() {
        return data.get(0).Addresses;
    }

    public class Wrapper {
        String TotalCount;
        List<SearchSettlementItem> Addresses;


    }

    public static class SearchSettlementItem {
        int Warehouses;
        String MainDescription;
        String Area;
        String Region;
        String SettlementTypeCode;

        public int getWarehouses() {
            return Warehouses;
        }

        public void setWarehouses(int warehouses) {
            Warehouses = warehouses;
        }

        public String getMainDescription() {
            return MainDescription;
        }

        public void setMainDescription(String mainDescription) {
            MainDescription = mainDescription;
        }

        public String getArea() {
            return Area;
        }

        public void setArea(String area) {
            Area = area;
        }

        public String getRegion() {
            return Region;
        }

        public void setRegion(String region) {
            Region = region;
        }

        public String getSettlementTypeCode() {
            return SettlementTypeCode;
        }

        public void setSettlementTypeCode(String settlementTypeCode) {
            SettlementTypeCode = settlementTypeCode;
        }
    }
}
