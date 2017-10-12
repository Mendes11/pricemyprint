package br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB_CONFIGURATIONS;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_MATERIAL;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_PIECE;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_PROJECT;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.DefaultConfigurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;

/**
 * Created by Mendes on 23/09/2016.
 */
public class V2 {
    SQLiteDatabase db;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String CREATE_PIECE_TABLE =
            "CREATE TABLE " + DB_PIECE.Estrutura_Peca.TABLE_NAME + " (" +
                    DB_PIECE.Estrutura_Peca.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DB_PIECE.Estrutura_Peca.COLUMN_NOME + TEXT_TYPE + COMMA_SEP+
                    DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO + " INTEGER" + COMMA_SEP +
                    DB_PIECE.Estrutura_Peca.COLUMN_TEMPO + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_PIECE.Estrutura_Peca.COLUMN_PRECO + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK + " INTEGER NOT NULL" +COMMA_SEP +
                    DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK + " INTEGER NOT NULL"+COMMA_SEP+
                    DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK + " INTEGER NOT NULL"+COMMA_SEP+
                    "FOREIGN KEY("+DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK+") REFERENCES "+
                    DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE"+COMMA_SEP+
                    "FOREIGN KEY("+DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK+") REFERENCES "+
                    DB_CONFIGURATIONS.TABLE_NAME+"("+DB_CONFIGURATIONS.COLUMN_ID+")"+COMMA_SEP+
                    "FOREIGN KEY("+DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK+") REFERENCES "+
                    DB_MATERIAL.TABLE_NAME+"("+DB_MATERIAL.COLUMN_ID+"));";

    private static final String ALTER_TABLE_CONFIGURATION = "ALTER TABLE "+ DB_CONFIGURATIONS.TABLE_NAME+" ADD "+DB_CONFIGURATIONS.COLUMN_NOME_CONFIGURACAO + TEXT_TYPE+" default '"+DefaultConfigurations.getcNome()+"';";
    private static final String ALTER_TABLE_PIECE1 = "ALTER TABLE "+DB_PIECE.Estrutura_Peca.TABLE_NAME+" ADD "+DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK+" INTEGER;";

    private static final String ALTER_TABLE_PIECE2 = "ALTER TABLE "+DB_PIECE.Estrutura_Peca.TABLE_NAME+" ADD  FOREIGN KEY("+DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK+") REFERENCES " +
            DB_CONFIGURATIONS.TABLE_NAME+"("+DB_CONFIGURATIONS.COLUMN_ID+");";

    private static final String DROP_TABLE_PIECE = "DROP TABLE IF EXISTS "+DB_PIECE.Estrutura_Peca.TABLE_NAME;

    public V2(SQLiteDatabase db){
        this.db = db;
    }

    public void updateV1toV2(){
        db.execSQL(ALTER_TABLE_CONFIGURATION);
        //Procura todas as peças que possuem configuração e atribui 1 a elas
        List<Piece> list = getPieces(null, null, null, null);
        if(list != null) {
            //db.execSQL(CREATE_PIECE_TABLE_AUX);
            //Agora que ele possui toda a lista de peças armazenada, irá remover a tabela antiga e atualiza-a com o valor de config.
            Configurations conf = getConfigurations(null, null, null, null);
            db.execSQL(DROP_TABLE_PIECE); // Dropa a tabela antiga
            db.execSQL(CREATE_PIECE_TABLE); // Cria a nova tabela com as colunas atualizadas.
            for (Piece obj : list) {
                obj.setoConfigurations(conf); //Atualiza o ID do config.
                try {
                    // Neste caso ele irá povoar toda a tabela novamente, porém já com os IDs.
                    insertPiece(obj);
                }catch(Exception e){

                }
            }
        }
    }

