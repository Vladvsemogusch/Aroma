package ua.pp.oped.aromateque.api;


import ua.pp.oped.aromateque.utility.Constants;

public class CityPost {
    String apiKey = Constants.API_KEY;
    String modelName = "Address";
    String calledMethod = "getSettlements";
    MethodProperties methodProperties;

    class MethodProperties {
        String FindByString;

        public MethodProperties(String findByString) {
            FindByString = findByString;
        }
    }


    public CityPost(String findByString) {
        methodProperties = new MethodProperties(findByString);
    }

}
