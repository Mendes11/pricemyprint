package br.com.cozinheirodelivery.pricemyprint.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades.V2;
import br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades.V5;
import br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades.V7;
import br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades.V8;
import br.com.cozinheirodelivery.pricemyprint.Objects.Calculator;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.DefaultConfigurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

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
                    DB_MATERIAL.COLUMN_DIAMETRO+ " DECIMAL(10,2)"+COMMA_SEP+
                    DB_MATERIAL.COLUMN_IDPROJETO+" INTEGER"+COMMA_SEP+
                    "FOREIGN KEY("+DB_MATERIAL.COLUMN_IDPROJETO+") REFERENCES "+
                    DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE"+
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
                    DB_CONFIGURATIONS.COLUMN_NOME_CONFIGURACAO+ TEXT_TYPE+COMMA_SEP+
                    DB_CONFIGURATIONS.COLUMN_LUCRO+" INTEGER"+COMMA_SEP+
                    DB_CONFIGURATIONS.COLUMN_IDPROJETO+ " INTEGER"+COMMA_SEP+
                    "FOREIGN KEY("+DB_CONFIGURATIONS.COLUMN_IDPROJETO+") REFERENCES "+
                    DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE"+
                    ");";

    private static final String CREATE_PROJECT_TABLE =
            "CREATE TABLE " + DB_PROJECT.Estrutura_Projeto.TABLE_NAME + " (" +
                    DB_PROJECT.Estrutura_Projeto.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DB_PROJECT.Estrutura_Projeto.COLUMN_NOME + TEXT_TYPE +COMMA_SEP+
                    DB_PROJECT.Estrutura_Projeto.COLUMN_IMAGE_PATH + TEXT_TYPE+
            " );";

    private static final String CREATE_PIECE_TABLE =
            "CREATE TABLE " + DB_PIECE.Estrutura_Peca.TABLE_NAME + " (" +
                    DB_PIECE.Estrutura_Peca.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DB_PIECE.Estrutura_Peca.COLUMN_NOME + TEXT_TYPE + COMMA_SEP+
                    DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO + " INTEGER" + COMMA_SEP +
                    DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS+ " DECIMAL(10,2)"+ COMMA_SEP+
                    DB_PIECE.Estrutura_Peca.COLUMN_QUANT+" INTEGER" + COMMA_SEP+
                    DB_PIECE.Estrutura_Peca.COLUMN_TEMPO + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_PIECE.Estrutura_Peca.COLUMN_PRECO + " DECIMAL(10,2)"+COMMA_SEP +
                    DB_PIECE.Estrutura_Peca.COLUMN_SELECTED_INFO+ " INTEGER"+COMMA_SEP+
                    DB_PIECE.Estrutura_Peca.COLUMN_PATH+TEXT_TYPE+COMMA_SEP+
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



    private static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "pricemyprint.db";

    public DB(Context c){
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        this.c = c;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROJECT_TABLE);
        db.execSQL(CREATE_MATERIAL_TABLE);
        db.execSQL(CREATE_CONFIGURATION_TABLE);
        db.execSQL(CREATE_PIECE_TABLE);
        //db.execSQL(CREATE_PIECE_FOREIGN_PROJETO);
        //db.execSQL(CREATE_PIECE_FOREIGN_MATERIAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2){
            V2 v2 = new V2(db);
            v2.updateV1toV2();
        }
        if(oldVersion < 3){
            String sql = "alter table "+DB_PIECE.Estrutura_Peca.TABLE_NAME+" add "+DB_PIECE.Estrutura_Peca.COLUMN_QUANT+" INTEGER default 1;";
            db.execSQL(sql);
        }
        if(oldVersion < 4){
            db.execSQL("Update "+DB_PIECE.Estrutura_Peca.TABLE_NAME+" set "+DB_PIECE.Estrutura_Peca.COLUMN_TEMPO+
                    " = "+DB_PIECE.Estrutura_Peca.COLUMN_TEMPO+"*"+DB_PIECE.Estrutura_Peca.COLUMN_QUANT+", "+
                    DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO+"="+DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO+"*"+
                    DB_PIECE.Estrutura_Peca.COLUMN_QUANT+";");
        }
        if(oldVersion < 5){
            // Atualização de Peça para aceitar gramas como entrada.
            db.execSQL("alter table "+DB_PIECE.Estrutura_Peca.TABLE_NAME+" add "+DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS+" DECIMAL(10,2) default 0.0;");
            // Atualização do Config para aceitar lucro final.
            db.execSQL("alter table "+DB_CONFIGURATIONS.TABLE_NAME+" add "+DB_CONFIGURATIONS.COLUMN_LUCRO+" INTEGER default 0;");
            new V5(db).UpdateDatabase();
            //db.execSQL("update "+DB_PIECE.Estrutura_Peca.TABLE_NAME+" set "+DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS+" = "+);
        }
        if(oldVersion < 6){
            //Atualização do Projeto para ter um ImagePath.
            db.execSQL("alter table "+DB_PROJECT.Estrutura_Projeto.TABLE_NAME+ " add "+DB_PROJECT.Estrutura_Projeto.COLUMN_IMAGE_PATH + TEXT_TYPE+";");
            db.execSQL("alter table "+DB_PIECE.Estrutura_Peca.TABLE_NAME+ " add "+DB_PIECE.Estrutura_Peca.COLUMN_PATH + TEXT_TYPE+";");
            db.execSQL("alter table "+DB_PIECE.Estrutura_Peca.TABLE_NAME+ " add "+DB_PIECE.Estrutura_Peca.COLUMN_SELECTED_INFO+" INTEGER default 0;");
            Log.d("DB_Update","Updated to Version 6");
        }if(oldVersion < 7){
            Log.d("DB_Update","Updating to Version 7");
            new V7(c,db).updateDatabase();
            Log.d("DB_Update","Updated to Version 7");
        }if(oldVersion < 8){
             Log.d("DB_Update","Updating to Version 8");
            V8 v8 = new V8(c,db);
            v8.updateDatabase();
            Log.d("DB_Update","Updated to Version 8");
        }
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //onUpgrade(db, oldVersion, newVersion);
    }
    public Configurations checkConfigs(){
        //Irá verificar se há configurações já cadastradas, senão, irá preencher com valores da DefaultConfigurations.
        Configurations obj;
        List<Configurations> list = getConfigurations(null,null,null,null);
        if(list == null){
            //@TODO: Alterar para pegar pelo Strings? Assim altera-se o locale.
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
            obj.setiLucro(DefaultConfigurations.getiLucro());
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
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_PATH,obj.getcImagePath());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_TEMPO, obj.getfTempo());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO, obj.getiFilamento());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_PRECO, obj.getfPreco());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS,obj.getfGramas());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_SELECTED_INFO,obj.getbSelectedInfo());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_QUANT,obj.getiQuant());
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
                throw new Exception(c.getString(R.string.default_db_error));
            }
            obj.setiIDPiece(newRowId);
        }else{
           int ret = db.update(DB_PIECE.Estrutura_Peca.TABLE_NAME,values,DB_PIECE.Estrutura_Peca.COLUMN_ID+" = ?",new String[]{String.valueOf(obj.getiIDPiece())});
            if (ret == 0){
                throw new Exception(c.getString(R.string.default_db_error));
            }
        }
        db.close();
        return obj;
    }
    public Project insertProject(Project obj) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_PROJECT.Estrutura_Projeto.COLUMN_NOME, obj.getcNome());
        values.put(DB_PROJECT.Estrutura_Projeto.COLUMN_IMAGE_PATH,obj.getcPicturePath());
        if(obj.getiIDProjeto() == 0) {
            long newRowId;
            newRowId = db.insertOrThrow(
                    DB_PROJECT.Estrutura_Projeto.TABLE_NAME,
                    null,
                    values);
            if(newRowId == -1){
                throw new Exception(c.getString(R.string.default_db_error));
            }
            obj.setiIDProjeto(newRowId);
        }else{
            int ret = db.update(DB_PROJECT.Estrutura_Projeto.TABLE_NAME,values,DB_PROJECT.Estrutura_Projeto.COLUMN_ID+" = ?",new String[]{String.valueOf(obj.getiIDProjeto())});
            if (ret == 0){
                throw new Exception(c.getString(R.string.default_db_error));
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
        values.put(DB_CONFIGURATIONS.COLUMN_LUCRO,obj.getiLucro());
        values.put(DB_CONFIGURATIONS.COLUMN_IDPROJETO,obj.getiIDProjeto());
        if(obj.getiIDConfiguracao() == 0){
            long newRowId;
            newRowId = db.insertOrThrow(
                    DB_CONFIGURATIONS.TABLE_NAME,
                    null,
                    values);
            if(newRowId == -1){
                throw new Exception(c.getString(R.string.default_db_error));
            }
            obj.setiIDConfiguracao(newRowId);
        }else{
            int ret = db.update(DB_CONFIGURATIONS.TABLE_NAME,
                    values,
                    DB_CONFIGURATIONS.COLUMN_ID+" = ?",new String[]{String.valueOf(obj.getiIDConfiguracao())});
            if (ret == 0){
                throw new Exception(c.getString(R.string.default_db_error));
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
        values.put(DB_MATERIAL.COLUMN_IDPROJETO,obj.getiIDProjeto());
        if(obj.getiIDTipoMaterial() == 0){
            long newRowId;
            newRowId = db.insertOrThrow(
                    DB_MATERIAL.TABLE_NAME,
                    null,
                    values
            );
            if(newRowId == -1){
                throw new Exception(c.getString(R.string.default_db_error));
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
                throw new Exception(c.getString(R.string.default_db_error));
            }
        }
        db.close();
        return obj;
    }


    public List<Project> getProjects(String where,String[] args,String sortCol,String sortArg) {
        SQLiteDatabase db = getReadableDatabase();
        String[] colNames = {
                DB_PROJECT.Estrutura_Projeto.COLUMN_ID,
                DB_PROJECT.Estrutura_Projeto.COLUMN_NOME,
                DB_PROJECT.Estrutura_Projeto.COLUMN_IMAGE_PATH
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
                obj.setcPicturePath(c.getString(c.getColumnIndexOrThrow(DB_PROJECT.Estrutura_Projeto.COLUMN_IMAGE_PATH)));
                obj.setChildList(this.getPieces(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK + " = ?", new String[]{String.valueOf(obj.getiIDProjeto())}, null, null));
                // Pega todos os configs existentes para o projeto e substitui nas peças os seus configs atuais (sim, não está otimizado)
                // TODO: 28/12/2016 Otimizar a parte da atribuição dos objetos aqui... 
                List<Configurations> configurationsList = getConfigurations(DB_CONFIGURATIONS.COLUMN_IDPROJETO+" = ?",new String[]{String.valueOf(obj.getiIDProjeto())},null,null);
                List<TiposMateriais> materialList = getTiposMateriais(DB_MATERIAL.COLUMN_IDPROJETO+" = ?",new String[]{String.valueOf(obj.getiIDProjeto())},null,null);
                if(obj.getChildList() != null){
                    for(Piece oPiece : obj.getChildList()){
                        if(configurationsList != null){
                            for(Configurations oConf : configurationsList){
                                if(oPiece.getoConfigurations().getiIDConfiguracao() == oConf.getiIDConfiguracao()){
                                    oPiece.setoConfigurations(oConf);
                                }
                            }
                        }
                        if(materialList != null){
                            for(TiposMateriais oMaterial : materialList){
                                if(oPiece.getiIDTipoMaterial().getiIDTipoMaterial() == oMaterial.getiIDTipoMaterial()){
                                    oPiece.setiIDTipoMaterial(oMaterial);
                                }
                            }
                        }
                    }
                }
                obj.setConfigurationsList(configurationsList);
                obj.setMateriaisList(materialList);
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
                DB_PIECE.Estrutura_Peca.COLUMN_PATH,
                DB_PIECE.Estrutura_Peca.COLUMN_TEMPO,
                DB_PIECE.Estrutura_Peca.COLUMN_PRECO,
                DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO,
                DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS,
                DB_PIECE.Estrutura_Peca.COLUMN_SELECTED_INFO,
                DB_PIECE.Estrutura_Peca.COLUMN_QUANT,
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
            HashMap<Long,TiposMateriais> materialMap = new HashMap<>();
            HashMap<Long,Configurations> configMap = new HashMap<>();
            do {
                Piece obj = new Piece();
                obj.setiIDPiece(c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_ID)));
                obj.setcNome(c.getString(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_NOME)));
                obj.setcImagePath(c.getString(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PATH)));
                long iIDTipoMaterial = c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK));
                if(materialMap.containsKey(iIDTipoMaterial)){
                    obj.setiIDTipoMaterial(materialMap.get(iIDTipoMaterial));
                }else{
                    List<TiposMateriais> tiposMateriaisList = getTiposMateriais(DB_MATERIAL.COLUMN_ID+" = ?",new String[]{String.valueOf(iIDTipoMaterial)},null,null);
                    if(tiposMateriaisList !=null) {
                        if (tiposMateriaisList.size() == 1) {
                            obj.setiIDTipoMaterial(tiposMateriaisList.get(0));
                            materialMap.put(iIDTipoMaterial,tiposMateriaisList.get(0));
                        } else {
                            obj.setiIDTipoMaterial(null);
                        }
                    }
                }
                long iIDConfiguracao =c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK));
                if(configMap.containsKey(iIDConfiguracao)){
                    obj.setoConfigurations(configMap.get(iIDConfiguracao));
                }else {
                    List<Configurations> listConfiguracao = getConfigurations(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK + " = ?", new String[]{String.valueOf(iIDConfiguracao)}, null, null);
                    if (listConfiguracao != null) {
                        obj.setoConfigurations(listConfiguracao.get(0));
                        configMap.put(iIDConfiguracao,listConfiguracao.get(0));
                    }
                }
                obj.setfPreco(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PRECO)));
                obj.setfTempo(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_TEMPO)));
                obj.setiFilamento(c.getInt(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO)));
                obj.setiQuant(c.getInt(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_QUANT)));
                obj.setfGramas(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS)));
                obj.setbSelectedInfo(c.getInt(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_SELECTED_INFO)));
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
                DB_CONFIGURATIONS.COLUMN_NOME_CONFIGURACAO,
                DB_CONFIGURATIONS.COLUMN_LUCRO,
                DB_CONFIGURATIONS.COLUMN_IDPROJETO
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
                obj.setiLucro(c.getInt(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_LUCRO)));
                obj.setiIDProjeto(c.getLong(c.getColumnIndexOrThrow(DB_CONFIGURATIONS.COLUMN_IDPROJETO)));
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
                DB_MATERIAL.COLUMN_DIAMETRO,
                DB_MATERIAL.COLUMN_IDPROJETO
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
                obj.setiIDProjeto(c.getLong(c.getColumnIndexOrThrow(DB_MATERIAL.COLUMN_IDPROJETO)));
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


