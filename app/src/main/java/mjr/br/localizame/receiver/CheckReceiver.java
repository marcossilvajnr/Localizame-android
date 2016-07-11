package mjr.br.localizame.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import mjr.br.localizame.R;
import mjr.br.localizame.activity.MapsActivity;
import mjr.br.localizame.application.ApplicationHelper;
import mjr.br.localizame.dao.impl.VinculoDispositivoPosicaoDAOImpl;
import mjr.br.localizame.dao.interfaces.VinculoDispositivoPosicaoDAO;
import mjr.br.localizame.helper.Constants;
import mjr.br.localizame.model.VinculoDispositivoPosicao;
import mjr.br.localizame.network.LocalizameRest;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcos on 7/8/16.
 */

public class CheckReceiver extends BroadcastReceiver {
    private static final String TAG = "CheckReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(TAG, "onReceive");

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(Constants.REST_IP)
                .build();

        LocalizameRest localizameRest = retrofit.create(LocalizameRest.class);
        Observable<VinculoDispositivoPosicao> vinculoRest = localizameRest.getVinculo(Build.SERIAL);

        vinculoRest.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VinculoDispositivoPosicao>() {
                    @Override
                    public void onCompleted() {
                        Log.w(TAG, "vinculoRest - getVinculo - complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "vinculoRest - getVinculo - error: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(VinculoDispositivoPosicao vinculoDispositivoPosicao) {
                        Log.e("resume", vinculoDispositivoPosicao.toString());

                        vinculoDispositivoPosicao.setKey(Build.SERIAL);
                        vinculoDispositivoPosicao.getPosicaoMobileCurrent().setKey(Build.SERIAL);
                        vinculoDispositivoPosicao.getPosicaoMobileFollow().setKey(vinculoDispositivoPosicao.getDispositivoMobileFollow().getId());

                        VinculoDispositivoPosicaoDAO vinculoDispositivoPosicaoDAO = new VinculoDispositivoPosicaoDAOImpl();
                        vinculoDispositivoPosicaoDAO.save(vinculoDispositivoPosicao);

                        ApplicationHelper.getInstance().stopCheck();

                        Intent intent = new Intent(ApplicationHelper.getInstance().getContext(), MapsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ApplicationHelper.getInstance().getContext().startActivity(intent);
                    }
                });
    }

}
