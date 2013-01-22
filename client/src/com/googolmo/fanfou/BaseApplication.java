package com.googolmo.fanfou;


import android.app.Application;
import com.googolmo.fanfouApi.Shmily;
import com.googolmo.fanfouApi.utils.NLog;
import com.googolmo.fanfou.data.Provider;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class BaseApplication extends Application {

    private Shmily mApi;
    private Provider mProvider;

    public Shmily getApi() {
        return mApi;
    }

    public Provider getProvider() {
        return mProvider;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        NLog.setDebug(Constants.DEBUG);
        this.mApi = new Shmily(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        this.mProvider = new Provider(this);
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
}
