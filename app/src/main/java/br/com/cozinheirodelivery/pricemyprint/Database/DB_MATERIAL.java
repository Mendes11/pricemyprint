package br.com.cozinheirodelivery.pricemyprint.Database;

import android.provider.BaseColumns;

/**
 * Created by Mendes on 23/08/2016.
 */
public class DB_MATERIAL implements BaseColumns {
    public DB_MATERIAL(){}

    public static final String TABLE_NAME = "TiposMateriais";
    public static final String COLUMN_ID = "iIDTipoMaterial";
    public static final String COLUMN_DENSIDADE = "fDensidade"; //g/cm^3
    public static final String COLUMN_PRECO_KG = "fPreco";
    public static final String COLUMN_NOME = "cNome";
    public static final String COLUMN_DIAMETRO = "fDiametro";
    public static final String COLUMN_IDPROJETO = "iIDProjeto";
}
