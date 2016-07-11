package mjr.br.localizame.dao.interfaces;

import mjr.br.localizame.model.VinculoDispositivoPosicao;

/**
 * Created by marcos on 7/7/16.
 */

public interface VinculoDispositivoPosicaoDAO {
    void save(VinculoDispositivoPosicao vinculoDispositivoPosicao);
    void remove(VinculoDispositivoPosicao vinculoDispositivoPosicao);
    VinculoDispositivoPosicao get(String chave);
}
