package mjr.br.localizame.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PosicaoMobile extends RealmObject implements Serializable {
	private static final long serialVersionUID = 1L;

    @PrimaryKey
    private String key;
    private Date dataGPS;
	private Double latitude;
	private Double longitude;

	public PosicaoMobile() { }

    public PosicaoMobile(String key, Date dataGPS, Double latitude, Double longitude) {
        this.key = key;
        this.dataGPS = dataGPS;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDataGPS() {
        return dataGPS;
    }

    public void setDataGPS(Date dataGPS) {
        this.dataGPS = dataGPS;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}


