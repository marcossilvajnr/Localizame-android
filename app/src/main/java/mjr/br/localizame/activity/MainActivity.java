package mjr.br.localizame.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.glxn.qrgen.android.QRCode;

import mjr.br.localizame.R;
import mjr.br.localizame.dao.impl.VinculoDispositivoPosicaoDAOImpl;
import mjr.br.localizame.dao.interfaces.VinculoDispositivoPosicaoDAO;
import mjr.br.localizame.helper.Constants;
import mjr.br.localizame.helper.PermissionsHelper;
import mjr.br.localizame.model.DispositivoMobile;
import mjr.br.localizame.model.PosicaoMobile;
import mjr.br.localizame.model.VinculoDispositivoPosicao;
import mjr.br.localizame.network.LocalizameRest;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("LocalizaMe");

        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        Bitmap myBitmap = QRCode.from(Build.SERIAL).withSize(250, 250).bitmap();
        myImage.setImageBitmap(myBitmap);

        final AppCompatActivity appCompatActivity = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(appCompatActivity).initiateScan();
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            PermissionsHelper permissionsHelper = new PermissionsHelper();
            if (!permissionsHelper.checkPermission(MainActivity.this, permissionsHelper.REQUEST_FINE_LOCATION)) {
                permissionsHelper.requestPermission(MainActivity.this, permissionsHelper.REQUEST_FINE_LOCATION);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                DispositivoMobile dispositivoMobileCurrent = new DispositivoMobile();
                dispositivoMobileCurrent.setId(Build.SERIAL);
                dispositivoMobileCurrent.setNome("current");

                PosicaoMobile posicaoMobileCurrent = new PosicaoMobile();
                posicaoMobileCurrent.setKey(Build.SERIAL);

                DispositivoMobile dispositivoMobileFollow = new DispositivoMobile();
                dispositivoMobileFollow.setId(result.getContents());
                dispositivoMobileFollow.setNome("follow");

                PosicaoMobile posicaoMobileFollow = new PosicaoMobile();
                posicaoMobileFollow.setKey(result.getContents());

                VinculoDispositivoPosicao vinculoDispositivoPosicao = new VinculoDispositivoPosicao();
                vinculoDispositivoPosicao.setKey(Build.SERIAL);
                vinculoDispositivoPosicao.setAlcanceMetros(5);
                vinculoDispositivoPosicao.setForaAlcanse(false);
                vinculoDispositivoPosicao.setDispositivoMobileCurrent(dispositivoMobileCurrent);
                vinculoDispositivoPosicao.setPosicaoMobileCurrent(posicaoMobileCurrent);
                vinculoDispositivoPosicao.setDispositivoMobileFollow(dispositivoMobileFollow);
                vinculoDispositivoPosicao.setPosicaoMobileFollow(posicaoMobileFollow);

                VinculoDispositivoPosicaoDAO vinculoDispositivoPosicaoDAO = new VinculoDispositivoPosicaoDAOImpl();
                vinculoDispositivoPosicaoDAO.save(vinculoDispositivoPosicao);

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
