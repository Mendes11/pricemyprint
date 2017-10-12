package br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB_CONFIGURATIONS;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_MATERIAL;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_PIECE;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_PROJECT;
import br.com.cozinheirodelivery.pricemyprint.Objects.Calculator;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 03/10/2017.
 * Não houveram alterações na estrutura do BD
 *
 * Apenas foi alterado a forma de utilização de quantidade.
 */

public class V8 {

    SQLiteDatabase db;
    Context c;

    public V8(Context c,SQLiteDatabase db){
        this.db = db;
        this.c = c;
    }
    public V8(){}
    public void updateDatabase() {
        // Carrega todos os projetos existentes.
        List<Project> mProjects = this.getProjects(null,null,null,null);
        if(mProjects != null) {
            for (Project oProject : mProjects) {
                if (oProject.getChildList() != null) {
                    for (Piece oPiece : oProject.getChildList()) {
                        // Para cada Peça, Se esta tiver bSelectedInfo = 0, divide o filamento e todas divide o tempo
                        int qtd = oPiece.getiQuant();
                        if (qtd > 1) {
                            oPiece.setfTempo(oPiece.getfTempo() / qtd);
                            if (oPiece.getbSelectedInfo() == 0) {
                                int compr = oPiece.getiFilamento();
                                compr /= qtd;
                                oPiece.setiFilamento(compr);
                                oPiece.setfGramas(Calculator.getWeight(oPiece.getiIDTipoMaterial(), compr));
                            }else if(oPiece.getbSelectedInfo() == 1){
                                Double massa = oPiece.getfGramas();
                                massa *= qtd;
                                oPiece.setfGramas(massa);
                                oPiece.setiFilamento(Calculator.getLenght(oPiece.getiIDTipoMaterial(),massa));
                            }
                            try {
                                insertPiece(oPiece);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
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

    public List<Project> getProjects(String where,String[] args,String sortCol,String sortArg) {
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
            return lista;
        }else{
            return null;
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
                    List<Configurations> listConfiguracao = this.getConfigurations(DB_PIECE.Estrutura_Peca.COLUMN_CONFIGURATION_FK + " = ?", new String[]{String.valueOf(iIDConfiguracao)}, null, null);
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
            return lista;
        }else{
            return null;
        }
    }
    public List<Configurations> getConfigurations(String where,String[] args,String sortCol,String sortArg){
        //SQLiteDatabase db = getReadableDatabase();
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
        Cursor c = this.db.query(DB_CONFIGURATIONS.TABLE_NAME,
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
            return lista;
        }else{
            return null;
        }
    }
}
