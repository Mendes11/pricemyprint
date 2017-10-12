package br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB_CONFIGURATIONS;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_MATERIAL;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_PIECE;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_PROJECT;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.SharedPrefs;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 28/12/2016.
 */

public class V7 {
    SQLiteDatabase db;
    Context c;
    List<Configurations> oldConfigList;
    List<TiposMateriais> oldMaterialList;
    List<Project> projectList;
    Project firstProject;
    public V7(Context c, SQLiteDatabase db){
        this.db = db;
    }

    public void updateDatabase(){
        oldConfigList = getConfigurations(null,null,null,null);
        oldMaterialList = getTiposMateriais(null,null,null,null);
        // RealizaPrimeiro o Config
        updateConfig();
        updateMaterial();
    }
    private void updateConfig(){
        if(oldConfigList != null) { // Verifica se não há configurações pré-definidas, se houver, atualiza os projetos com elas.
            projectList = getProjects(null,null,null,null);
            if(projectList != null) {
                firstProject = projectList.get(0);
                db.execSQL("alter table " + DB_CONFIGURATIONS.TABLE_NAME + " add column " + DB_CONFIGURATIONS.COLUMN_IDPROJETO + " INTEGER default " +firstProject.getiIDProjeto()+
                                " REFERENCES "+DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE;");

                // Após atualizadas as configurações, implementa-se o foreign key.
                /*db.execSQL("ALTER TABLE "+DB_CONFIGURATIONS.TABLE_NAME+" ADD FOREIGN KEY("+DB_CONFIGURATIONS.COLUMN_IDPROJETO+") REFERENCES "+
                        DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE;");*/
                if(projectList.size() > 1){
                    projectList.remove(0);
                    // Se houver mais de um projeto, cria-se novas configurações para cada um baseado nas configurações antigas.
                    for(Project oProject : projectList){
                        for(Configurations oConf : oldConfigList){
                            oConf.setiIDConfiguracao(0); // Zera o iIDConfig para ele criar um novo.
                            oConf.setiIDProjeto(oProject.getiIDProjeto()); // Seta o iIDProjeto.
                            try {
                                insertConfigurations(oConf);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }else{
            db.execSQL("alter table " + DB_CONFIGURATIONS.TABLE_NAME + " add column " + DB_CONFIGURATIONS.COLUMN_IDPROJETO + " INTEGER "+
                    "REFERENCES "+DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE;");
        }
    }
    private void updateMaterial(){
        if(oldMaterialList != null){
            projectList = getProjects(null,null,null,null);
            if(projectList != null){
                firstProject = projectList.get(0);
                db.execSQL("alter table " + DB_MATERIAL.TABLE_NAME + " add column " + DB_MATERIAL.COLUMN_IDPROJETO + " INTEGER default " +firstProject.getiIDProjeto()+
                        " REFERENCES "+
                        DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE;");
                // Implementando o foreign key após atualizado que já estava cadastrado...
                /*db.execSQL("ALTER TABLE "+DB_MATERIAL.TABLE_NAME+" ADD FOREIGN KEY("+DB_MATERIAL.COLUMN_IDPROJETO+") REFERENCES "+
                        DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE;");*/
                // Agora cria para cada projeto restante, um novo material, identico ao antigo.
                if(projectList.size() > 1){
                    projectList.remove(0);
                    for(Project oProject : projectList){
                        for(TiposMateriais oMaterial : oldMaterialList){
                            oMaterial.setiIDTipoMaterial(0);
                            oMaterial.setiIDProjeto(oProject.getiIDProjeto());
                            try {
                                insertTipoMaterial(oMaterial);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }else{
            db.execSQL("alter table " + DB_MATERIAL.TABLE_NAME + " add column " + DB_MATERIAL.COLUMN_IDPROJETO + " INTEGER "+
                    " REFERENCES "+
                    DB_PROJECT.Estrutura_Projeto.TABLE_NAME+"("+DB_PROJECT.Estrutura_Projeto.COLUMN_ID+") ON DELETE CASCADE;");
        }
    }
    public Piece insertPiece(Piece obj) throws Exception {
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
        return obj;
    }
    public Project insertProject(Project obj) throws Exception {
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
        return obj;
    }
    public Configurations insertConfigurations(Configurations obj) throws Exception {
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
        values.put(DB_CONFIGURATIONS.COLUMN_IDPROJETO,obj.getiIDProjeto());
        values.put(DB_CONFIGURATIONS.COLUMN_LUCRO,obj.getiLucro());
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
        return obj;
    }

    public TiposMateriais insertTipoMaterial(TiposMateriais obj) throws Exception {
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
        return obj;
    }


    public List<Project> getProjects(String where, String[] args, String sortCol, String sortArg) {
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
                lista.add(obj);
            } while (c.moveToNext());
            return lista;
        }else{
            return null;
        }
    }
    public int deletePieces(Piece piece) throws Exception {
        int ret = db.delete(DB_PIECE.Estrutura_Peca.TABLE_NAME, DB_PIECE.Estrutura_Peca.COLUMN_ID + " = ?", new String[]{piece.getiIDPiece() + ""});
        if(ret == 0){
            throw new Exception(c.getString(R.string.peca_delete_error1));
        }else{
            return 1;
        }
    }
    public int deleteConfiguration(Configurations conf) throws Exception {
        List<Piece> list = getPieces(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK+" = ?",new String[]{conf.getiIDConfiguracao()+""},null,null);
        if(list == null) {
            int ret = db.delete(DB_CONFIGURATIONS.TABLE_NAME, DB_CONFIGURATIONS.COLUMN_ID + " = ?", new String[]{conf.getiIDConfiguracao() + ""});
            if (ret == 0) {
                throw new Exception(c.getString(R.string.configuration_delete_error));
            } else {
                return 1;
            }
        }else{
            throw new Exception(c.getString(R.string.configuration_delete_error));
        }
    }
    public int deleteProjectPieces(long iIDProject) throws Exception {
        if(getPieces(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK+" = ?",new String[]{iIDProject+""},null,null) != null) {
            int ret = db.delete(DB_PIECE.Estrutura_Peca.TABLE_NAME, DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK + " = ?", new String[]{iIDProject + ""});
            if (ret == 0) {
                throw new Exception(c.getString(R.string.peca_delete_error2));
            } else {
                return 1;
            }
        }else{
            return 1;
        }
    }
    public int deleteProject(Project project) throws Exception {
        deleteProjectPieces(project.getiIDProjeto());
        int ret = db.delete(DB_PROJECT.Estrutura_Projeto.TABLE_NAME, DB_PROJECT.Estrutura_Projeto.COLUMN_ID+" = ?", new String[]{project.getiIDProjeto() + ""});
        if(ret == 0){
            throw new Exception(c.getString(R.string.projeto_delete_error));
        }else{
            return 1;
        }
    }
    public int deleteMaterialSingle(TiposMateriais tipo) throws Exception {
        List<Piece> list = getPieces(DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK + " = ?", new String[]{tipo.getiIDTipoMaterial() + ""}, null, null);
        if(list != null){
            throw new Exception(c.getString(R.string.material_delete_error));
        }
        int ret = db.delete(DB_MATERIAL.TABLE_NAME, "iIDTipoMaterial = ?", new String[]{tipo.getiIDTipoMaterial() + ""});
        if(ret == 0){
            throw new Exception(c.getString(R.string.material_delete_error));
        }else{
            return 1;
        }
    }

    public List<Piece> getPieces(String where,String[] args,String sortCol,String sortArg){
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
            do {
                Piece obj = new Piece();
                obj.setiIDPiece(c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_ID)));
                obj.setcNome(c.getString(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_NOME)));
                obj.setcImagePath(c.getString(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PATH)));
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
                obj.setiQuant(c.getInt(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_QUANT)));
                obj.setfGramas(c.getDouble(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS)));
                obj.setbSelectedInfo(c.getInt(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_SELECTED_INFO)));
                obj.setiIDProject(c.getLong(c.getColumnIndexOrThrow(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK)));
                lista.add(obj);
            } while (c.moveToNext());
            return lista;
        }else{
            return null;
        }
    }
    public List<Configurations> getConfigurations(String where,String[] args,String sortCol,String sortArg){
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
                DB_CONFIGURATIONS.COLUMN_LUCRO
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
                list.add(obj);
            }while(c.moveToNext());
            return list;
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

}
