package com.samwolfand.unreeld;

import android.app.Application;

/**
 * Created by wkh176 on 1/8/16.
 */
public class MockApplicationModule extends ApplicationModule {



    public MockApplicationModule(Application application) {
        super(application);
    }


    @Override
    Application providesApplication() {
        return super.providesApplication();
    }


}
