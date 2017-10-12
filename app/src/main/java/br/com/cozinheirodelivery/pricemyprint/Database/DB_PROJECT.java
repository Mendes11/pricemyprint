package br.com.cozinheirodelivery.pricemyprint.Database;

import android.provider.BaseColumns;

/**
 * Created by Mendes on 23/08/2016.
 */
public class DB_PROJECT {
    public  DB_PROJECT(){}
    public static abstract class Estrutura_Projeto implements BaseColumns {
        public static final String TABLE_NAME = "Projeto";
        public static final String COLUMN_ID = "iIDProjeto";
        public static final String COLUMN_NOME = "cNome";
        public static final String COLUMN_IMAGE_PATH = "cImagePath";
    }
}
