package ua.pp.oped.aromateque.api;


import ua.pp.oped.aromateque.utility.Constants;

public class SearchSettlementPost {

    String modelName = "Address";
    String calledMethod = "searchSettlements";
    MethodProperties methodProperties;

    class MethodProperties {
        String CityName;
        int Limit = 20;

        public MethodProperties(String CityName) {
            this.CityName = CityName;
        }
    }

    String apiKey = Constants.API_KEY;

    public SearchSettlementPost(String CityName) {
        methodProperties = new MethodProperties(CityName);
    }

}
