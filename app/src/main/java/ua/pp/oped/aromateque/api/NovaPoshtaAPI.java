package ua.pp.oped.aromateque.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NovaPoshtaAPI {

    @POST("json/")
    Call<SettlementResponse> getCities(@Body CityPost settlementPost);

    @POST("json/")
    Call<SettlementResponse> getSettlements(@Body SettlementPost settlementPost);

    @POST("json/")
    Call<SearchSettlementResponse> searchSettlements(@Body SearchSettlementPost settlementPost);

    @POST("json/")
    Call<WarehouseResponse> getWarehouses(@Body WarehousePost warehousePost);
}
