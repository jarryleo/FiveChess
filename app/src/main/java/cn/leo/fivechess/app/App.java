package cn.leo.fivechess.app;

import android.app.Application;

import cn.leo.fivechess.utils.Logger;

/**
 * Created by Leo on 2017/10/12.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(this);
    }
}
