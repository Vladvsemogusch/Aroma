package ua.pp.oped.aromateque.api;


import ua.pp.oped.aromateque.utility.Constants;

public class SettlementPost {

    String modelName = "AddressGeneral";
    String calledMethod = "getSettlements";
    MethodProperties methodProperties;

    class MethodProperties {
        String FindByString;
        boolean MainCitiesOnly = true;

        public MethodProperties(String findByString) {
            FindByString = findByString;
        }
    }

    String apiKey = Constants.API_KEY;

    public SettlementPost(String findByString) {
        methodProperties = new MethodProperties(findByString);
    }

}
