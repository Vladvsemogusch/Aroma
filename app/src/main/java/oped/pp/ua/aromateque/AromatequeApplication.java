package oped.pp.ua.aromateque;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class AromatequeApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
}


