package pricemypiece.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheiro.pricemypiece.Database.DatabaseUpgrades.V2;
import br.com.cozinheiro.pricemypiece.Objects.Configurations;
import br.com.cozinheiro.pricemypiece.Objects.DefaultConfigurations;
import br.com.cozinheiro.pricemypiece.Objects.Piece;
import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 23/08/2016.
 */
public class DB extends SQLiteOpenHelper {
    Context c;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String CREATE_MATERIAL_TABLE =
            "CREATE TABLE " +DB_MATERIAL.TABLE_NAME + " ("+
                    DB_MATERIAL.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DB_MATERIAL.COLUMN_NOME + TEXT_TYPE+COMMA_SEP+
                    DB_MATERIAL.COLUMN_DENSIDADE + " DECIMAL(10,2)"+COMMA_SEP+
                    DB_MATERIAL.COLUMN_PRECO_KG + " DECIMAL(10,2)"+COMMA_SEP+
                    DB_MATERIAL.COLUMN_DIAMETRO+ " DECIMAL(10,2)"+
                    ");";

    private static final String CREATE_CONFIGURATION_TABLE =
            "CREATE TABLE " +DB_CONFIGURATIONS.TABLE_NAME + " ("+
                    DB_CONFIGURATIONS.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DB_CONFIGURATIONS.COLUMN_TARIFA + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_CONFIGURATIONS.COLUMN_MEDIA_USO + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_CONFIGURATIONS.COLUMN_REPARO + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_CONFIGURATIONS.COLUMN_TEMPO_VIDA + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_CONFIGURATIONS.COLUMN_TAXA_FALHAS + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_CONFIGURATIONS.COLUMN_TEMPO_TRABALHO +" DECIMAL(10,2)"+COMMA_SEP+
                    DB_CONFIGURATIONS.COLUMN_PRECO_IMPRESSORA+ " DECIMAL(10,2)"+COMMA_SEP+
                    DB_CONFIGURATIONS.COLUMN_CONSUMO_IMPRESSORA+ " INTEGER"+COMMA_SEP+
                    DB_CONFIGURATIONS.COLUMN_NOME_CONFIGURACAO+ TEXT_TYPE+
                    ");";

    private static final String CREATE_PROJECT_TABLE =
            "CREATE TABLE " + DB_PROJECT.Estrutura_Projeto.TABLE_NAME + " (" +
                    DB_PROJECT.Estrutura_Projeto.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DB_PROJECT.Estrutura_Projeto.COLUMN_NOME + TEXT_TYPE +
            " );";

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
    /*
    private static final String CREATE_PIECE_FOREIGN_PROJETO =
            "ALTER TABLE "+DB_PIECE.Estrutura_Peca.TABLE_NAME + " ADD FOREIGN KEY("+DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK+") REFERENCES "+
                    DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+");";
    private static final String CREATE_PIECE_FOREIGN_MATERIAL =
            "ALTER TABLE "+DB_PIECE.Estrutura_Peca.TABLE_NAME + " ADD FOREIGN KEY("+DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK+") REFERENCES "+
                    DB_MATERIAL.TABLE_NAME+"("+DB_MATERIAL.COLUMN_ID+");";

*/
    private static final String DROP_TABLE_PIECE = "DROP TABLE IF EXISTS "+DB_PIECE.Estrutura_Peca.TABLE_NAME;
    private static final String DROP_TABLE_PROJETO = "DROP TABLE IF EXISTS "+DB_PROJECT.Estrutura_Projeto.TABLE_NAME;
    private static final String DROP_TABLE_MATERIAL = "DROP TABLE IF EXISTS "+DB_MATERIAL.TABLE_NAME;
    private static final String DROP_TABLE_CONFIGURATION = "DROP TABLE IF EXISTS "+DB_CONFIGURATIONS.TABLE_NAME;



    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "PriceMyPiece.db";

