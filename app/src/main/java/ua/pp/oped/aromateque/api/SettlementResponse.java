package ua.pp.oped.aromateque.api;


import java.util.ArrayList;
import java.util.List;

public class SettlementResponse {
    String success;
    List<GetSettlementItem> data;

    public String getSuccess() {
        return success;
    }

    public List<GetSettlementItem> getSettlements() {
        return data;
    }


    public class GetSettlementItem {
        String Description;
        String DescriptionRu;
        String SettlementTypeDescriptionRu;
        String RegionsDescription;
        String RegionsDescriptionRu;
        String AreaDescription;
        String AreaDescriptionRu;

        public String getDescription() {
            return Description;
        }

        public String getRegionsDescription() {
            return RegionsDescription;
        }

        public String getAreaDescription() {
            return AreaDescription;
        }

        public String getDescriptionRu() {
            return DescriptionRu;
        }

        public String getSettlementTypeDescriptionRu() {
            return SettlementTypeDescriptionRu;
        }

        public String getRegionsDescriptionRu() {
            return RegionsDescriptionRu;
        }

        public String getAreaDescriptionRu() {
            return AreaDescriptionRu;
        }
    }

    public List<SearchSettlementResponse.SearchSettlementItem> getSearchSettlements() {
        List<SearchSettlementResponse.SearchSettlementItem> searchSettlementItems = new ArrayList<>();
        for (GetSettlementItem getSettlementItem :
                data) {
            SearchSettlementResponse.SearchSettlementItem searchSettlementItem = new SearchSettlementResponse.SearchSettlementItem();
            searchSettlementItem.setMainDescription(getSettlementItem.getDescription());
            searchSettlementItem.setRegion(getSettlementItem.getRegionsDescription());
            searchSettlementItem.setArea(getSettlementItem.getAreaDescription());
            //PlaceHolder may be necessary to change if number of warehouses will be used.
            searchSettlementItem.setWarehouses(1);
            searchSettlementItems.add(searchSettlementItem);
        }
        return searchSettlementItems;
    }
}
