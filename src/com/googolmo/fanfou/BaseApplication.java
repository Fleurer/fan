package com.googolmo.fanfou;


import android.app.Application;
import com.google.gson.Gson;
import com.googolmo.fanfou.api.Api;
import com.googolmo.fanfou.utils.JsonUtils;
import com.googolmo.fanfou.utils.NLog;
import com.googolmo.fanfou.data.Provider;
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
                .cacheInMemory(true)
                .cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.delayBeforeLoading(0)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCachePercent(30)
				.discCacheSize(75 * 1024 * 1024)
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory(false)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .offOutOfMemoryHandling(false)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .enableLogging(Constants.DEBUG)
                .build();
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
}
