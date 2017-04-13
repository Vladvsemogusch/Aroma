package ua.pp.oped.aromateque;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;
import ua.pp.oped.aromateque.api.MagentoAPI;
import ua.pp.oped.aromateque.api.NovaPoshtaAPI;
import ua.pp.oped.aromateque.utility.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class AromatequeApplication extends android.app.Application {
    private Retrofit retrofit;
    private static MagentoAPI magentoAPI;
    private static NovaPoshtaAPI novaPoshtaAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .build();
        magentoAPI = retrofit.create(MagentoAPI.class);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
//            Timber.plant(new CrashReportingTree());
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NOVA_POSHTA_API_URL)
                .addConverterFactory(gsonConverterFactory)
                .build();
        novaPoshtaAPI = retrofit.create(NovaPoshtaAPI.class);
    }

    public static MagentoAPI getMagentoAPI() {
        return magentoAPI;
    }

    public static NovaPoshtaAPI getNovaPoshtaAPI() {
        return novaPoshtaAPI;
    }
}


