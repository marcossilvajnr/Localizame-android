package mjr.br.localizame.network;

import mjr.br.localizame.model.VinculoDispositivoPosicao;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import rx.Observable;

/**
 * Created by marcos on 7/6/16.
 */

public interface LocalizameRest {

    @GET("vinculodispositivo/")
    Observable<VinculoDispositivoPosicao> getVinculo(@Header("key") String key);

    @PUT("vinculodispositivo/")
    Observable<VinculoDispositivoPosicao> setVinculo(@Body VinculoDispositivoPosicao vinculoDispositivoPosicao);

    @POST("vinculodispositivo/")
    Observable<VinculoDispositivoPosicao> updateVinculo(@Body VinculoDispositivoPosicao vinculoDispositivoPosicao);

    @DELETE("vinculodispositivo/")
    Observable<Response<Void>> removeVinculo(@Header("key") String key);

}
