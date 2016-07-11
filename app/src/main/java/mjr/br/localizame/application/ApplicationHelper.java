package mjr.br.localizame.application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mjr.br.localizame.receiver.CheckReceiver;

/**
 * Created by marcos on 7/7/16.
 */

public class ApplicationHelper extends android.app.Application {
    private PendingIntent pendingIntentCheck;
    private Intent intentCheck;
    private Context context;
    private static ApplicationHelper instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new Answers());
        instance = this;
        context = getApplicationContext();

        startRealm();
        startCheck();
    }

    private void startRealm(){
        RealmConfiguration.Builder builder = new RealmConfiguration.Builder(getApplicationContext());
        builder.name("localizame.realm");
        builder.schemaVersion(1);
        RealmConfiguration realmConfiguration = builder.build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private void startCheck(){
        stopCheck();
        intentCheck = new Intent(getApplicationContext(), CheckReceiver.class);
        pendingIntentCheck = PendingIntent.getBroadcast(getApplicationContext(), 0, intentCheck, 0);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 60 * 1000, pendingIntentCheck);
    }

    public synchronized void stopCheck(){
        if (pendingIntentCheck != null) {
            pendingIntentCheck = PendingIntent.getBroadcast(getApplicationContext(), 0, intentCheck, 0);
            pendingIntentCheck.cancel();
            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntentCheck);
        }
    }

    public Context getContext() {
        return context;
    }

    public static ApplicationHelper getInstance() {
        return instance;
    }
}
