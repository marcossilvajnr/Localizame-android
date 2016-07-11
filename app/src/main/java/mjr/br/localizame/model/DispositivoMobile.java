package mjr.br.localizame.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by marcos on 7/5/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DispositivoMobile extends RealmObject implements Serializable {
    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private String id;
    private String nome;

    public DispositivoMobile() { }

    public DispositivoMobile(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
