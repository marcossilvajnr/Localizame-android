package mjr.br.localizame.dao.impl;

import io.realm.Realm;
import io.realm.RealmResults;
import mjr.br.localizame.dao.interfaces.VinculoDispositivoPosicaoDAO;
import mjr.br.localizame.model.VinculoDispositivoPosicao;

/**
 * Created by marcos on 7/7/16.
 */

public class VinculoDispositivoPosicaoDAOImpl implements VinculoDispositivoPosicaoDAO {

    @Override
    public void save(VinculoDispositivoPosicao vinculoDispositivoPosicao) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(vinculoDispositivoPosicao);
        realm.commitTransaction();
    }

    @Override
    public void remove(VinculoDispositivoPosicao vinculoDispositivoPosicao) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        vinculoDispositivoPosicao.removeFromRealm();
        realm.commitTransaction();
    }

    @Override
    public VinculoDispositivoPosicao get(String chave) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<VinculoDispositivoPosicao> vinculoDispositivoPosicaoResults = realm.where(VinculoDispositivoPosicao.class).findAll();
        return vinculoDispositivoPosicaoResults.isEmpty() ? null : vinculoDispositivoPosicaoResults.first();
    }

}
