package com.samwolfand.unreeld;

/**
 * Created by wkh176 on 1/6/16.
 */
public class TestUnreeldApplication extends UnreeldApplication {

    private ApplicationModule mApplicationModule;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setApplicationModule(ApplicationModule applicationModule) {
        this.mApplicationModule = applicationModule;
        initComponent();
    }


    public ApplicationModule getApplicationModule() {

        return mApplicationModule;
    }
}
