package mjr.br.localizame.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import mjr.br.localizame.R;
import mjr.br.localizame.dao.impl.VinculoDispositivoPosicaoDAOImpl;
import mjr.br.localizame.dao.interfaces.VinculoDispositivoPosicaoDAO;
import mjr.br.localizame.helper.Constants;
import mjr.br.localizame.model.DispositivoMobile;
import mjr.br.localizame.model.PosicaoMobile;
import mjr.br.localizame.model.VinculoDispositivoPosicao;
import mjr.br.localizame.network.LocalizameRest;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private static GoogleApiClient googleApiClient;
    private VinculoDispositivoPosicao vinculoDispositivoPosicao;
    private Boolean firstSend = false;
    private Button btnParar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar();
        setTitle("LocalizaMe");

        VinculoDispositivoPosicaoDAO vinculoDispositivoPosicaoDAO = new VinculoDispositivoPosicaoDAOImpl();
        vinculoDispositivoPosicao = vinculoDispositivoPosicaoDAO.get(Build.SERIAL);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnParar = (Button) findViewById(R.id.buttonParar);
        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleApiClient != null) {
                    if (googleApiClient.isConnected()) {
                        Log.w(TAG, "googleApiClient - parando captura de localização");
                        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, MapsActivity.this);
                    }
                    Log.w(TAG, "googleApiClient - desconectando...");
                    googleApiClient.disconnect();
                }

                Retrofit retrofit = new Retrofit.Builder()
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(JacksonConverterFactory.create())
                        .baseUrl(Constants.REST_IP)
                        .build();

                LocalizameRest localizameRest = retrofit.create(LocalizameRest.class);
                Observable<Response<Void>> vinculoRest = localizameRest.removeVinculo(Build.SERIAL);

                vinculoRest.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Void>>() {
                    @Override
                    public void onCompleted() {
                        Log.w(TAG, "localizameRest - removeVinculo - onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(TAG, "localizameRest - removeVinculo - onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Response<Void> voidResponse) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onStart() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        googleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                Log.w(TAG, "googleApiClient - parando captura de localização");
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
            Log.w(TAG, "googleApiClient - desconectando...");
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w(TAG, "GoogleApiClient - onConnectionFailed - código de erro: " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.w(TAG, "GoogleApiClient - onConnectionSuspended - código de erro: " + cause);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(10 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        } else {
            Log.w(TAG, "GoogleApiClient - onConnected - permissão ACCESS_FINE_LOCATION");
        }
        Log.w(TAG, "GoogleApiClient - onConnected - ok");
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.e(TAG, "new location: " + location.toString());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        double radiusInMeters = 15;
        int strokeColor = 0xffff0000;
        int shadeColor = 0x44ff0000;

        mMap.clear();

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        mMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        markerOptions.title("Sua Posição");

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        vinculoDispositivoPosicao.getPosicaoMobileCurrent().setLatitude(location.getLatitude());
        vinculoDispositivoPosicao.getPosicaoMobileCurrent().setLongitude(location.getLongitude());
        realm.commitTransaction();

        if (!firstSend) {
            Log.w("teste","first send");
            firstSend(location);
        } else {
            Log.w("teste","sendLocation");
            sendLocation(location);
        }
    }

    public void sendLocation(final Location location) {
        DispositivoMobile dispositivoMobileCurrent = new DispositivoMobile();
        dispositivoMobileCurrent.setId(Build.SERIAL);
        dispositivoMobileCurrent.setNome("current");

        PosicaoMobile posicaoMobileCurrent = new PosicaoMobile();
        posicaoMobileCurrent.setKey(Build.SERIAL);
        posicaoMobileCurrent.setLatitude(location.getLatitude());
        posicaoMobileCurrent.setLongitude(location.getLongitude());

        DispositivoMobile dispositivoMobileFollow = new DispositivoMobile();
        dispositivoMobileFollow.setId(vinculoDispositivoPosicao.getDispositivoMobileFollow().getId());

        VinculoDispositivoPosicao vinculoDispositivoPosicaoAux = new VinculoDispositivoPosicao();
        vinculoDispositivoPosicaoAux.setKey(Build.SERIAL);
        vinculoDispositivoPosicaoAux.setAlcanceMetros(15);
        vinculoDispositivoPosicaoAux.setForaAlcanse(false);
        vinculoDispositivoPosicaoAux.setDispositivoMobileCurrent(dispositivoMobileCurrent);
        vinculoDispositivoPosicaoAux.setPosicaoMobileCurrent(posicaoMobileCurrent);
        vinculoDispositivoPosicaoAux.setDispositivoMobileFollow(dispositivoMobileFollow);

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(Constants.REST_IP)
                .build();

        LocalizameRest localizameRest = retrofit.create(LocalizameRest.class);
        Observable<VinculoDispositivoPosicao> vinculoRest = localizameRest.updateVinculo(vinculoDispositivoPosicaoAux);

        vinculoRest.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VinculoDispositivoPosicao>() {
                    @Override
                    public void onCompleted() {
                        Log.w(TAG, "vinculoRest - onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(TAG, "vinculoRest - onError: " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final VinculoDispositivoPosicao vinculoDispositivoPosicaoAux) {
                        Log.w(TAG, "vinculoRest - onNext");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.w(TAG, "vinculoRest - onNext - " + vinculoDispositivoPosicaoAux.getDispositivoMobileCurrent().getId());
                                Log.w(TAG, "vinculoRest - onNext - " + vinculoDispositivoPosicaoAux.getDispositivoMobileFollow().getId());

                                LatLng latLng = new LatLng(vinculoDispositivoPosicaoAux.getPosicaoMobileCurrent().getLatitude(), vinculoDispositivoPosicaoAux.getPosicaoMobileCurrent().getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                                markerOptions.title("Alvo");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                mMap.addMarker(markerOptions);
                            }
                        });

                    }
                });
   }

    private void firstSend(final Location location){
        firstSend = true;
        DispositivoMobile dispositivoMobileCurrent = new DispositivoMobile();
        dispositivoMobileCurrent.setId(Build.SERIAL);
        dispositivoMobileCurrent.setNome("current");

        PosicaoMobile posicaoMobileCurrent = new PosicaoMobile();
        posicaoMobileCurrent.setKey(Build.SERIAL);
        posicaoMobileCurrent.setLatitude(location.getLatitude());
        posicaoMobileCurrent.setLongitude(location.getLongitude());

        DispositivoMobile dispositivoMobileFollow = new DispositivoMobile();
        dispositivoMobileFollow.setId(vinculoDispositivoPosicao.getDispositivoMobileFollow().getId());
        dispositivoMobileFollow.setNome("follow");

        PosicaoMobile posicaoMobileFollow = new PosicaoMobile();
        posicaoMobileFollow.setKey(Build.SERIAL);
        posicaoMobileFollow.setLatitude(location.getLatitude());
        posicaoMobileFollow.setLongitude(location.getLongitude());

        VinculoDispositivoPosicao vinculoDispositivoPosicaoAux = new VinculoDispositivoPosicao();
        vinculoDispositivoPosicaoAux.setKey(Build.SERIAL);
        vinculoDispositivoPosicaoAux.setAlcanceMetros(15);
        vinculoDispositivoPosicaoAux.setForaAlcanse(false);
        vinculoDispositivoPosicaoAux.setDispositivoMobileCurrent(dispositivoMobileCurrent);
        vinculoDispositivoPosicaoAux.setPosicaoMobileCurrent(posicaoMobileCurrent);
        vinculoDispositivoPosicaoAux.setDispositivoMobileFollow(dispositivoMobileFollow);
        vinculoDispositivoPosicaoAux.setPosicaoMobileFollow(posicaoMobileFollow);

        VinculoDispositivoPosicaoDAO dao = new VinculoDispositivoPosicaoDAOImpl();
        dao.save(vinculoDispositivoPosicaoAux);

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(Constants.REST_IP)
                .build();

        LocalizameRest localizameRest = retrofit.create(LocalizameRest.class);
        Observable<VinculoDispositivoPosicao> vinculoRest = localizameRest.setVinculo(vinculoDispositivoPosicaoAux);

        vinculoRest.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VinculoDispositivoPosicao>() {
                    @Override
                    public void onCompleted() {
                        Log.e("complete","");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error", e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(VinculoDispositivoPosicao vinculoDispositivoPosicao) {
                        Log.e("resume", vinculoDispositivoPosicao.toString());
                    }
                });

    }

}