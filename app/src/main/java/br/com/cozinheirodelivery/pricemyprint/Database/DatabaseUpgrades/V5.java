package br.com.cozinheirodelivery.pricemyprint.Database.DatabaseUpgrades;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
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

/**
 * Created by Mendes on 15/12/2016.
 */

public class V5 {
    SQLiteDatabase db;
    public V5(SQLiteDatabase db){
        this.db = db;
    }
    public void UpdateDatabase(){
        List<Project> projectList = getProjects(null,null,null,null);
        if(projectList != null) {
            for (Project project : projectList) {
                if (project.getChildList() != null) {
                    for (Piece piece : project.getChildList()) {
                        piece.setfGramas(Calculator.getWeight(piece.getiIDTipoMaterial(), piece.getiFilamento()));
                        try {
                            Piece p = this.insertPiece(piece);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        projectList = getProjects(null,null,null,null);
    }
    public List<Project> getProjects(String where, String[] args, String sortCol, String sortArg) {

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
                obj.setChildList(this.getPieces(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK + " = ?", new String[]{String.valueOf(obj.getiIDProjeto())}, null, null));
                lista.add(obj);
            } while (c.moveToNext());

            return lista;
        }else{

            return null;
        }
    }
    public Piece insertPiece(Piece obj) throws Exception {
        ContentValues values = new ContentValues();
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_NOME, obj.getcNome());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_TEMPO, obj.getfTempo());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO, obj.getiFilamento());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_PRECO, obj.getfPreco());
        values.put(DB_PIECE.Estrutura_Peca.COLUMN_GRAMAS,obj.getfGramas());
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
                throw new Exception("Não foi possível inserir nova Peça, verifique os dados inseridos.");
            }
            obj.setiIDPiece(newRowId);
        }else{
            int ret = db.update(DB_PIECE.Estrutura_Peca.TABLE_NAME,values,DB_PIECE.Estrutura_Peca.COLUMN_ID+" = ?",new String[]{String.valueOf(obj.getiIDPiece())});
            if (ret == 0){
                throw new Exception("Não foi Possível atualizar a Peça.");
            }
        }
        return obj;
    }
    public List<Piece> getPieces(String where,String[] args,String sortCol,String sortArg){

        String[] colNames = {
                DB_PIECE.Estrutura_Peca.COLUMN_ID,
                DB_PIECE.Estrutura_Peca.COLUMN_NOME,
                DB_PIECE.Estrutura_Peca.COLUMN_TEMPO,
                DB_PIECE.Estrutura_Peca.COLUMN_PRECO,
                DB_PIECE.Estrutura_Peca.COLUMN_FILAMENTO,
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
