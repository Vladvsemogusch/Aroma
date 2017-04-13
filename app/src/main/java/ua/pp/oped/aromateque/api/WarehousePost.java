package ua.pp.oped.aromateque.api;


import ua.pp.oped.aromateque.utility.Constants;

public class WarehousePost {
    String apiKey = Constants.API_KEY;
    String modelName = "AddressGeneral";
    String calledMethod = "getWarehouses";
    MethodProperties methodProperties;

    private class MethodProperties {
        String CityName;
        String FindByString;

        public MethodProperties(String cityName, String findByString) {
            CityName = cityName;
            FindByString = findByString;
        }
    }


    public WarehousePost(String cityName, String findByString) {
        methodProperties = new MethodProperties(cityName, findByString);
    }

}
