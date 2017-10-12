package br.com.cozinheirodelivery.pricemyprint.Database;

import android.provider.BaseColumns;

/**
 * Created by Mendes on 23/08/2016.
 */
public final class DB_PIECE {

    public DB_PIECE(){}

    public static abstract class Estrutura_Peca implements BaseColumns {
        public static final String TABLE_NAME = "Peca";
        public static final String COLUMN_ID = "iIDPeca";
        public static final String COLUMN_NOME = "cNome";
        public static final String COLUMN_TEMPO = "fTempo";
        public static final String COLUMN_FILAMENTO = "iFilamento";
        public static final String COLUMN_GRAMAS = "fGramas";
        public static final String COLUMN_SELECTED_INFO = "bSelectedInfo";
        public static final String COLUMN_PRECO = "fPreco";
        public static final String COLUMN_QUANT = "iQnt";
        public static final String COLUMN_PATH = "cImagePath";
        public static final String COLUMN_PROJETO_FK = "iIDProjeto";
        public static final String COLUMN_MATERIAL_FK = "iIDTipoMaterial";
        public static final String COLUMN_CONFIGURATION_FK = "iIDConfiguracao";
    }
}
