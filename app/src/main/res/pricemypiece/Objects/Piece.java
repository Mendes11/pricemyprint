package pricemypiece.Objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mendes on 23/08/2016.
 */
public class Piece implements Parcelable {
    TiposMateriais iIDTipoMaterial;
    Configurations oConfigurations;
    long iIDPiece,iIDProject;
    int iFilamento;
    String cNome;
    Double fPreco,fTempo;
    Project oproject;
    public Piece(){

    }

    protected Piece(Parcel in) {
        iIDTipoMaterial = in.readParcelable(TiposMateriais.class.getClassLoader());
        iIDPiece = in.readLong();
        iIDProject = in.readLong();
        iFilamento = in.readInt();
        cNome = in.readString();
        fTempo = in.readDouble();
        fPreco = in.readDouble();
        oproject = in.readParcelable(Project.class.getClassLoader());
        oConfigurations = in.readParcelable(Configurations.class.getClassLoader());
    }

    public static final Creator<Piece> CREATOR = new Creator<Piece>() {
        @Override
        public Piece createFromParcel(Parcel in) {
            return new Piece(in);
        }

        @Override
        public Piece[] newArray(int size) {
            return new Piece[size];
        }
    };

    public Project getOproject() {
        return oproject;
    }

    public void setOproject(Project oproject) {
        this.oproject = oproject;
    }

    public TiposMateriais getiIDTipoMaterial() {
        return iIDTipoMaterial;
    }

    public void setiIDTipoMaterial(TiposMateriais iIDTipoMaterial) {
        this.iIDTipoMaterial = iIDTipoMaterial;
    }

    public long getiIDPiece() {
        return iIDPiece;
    }


    public void setiIDPiece(long iIDPiece) {
        this.iIDPiece = iIDPiece;
    }

    public long getiIDProject() {
        return iIDProject;
    }

    public void setiIDProject(long iIDProject) {
        this.iIDProject = iIDProject;
    }

    public int getiFilamento() {
        return iFilamento;
    }

    public void setiFilamento(int iFilamento) {
        this.iFilamento = iFilamento;
    }

    public String getcNome() {
        return cNome;
    }

    public void setcNome(String cNome) {
        this.cNome = cNome;
    }

    public Double getfPreco() {
        return fPreco;
    }

    public void setfPreco(Double fPreco) {
        this.fPreco = fPreco;
    }

    public Double getfTempo() {
        return fTempo;
    }

    public void setfTempo(Double fTempo) {
        this.fTempo = fTempo;
    }

    public Configurations getoConfigurations() {
        return oConfigurations;
    }

    public void setoConfigurations(Configurations oConfigurations) {
        this.oConfigurations = oConfigurations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(iIDPiece);
        dest.writeLong(iIDProject);
        dest.writeInt(iFilamento);
        dest.writeString(cNome);
        dest.writeDouble(fTempo);
        dest.writeDouble(fPreco);
        dest.writeParcelable(oproject, flags);
        dest.writeParcelable(iIDTipoMaterial,flags);
        dest.writeParcelable(oConfigurations,flags);
    }
}
