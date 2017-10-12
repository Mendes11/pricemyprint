package br.com.cozinheirodelivery.pricemyprint.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Mendes on 23/08/2016.
 */
public class Project implements Parcelable{
    long iIDProjeto;
    String cNome;
    List<Piece> childList; // Para o ExpandableAdapter
    String cPicturePath;
    List<Configurations> configurationsList; // Utilizada para alocar todas as confs adicionadas ao projeto (O mesmo objeto se encontra dentro do piece)
    List<TiposMateriais> materiaisList; // Utilizada para alocar todas os materiais adicionados ao projeto (O mesmo objeto se encontra dentro do piece)
    public Project(){

    }

    protected Project(Parcel in) {
        iIDProjeto = in.readLong();
        cNome = in.readString();
        cPicturePath = in.readString();
        childList = in.createTypedArrayList(Piece.CREATOR);
        configurationsList = in.createTypedArrayList(Configurations.CREATOR);
        materiaisList = in.createTypedArrayList(TiposMateriais.CREATOR);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public List<Configurations> getConfigurationsList() {
        return configurationsList;
    }

    public void setConfigurationsList(List<Configurations> configurationsList) {
        this.configurationsList = configurationsList;
    }

    public List<TiposMateriais> getMateriaisList() {
        return materiaisList;
    }

    public void setMateriaisList(List<TiposMateriais> materiaisList) {
        this.materiaisList = materiaisList;
    }

    public String getcPicturePath() {
        return cPicturePath;
    }

    public void setcPicturePath(String cPicturePath) {
        this.cPicturePath = cPicturePath;
    }

    public List<Piece> getChildList() {
        return childList;
    }

    public void setChildList(List<Piece> childList) {
        this.childList = childList;
    }

    public long getiIDProjeto() {
        return iIDProjeto;
    }

    public void setiIDProjeto(long iIDProjeto) {
        this.iIDProjeto = iIDProjeto;
    }

    public String getcNome() {
        return cNome;
    }

    public void setcNome(String cNome) {
        this.cNome = cNome;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(iIDProjeto);
        dest.writeString(cNome);
        dest.writeString(cPicturePath);
        dest.writeList(childList);
        dest.writeList(configurationsList);
        dest.writeList(materiaisList);
    }

    @Override
    public String toString() {
        return getcNome();
    }
}