    public Piece insertPiece(Piece obj) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_ID,obj.getiIDPiece());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_NOME, obj.getcNome());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_TEMPO, obj.getfTempo());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO, obj.getiFilamento());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_PRECO, obj.getfPreco());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK, obj.getiIDProject());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK, obj.getiIDTipoMaterial().getiIDTipoMaterial());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK, obj.getoConfigurations().getiIDConfiguracao());
            long newRowId;
            newRowId = db.insert(
                    DB_PIECE.Estrutura_Peca.TABLE_NAME,
                    null,
                    values);
            if(newRowId == -1){
                throw new Exception("Não foi possível inserir nova Peça, verifique os dados inseridos.");
            }
            //obj.setiIDPiece(newRowId);
        return obj;
    }

    public List<Piece> getPieces(String where,String[] args,String sortCol,String sortArg){
        String[] colNames = {
                DB_PIECE.Estrutura_Peca.COLUMN_ID,
                DB_PIECE.Estrutura_Peca.COLUMN_NOME,
                DB_PIECE.Estrutura_Peca.COLUMN_TEMPO,
                DB_PIECE.Estrutura_Peca.COLUMN_PRECO,
                DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO,
                DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK,
                DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK
        };
        String sortOrder = null;
        if (sortCol != null){
            sortOrder = " order by " + sortCol + " " + sortArg;
        }
        Cursor c = db.query(DB_PIECE.Estrutura_Peca.TABLE_NAME,
                colNames,
                where,
                args,
                null,
                null,
                sortOrder);
        if(c.moveToFirst() && c.getCount()>0) {
            List<Piece> lista = new ArrayList<>();
            do {
                Piece obj = new Piece();
                obj.setiIDPiece(c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_ID)));
                obj.setcNome(c.getString(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_NOME)));
                List<TiposMateriais> tiposMateriaisList = getTiposMateriais(DB_MATERIAL.COLUMN_ID+" = ?",new String[]{String.valueOf(c.getString(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK)))},null,null);
                if(tiposMateriaisList !=null) {
                    if (tiposMateriaisList.size() == 1) {
                        obj.setiIDTipoMaterial(tiposMateriaisList.get(0));
                    } else {
                        obj.setiIDTipoMaterial(null);
                    }
                }
                obj.setfPreco(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PRECO)));
                obj.setfTempo(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_TEMPO)));
                obj.setiFilamento(c.getInt(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO)));
                obj.setiIDProject(c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK)));
                lista.add(obj);
            } while (c.moveToNext());
            return lista;
        }else{
            return null;
        }
    }

    public List<TiposMateriais> getTiposMateriais(String where,String[] args,String sortCol,String sortArg){
        String[] colNames = {
                DB_MATERIAL.COLUMN_ID,
                DB_MATERIAL.COLUMN_NOME,
                DB_MATERIAL.COLUMN_PRECO_KG,
                DB_MATERIAL.COLUMN_DENSIDADE,
                DB_MATERIAL.COLUMN_DIAMETRO
        };
        String sortOrder = null;
        if (sortCol != null){
            sortOrder = " order by " + sortCol + " " + sortArg;
        }
        Cursor c = db.query(DB_MATERIAL.TABLE_NAME,
                colNames,
                where,
                args,
                null,
                null,
                sortOrder);
        if(c.moveToFirst()&& c.getCount()>0) {
            List<TiposMateriais> lista = new ArrayList<>();
            do {
                TiposMateriais obj = new TiposMateriais();
                obj.setiIDTipoMaterial(c.getLong(c.getColumnIndexOrThrow(DB_MATERIAL.COLUMN_ID)));
                obj.setcNome(c.getString(c.getColumnIndexOrThrow(DB_MATERIAL.COLUMN_NOME)));
                obj.setfPreco(c.getDouble(c.getColumnIndexOrThrow(DB_MATERIAL.COLUMN_PRECO_KG)));
                obj.setfDensidade(c.getDouble(c.getColumnIndexOrThrow(DB_MATERIAL.COLUMN_DENSIDADE)));
                obj.setfDiametro(c.getDouble(c.getColumnIndexOrThrow(DB_MATERIAL.COLUMN_DIAMETRO)));
                lista.add(obj);
            } while (c.moveToNext());
            return lista;
        }else{
            return null;
        }
    }

    public Configurations getConfigurations(String where,String[] args,String sortCol,String sortArg){
        String[] colNames = {
                DB_CONFIGURATIONS.COLUMN_ID,
                DB_CONFIGURATIONS.COLUMN_TEMPO_TRABALHO,
                DB_CONFIGURATIONS.COLUMN_TAXA_FALHAS,
                DB_CONFIGURATIONS.COLUMN_TEMPO_VIDA,
                DB_CONFIGURATIONS.COLUMN_REPARO,
                DB_CONFIGURATIONS.COLUMN_MEDIA_USO,
                DB_CONFIGURATIONS.COLUMN_TARIFA,
                DB_CONFIGURATIONS.COLUMN_PRECO_IMPRESSORA,
                DB_CONFIGURATIONS.COLUMN_CONSUMO_IMPRESSORA
        };
        String sortOrder = null;
        if (sortCol != null){
            sortOrder = " order by " + sortCol + " " + sortArg;
        }
        Cursor c = db.query(DB_CONFIGURATIONS.TABLE_NAME,
                colNames,
                where,
                args,
                null,
                null,
                sortOrder);
        if(c.moveToFirst() && c.getCount()>0) {
            Configurations obj = new Configurations();
            obj.setiIDConfiguracao(c.getLong(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_ID)));
            obj.setfHoraTrabalho(c.getDouble(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_TEMPO_TRABALHO)));
            obj.setfReparo(c.getDouble(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_REPARO)));
            obj.setfTarifa(c.getDouble(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_TARIFA)));
            obj.setfTaxaFalhas(c.getDouble(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_TAXA_FALHAS)));
            obj.setfMediaUso(c.getDouble(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_MEDIA_USO)));
            obj.setfTempoVida(c.getDouble(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_TEMPO_VIDA)));
            obj.setfPrecoImpressora(c.getDouble(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_PRECO_IMPRESSORA)));
            obj.setiConsumo(c.getInt(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_CONSUMO_IMPRESSORA)));
            return obj;
        }else{
            return null;
        }
    }
}