    public DB(Context c){
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        this.c = c;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MATERIAL_TABLE);
        db.execSQL(CREATE_CONFIGURATION_TABLE);
        db.execSQL(CREATE_PROJECT_TABLE);
        db.execSQL(CREATE_PIECE_TABLE);
        //db.execSQL(CREATE_PIECE_FOREIGN_PROJETO);
        //db.execSQL(CREATE_PIECE_FOREIGN_MATERIAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == 2){
            V2 v2 = new V2(db);
            v2.updateV1toV2();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }

    public Configurations checkConfigs(){
        //Irá verificar se há configurações já cadastradas, senão, irá preencher com valores da DefaultConfigurations.
        Configurations obj;
        List<Configurations> list = getConfigurations(null,null,null,null);
        if(list == null){
            obj = new Configurations();
            obj.setfTempoVida(DefaultConfigurations.getfTempoVida());
            obj.setfMediaUso(DefaultConfigurations.getfMediaUso());
            obj.setfTaxaFalhas(DefaultConfigurations.getfTaxaFalhas());
            obj.setfTarifa(DefaultConfigurations.getfTarifa());
            obj.setfHoraTrabalho(DefaultConfigurations.getfHoraTrabalho());
            obj.setfReparo(DefaultConfigurations.getfReparo());
            obj.setfPrecoImpressora(DefaultConfigurations.getfPrecoImpressora());
            obj.setiConsumo(DefaultConfigurations.getiConsumo());
            obj.setcNomeConfig(DefaultConfigurations.getcNome());
            try {
                obj = insertConfigurations(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            obj = list.get(0);
        }
        return obj;
    }
    public Piece insertPiece(Piece obj) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_NOME, obj.getcNome());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_TEMPO, obj.getfTempo());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO, obj.getiFilamento());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_PRECO, obj.getfPreco());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK, obj.getiIDProject());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK,obj.getiIDTipoMaterial().getiIDTipoMaterial());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK,obj.getoConfigurations().getiIDConfiguracao());
        if(obj.getiIDPiece() == 0) { // Valor padrão de inicialização do long é 0.
            long newRowId;
            newRowId = db.insert(
                    DB_PIECE.Estrutura_Peca.TABLE_NAME,
                    null,
                    values);
            if(newRowId == -1){
                throw new Exception("Não foi possível inserir nova Peça, verifique os dados inseridos.");
            }
            obj.setiIDPiece(newRowId);
        }else{
           int ret = db.update(DB_PIECE.Estrutura_Peca.TABLE_NAME,values,DB_PIECE.Estrutura_Peca.COLUMN_ID+" = ?",new String[]{String.valueOf(obj.getiIDPiece())});
            if (ret == 0){
                throw new Exception("Não foi Possível atualizar a Peça.");
            }
        }
        db.close();
        return obj;
    }
    public Project insertProject(Project obj) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_PROJECT.Estrutura_Projeto.COLUMN_NOME, obj.getcNome());
        if(obj.getiIDProjeto() == 0) {
            long newRowId;
            newRowId = db.insertOrThrow(
                    DB_PROJECT.Estrutura_Projeto.TABLE_NAME,
                    null,
                    values);
            if(newRowId == -1){
                throw new Exception("Não foi possível inserir novo Projeto, verifique os dados inseridos.");
            }
            obj.setiIDProjeto(newRowId);
        }else{
            int ret = db.update(DB_PROJECT.Estrutura_Projeto.TABLE_NAME,values,DB_PROJECT.Estrutura_Projeto.COLUMN_ID+" = ?",new String[]{String.valueOf(obj.getiIDProjeto())});
            if (ret == 0){
                throw new Exception("Não foi Possível atualizar o Projeto.");
            }
        }
        db.close();
        return obj;
    }
    public Configurations insertConfigurations(Configurations obj) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_CONFIGURATIONS.COLUMN_MEDIA_USO,obj.getfMediaUso());
        values.put(DB_CONFIGURATIONS.COLUMN_REPARO,obj.getfReparo());
        values.put(DB_CONFIGURATIONS.COLUMN_TARIFA,obj.getfTarifa());
        values.put(DB_CONFIGURATIONS.COLUMN_TAXA_FALHAS,obj.getfTaxaFalhas());
        values.put(DB_CONFIGURATIONS.COLUMN_TEMPO_TRABALHO,obj.getfHoraTrabalho());
        values.put(DB_CONFIGURATIONS.COLUMN_TEMPO_VIDA,obj.getfTempoVida());
        values.put(DB_CONFIGURATIONS.COLUMN_PRECO_IMPRESSORA,obj.getfPrecoImpressora());
        values.put(DB_CONFIGURATIONS.COLUMN_CONSUMO_IMPRESSORA,obj.getiConsumo());
        values.put(DB_CONFIGURATIONS.COLUMN_NOME_CONFIGURACAO,obj.getcNomeConfig());
        if(obj.getiIDConfiguracao() == 0){
            long newRowId;
            newRowId = db.insertOrThrow(
                    DB_CONFIGURATIONS.TABLE_NAME,
                    null,
                    values);
            if(newRowId == -1){
                throw new Exception("Não foi possível inserir novas configurações. Verifique os dados");
            }
            obj.setiIDConfiguracao(newRowId);
        }else{
            int ret = db.update(DB_CONFIGURATIONS.TABLE_NAME,
                    values,
                    DB_CONFIGURATIONS.COLUMN_ID+" = ?",new String[]{String.valueOf(obj.getiIDConfiguracao())});
            if (ret == 0){
                throw new Exception("Não foi possível atualizar os dados.");
            }
        }
        db.close();
        return obj;
    }

    public TiposMateriais insertTipoMaterial(TiposMateriais obj) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_MATERIAL.COLUMN_NOME,obj.getcNome());
        values.put(DB_MATERIAL.COLUMN_DENSIDADE,obj.getfDensidade());
        values.put(DB_MATERIAL.COLUMN_PRECO_KG,obj.getfPreco());
        values.put(DB_MATERIAL.COLUMN_DIAMETRO,obj.getfDiametro());
        if(obj.getiIDTipoMaterial() == 0){
            long newRowId;
            newRowId = db.insertOrThrow(
                    DB_MATERIAL.TABLE_NAME,
                    null,
                    values
            );
            if(newRowId == -1){
                throw new Exception("Não foi possível inserir novo Material. Verifique os dados.");
            }
            obj.setiIDTipoMaterial(newRowId);
        }else{
            int ret = db.update(
                    DB_MATERIAL.TABLE_NAME,
                    values,
                    DB_MATERIAL.COLUMN_ID+" = ?",
                    new String[]{String.valueOf(obj.getiIDTipoMaterial())}
            );
            if(ret == 0){
                throw new Exception("Não foi possível atualizar os dados.");
            }
        }
        db.close();
        return obj;
    }

    public List<Project> getProjects(String where,String[] args,String sortCol,String sortArg) {
        SQLiteDatabase db = getReadableDatabase();
        String[] colNames = {
                DB_PROJECT.Estrutura_Projeto.COLUMN_ID,
                DB_PROJECT.Estrutura_Projeto.COLUMN_NOME
        };
        String sortOrder = null;
        if (sortCol != null){
            sortOrder = " order by " + sortCol + " " + sortArg;
        }
        Cursor c = db.query(DB_PROJECT.Estrutura_Projeto.TABLE_NAME,
                colNames,
                where,
                args,
                null,
                null,
                sortOrder);
        if(c.moveToFirst() && c.getCount()>0) {
            List<Project> lista = new ArrayList<>();
            do {
                Project obj = new Project();
                obj.setiIDProjeto(c.getLong(c.getColumnIndexOrThrow(DB_PROJECT.Estrutura_Projeto.COLUMN_ID)));
                obj.setcNome(c.getString(c.getColumnIndexOrThrow(DB_PROJECT.Estrutura_Projeto.COLUMN_NOME)));
                lista.add(obj);
            } while (c.moveToNext());
            db.close();
            return lista;
        }else{
            db.close();
            return null;
        }
    }
    public int deletePieces(Piece piece) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        int ret = db.delete(DB_PIECE.Estrutura_Peca.TABLE_NAME, DB_PIECE.Estrutura_Peca.COLUMN_ID + " = ?", new String[]{piece.getiIDPiece() + ""});
        if(ret == 0){
            db.close();
            throw new Exception(c.getString(R.string.peca_delete_error1));
        }else{
            db.close();
            return 1;
        }
    }
    public int deleteConfiguration(Configurations conf) throws Exception {
        List<Piece> list = getPieces(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK+" = ?",new String[]{conf.getiIDConfiguracao()+""},null,null);
        if(list == null) {
            SQLiteDatabase db = getWritableDatabase();
            int ret = db.delete(DB_CONFIGURATIONS.TABLE_NAME, DB_CONFIGURATIONS.COLUMN_ID + " = ?", new String[]{conf.getiIDConfiguracao() + ""});
            if (ret == 0) {
                db.close();
                throw new Exception(c.getString(R.string.configuration_delete_error));
            } else {
                db.close();
                return 1;
            }
        }else{
            throw new Exception(c.getString(R.string.configuration_delete_error));
        }
    }
    public int deleteProjectPieces(long iIDProject) throws Exception {
        if(getPieces(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK+" = ?",new String[]{iIDProject+""},null,null) != null) {
            SQLiteDatabase db = getWritableDatabase();
            int ret = db.delete(DB_PIECE.Estrutura_Peca.TABLE_NAME, DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK + " = ?", new String[]{iIDProject + ""});
            if (ret == 0) {
                db.close();
                throw new Exception(c.getString(R.string.peca_delete_error2));
            } else {
                db.close();
                return 1;
            }
        }else{
            return 1;
        }
    }
    public int deleteProject(Project project) throws Exception {
        deleteProjectPieces(project.getiIDProjeto());
        SQLiteDatabase db = getWritableDatabase();
        int ret = db.delete(DB_PROJECT.Estrutura_Projeto.TABLE_NAME, DB_PROJECT.Estrutura_Projeto.COLUMN_ID+" = ?", new String[]{project.getiIDProjeto() + ""});
        if(ret == 0){
            db.close();
            throw new Exception(c.getString(R.string.projeto_delete_error));
        }else{
            db.close();
            return 1;
        }
    }
    public int deleteMaterialSingle(TiposMateriais tipo) throws Exception {
        List<Piece> list = getPieces(DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK + " = ?", new String[]{tipo.getiIDTipoMaterial() + ""}, null, null);
        if(list != null){
            throw new Exception(c.getString(R.string.material_delete_error));
        }
        SQLiteDatabase db = getWritableDatabase();
        int ret = db.delete(DB_MATERIAL.TABLE_NAME, "iIDTipoMaterial = ?", new String[]{tipo.getiIDTipoMaterial() + ""});
        if(ret == 0){
            db.close();
            throw new Exception(c.getString(R.string.material_delete_error));
        }else{
            db.close();
            return 1;
        }
    }

    public List<Piece> getPieces(String where,String[] args,String sortCol,String sortArg){
        SQLiteDatabase db = getReadableDatabase();
        String[] colNames = {
                DB_PIECE.Estrutura_Peca.COLUMN_ID,
                DB_PIECE.Estrutura_Peca.COLUMN_NOME,
                DB_PIECE.Estrutura_Peca.COLUMN_TEMPO,
                DB_PIECE.Estrutura_Peca.COLUMN_PRECO,
                DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO,
                DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK,
                DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK,
                DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK
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
                List<Configurations> listConfiguracao = getConfigurations(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK+ " = ?",new String[]{String.valueOf(c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK)))},null,null);
                if(listConfiguracao != null) {
                    obj.setoConfigurations(listConfiguracao.get(0));
                }
                obj.setfPreco(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PRECO)));
                obj.setfTempo(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_TEMPO)));
                obj.setiFilamento(c.getInt(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO)));
                obj.setiIDProject(c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK)));
                lista.add(obj);
            } while (c.moveToNext());
            db.close();
            return lista;
        }else{
            db.close();
            return null;
        }
    }
    public List<Configurations> getConfigurations(String where,String[] args,String sortCol,String sortArg){
        SQLiteDatabase db = getReadableDatabase();
        String[] colNames = {
                DB_CONFIGURATIONS.COLUMN_ID,
                DB_CONFIGURATIONS.COLUMN_TEMPO_TRABALHO,
                DB_CONFIGURATIONS.COLUMN_TAXA_FALHAS,
                DB_CONFIGURATIONS.COLUMN_TEMPO_VIDA,
                DB_CONFIGURATIONS.COLUMN_REPARO,
                DB_CONFIGURATIONS.COLUMN_MEDIA_USO,
                DB_CONFIGURATIONS.COLUMN_TARIFA,
                DB_CONFIGURATIONS.COLUMN_PRECO_IMPRESSORA,
                DB_CONFIGURATIONS.COLUMN_CONSUMO_IMPRESSORA,
                DB_CONFIGURATIONS.COLUMN_NOME_CONFIGURACAO
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
            List<Configurations> list = new ArrayList<>();
            do {
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
                obj.setcNomeConfig(c.getString(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_NOME_CONFIGURACAO)));
                list.add(obj);
            }while(c.moveToNext());
            db.close();
                return list;
        }else{
            db.close();
            return null;
        }
    }

    public List<TiposMateriais> getTiposMateriais(String where,String[] args,String sortCol,String sortArg){
        SQLiteDatabase db = getReadableDatabase();
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
            db.close();
            return lista;
        }else{
            db.close();
            return null;
        }
    }
}


