package com.googolmo.fanfou;


import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import com.google.gson.Gson;
import com.googolmo.fanfou.api.Api;
import com.googolmo.fanfou.data.Provider;
import com.googolmo.fanfou.utils.JsonUtils;
import com.googolmo.fanfou.utils.NLog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getName();

    //    private Shmily mApi;
    private Api mApi;
    private Provider mProvider;
    private Gson gson;

    public Api getApi() {
        return mApi;
    }

    public Provider getProvider() {
        return mProvider;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        NLog.setDebug(Constants.DEBUG);
        this.gson = JsonUtils.getGson();
        this.mApi = new Api(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET, this.gson);
        this.mProvider = new Provider(getApplicationContext(), gson);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .delayBeforeLoading(0)
                .build();
        ImageLoaderConfiguration config;
        if (Constants.DEBUG) {
            config = new ImageLoaderConfiguration.Builder(this)
                    .memoryCacheSize(getMemorySize(40))
                    .discCacheSize(75 * 1024 * 1024)
                    .discCacheFileCount(250)
                    .defaultDisplayImageOptions(defaultOptions)
                    .threadPoolSize(5)
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    .enableLogging()
                    .build();
        } else {
            config = new ImageLoaderConfiguration.Builder(this)
                    .memoryCacheSize(getMemorySize(40))
                    .discCacheSize(75 * 1024 * 1024)
                    .discCacheFileCount(250)
                    .defaultDisplayImageOptions(defaultOptions)
                    .threadPoolSize(5)
                    .discCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    .build();
        }

        ImageLoader.getInstance().init(config);
    }

    public BaseApplication() {
        super();
    }

    @Override
    public void onTerminate() {
        NLog.i(TAG, "onTerminate");
        super.onTerminate();

    }

    public int getMemorySize(int mermoryPercent) {
        int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

        if (memClass == 0) {
            memClass = 12;
        }
        if (mermoryPercent < 0) {
            mermoryPercent = 0;
        }
        if (mermoryPercent > 80) {
            mermoryPercent = 80;
        }
        int capacity = (1024 * 1024 * (memClass * mermoryPercent)) / 100;
        if (capacity <= 0) {
            capacity = 1024 * 1024 * 4;
        }
        return capacity;
    }
}
