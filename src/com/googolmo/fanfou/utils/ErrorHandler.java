/*
 * Copyright (c) 2013. By @GoogolMo
 */

package com.googolmo.fanfou.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.googolmo.fanfou.R;
import com.googolmo.fanfou.api.FanfouException;

/**
 * User: GoogolMo
 * Date: 13-3-8
 * Time: 上午8:24
 */
public class ErrorHandler extends Handler{

    private Context mContext;
    private CallBack mCallBack;

    private ErrorHandler(Context mContext) {
        super();
//        Looper.prepare();

        this.mContext = mContext;
    }

    private ErrorHandler(Handler.Callback callback, Context mContext) {
        super(callback);
        this.mContext = mContext;
    }

    private ErrorHandler(Looper looper, Context mContext) {
        super(looper);
        this.mContext = mContext;
    }

    private ErrorHandler(Looper looper, Handler.Callback callback, Context mContext) {
        super(looper, callback);
        this.mContext = mContext;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == 400) {
            //TODO
            if (msg.obj instanceof FanfouException) {
                String showType = msg.getData().getString("showtype");
                FanfouException e = (FanfouException)msg.obj;
                if (mCallBack != null) {
                    mCallBack.beforeShow();
                }
                if (showType.equals(ShowType.TOAST.name())) {
                    Toast.makeText(mContext, e.getError(), Toast.LENGTH_LONG).show();
                    if (mCallBack != null) {
                        mCallBack.afterShow();
                    }
                } else if (showType.equals(ShowType.DIALOG.name())) {
                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                            .setTitle(mContext.getString(R.string.error_title))
                            .setMessage(e.getError())
                            .setPositiveButton(mContext.getString(R.string.sure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    if (mCallBack != null) {
                                        mCallBack.afterShow();
                                    }
                                }
                            })
                            .create();
                    dialog.show();
                }
            }
        }
    }

    public void handlerError(FanfouException e, CallBack callBack) {
        handlerError(e, ShowType.TOAST, callBack);
    }

    public void handlerError(FanfouException e, ShowType type, CallBack callBack) {
        Message msg = this.obtainMessage(400);
        msg.obj = e;
        mCallBack = callBack;
        Bundle data = new Bundle();
        data.putString("showtype", type.name());
        msg.setData(data);
        this.sendMessage(msg);
    }

//    public class ErrorDialog{
//        private String title;
//        private String message;
//    }


    public interface CallBack{
        public void beforeShow();
        public void afterShow();
    }

    public enum ShowType{
        TOAST,
        DIALOG,
    };

    public static void handlerError(Context context, FanfouException e, ShowType type, CallBack callBack) {
        Looper.prepare();
        ErrorHandler handler = new ErrorHandler(context);
        handler.handlerError(e, type, callBack);
    }


}
