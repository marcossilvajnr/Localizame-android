package mjr.br.localizame.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcos on 7/5/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class VinculoDispositivoPosicao extends RealmObject implements Serializable {
    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private String key;
    private DispositivoMobile dispositivoMobileCurrent;
    private PosicaoMobile posicaoMobileCurrent;
    private DispositivoMobile dispositivoMobileFollow;
    private PosicaoMobile posicaoMobileFollow;
    private Integer alcanceMetros;
    private Boolean foraAlcanse;

    public VinculoDispositivoPosicao() {}

    public VinculoDispositivoPosicao(String key, DispositivoMobile dispositivoMobileCurrent, PosicaoMobile posicaoMobileCurrent, DispositivoMobile dispositivoMobileFollow, PosicaoMobile posicaoMobileFollow, Integer alcanceMetros, Boolean foraAlcanse) {
        this.key = key;
        this.dispositivoMobileCurrent = dispositivoMobileCurrent;
        this.posicaoMobileCurrent = posicaoMobileCurrent;
        this.dispositivoMobileFollow = dispositivoMobileFollow;
        this.posicaoMobileFollow = posicaoMobileFollow;
        this.alcanceMetros = alcanceMetros;
        this.foraAlcanse = foraAlcanse;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DispositivoMobile getDispositivoMobileCurrent() {
        return dispositivoMobileCurrent;
    }

    public void setDispositivoMobileCurrent(DispositivoMobile dispositivoMobileCurrent) {
        this.dispositivoMobileCurrent = dispositivoMobileCurrent;
    }

    public PosicaoMobile getPosicaoMobileCurrent() {
        return posicaoMobileCurrent;
    }

    public void setPosicaoMobileCurrent(PosicaoMobile posicaoMobileCurrent) {
        this.posicaoMobileCurrent = posicaoMobileCurrent;
    }

    public DispositivoMobile getDispositivoMobileFollow() {
        return dispositivoMobileFollow;
    }

    public void setDispositivoMobileFollow(DispositivoMobile dispositivoMobileFollow) {
        this.dispositivoMobileFollow = dispositivoMobileFollow;
    }

    public PosicaoMobile getPosicaoMobileFollow() {
        return posicaoMobileFollow;
    }

    public void setPosicaoMobileFollow(PosicaoMobile posicaoMobileFollow) {
        this.posicaoMobileFollow = posicaoMobileFollow;
    }

    public Integer getAlcanceMetros() {
        return alcanceMetros;
    }

    public void setAlcanceMetros(Integer alcanceMetros) {
        this.alcanceMetros = alcanceMetros;
    }

    public Boolean getForaAlcanse() {
        return foraAlcanse;
    }

    public void setForaAlcanse(Boolean foraAlcanse) {
        this.foraAlcanse = foraAlcanse;
    }

}
