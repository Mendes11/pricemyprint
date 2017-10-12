package pricemypiece.Database;

import android.provider.BaseColumns;

/**
 * Created by Mendes on 23/08/2016.
 */
public class DB_CONFIGURATIONS implements BaseColumns {
    public DB_CONFIGURATIONS(){

    }
    public static final String TABLE_NAME = "Configuracoes";
    public static final String COLUMN_ID = "iIDConfiguracao";
    public static final String COLUMN_TARIFA = "fTarifa";
    public static final String COLUMN_MEDIA_USO = "fMediaUso"; // DADO EM HORAS/DIA
    public static final String COLUMN_TEMPO_VIDA = "fTempoVida"; // DADO EM ANOS.
    public static final String COLUMN_REPARO = "fReparo"; // DADO EM PORCENTAGEM DO PRECO TOTAL DA IMPRESSORA EM FUNCAO DO SEU TEMPO DE VIDA.
    public static final String COLUMN_TEMPO_TRABALHO = "fHoraTrabalho";
    public static final String COLUMN_TAXA_FALHAS = "fTaxaFalhas"; //Porcentagem do valor total adicionado, destinado a falhas.
    public static final String COLUMN_PRECO_IMPRESSORA = "fPrecoImpressora";
    public static final String COLUMN_CONSUMO_IMPRESSORA = "iConsumo";
    public static final String COLUMN_NOME_CONFIGURACAO = "cNomeConfig"; // Nome dado à configuração em questão.
}
