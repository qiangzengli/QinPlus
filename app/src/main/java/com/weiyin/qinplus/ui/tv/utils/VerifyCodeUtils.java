package com.weiyin.qinplus.ui.tv.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weiyin.qinplus.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 计时器
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class VerifyCodeUtils {
    public static void sendCode(final Context context, final TextView view) {
        final int count = 60;
        //设置0延迟，每隔一秒发送一条数据
        Observable.interval(0, 1, TimeUnit.SECONDS)
                //设置循环11次
                .take(count + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return count - aLong;
                    }
                })
                .doOnSubscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //在发送数据的时候设置为不能点击
                        view.setEnabled(false);
                    }
                })
//操作UI主要在UI线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onComplete() {
                        //数据发送完后设置为原来的文字
                        view.setText(context.getString(R.string.login_send));
                        view.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) { //接受到一条就是会操作一次UI
                        view.setText(aLong + "秒");
                    }
                });
    }

    public static void progressUpdate(final TextView view, final ProgressBar progressBar, final int sec) {
        final int count = sec;
        Observable.interval(0, 100, TimeUnit.MILLISECONDS)//设置0延迟，每隔一毫秒发送一条数据
                .take(count / 100 + 1)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return count - aLong * 100;
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) { //接受到一条就是会操作一次UI
                        Log.d("VerifyCodeUtils", "aLong = " + aLong + "");
                        int progress = (int) ((1 - (aLong.doubleValue() / sec)) * 100);
                        Log.d("VerifyCodeUtils", "progress = " + progress + "");
                        view.setText(format(aLong));
                        progressBar.setProgress(progress);
                    }
                });
    }

    private static String format(long timeLengt) {
        int s = (int) (timeLengt / 1000);
        int m = s / 60;
        s = s % 60;
        return m + ":" + s;
    }
}
