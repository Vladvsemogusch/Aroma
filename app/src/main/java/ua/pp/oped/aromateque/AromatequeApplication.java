package ua.pp.oped.aromateque;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;
import ua.pp.oped.aromateque.utility.Constants;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class AromatequeApplication extends android.app.Application {
    private Retrofit retrofit;
    private static MagentoRestService apiMagento;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiMagento = retrofit.create(MagentoRestService.class);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
//            Timber.plant(new CrashReportingTree());
        }
    }

    public static MagentoRestService getApiMagento() {
        return apiMagento;
    }
}


