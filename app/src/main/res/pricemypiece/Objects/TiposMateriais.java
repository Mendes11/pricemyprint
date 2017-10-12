package pricemypiece.Objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mendes on 23/08/2016.
 */
public class TiposMateriais implements Parcelable {
    long iIDTipoMaterial;
    String cNome;
    Double fPreco,fDensidade,fDiametro;
    public TiposMateriais(){}

    protected TiposMateriais(Parcel in) {
        iIDTipoMaterial = in.readLong();
        cNome = in.readString();
        fDensidade = in.readDouble();
        fDiametro = in.readDouble();
        fPreco = in.readDouble();
    }

    public static final Creator<TiposMateriais> CREATOR = new Creator<TiposMateriais>() {
        @Override
        public TiposMateriais createFromParcel(Parcel in) {
            return new TiposMateriais(in);
        }

        @Override
        public TiposMateriais[] newArray(int size) {
            return new TiposMateriais[size];
        }
    };

    @Override
    public String toString() {
        return this.cNome;
    }

    public Double getfDiametro() {
        return fDiametro;
    }

    public void setfDiametro(Double fDiametro) {
        this.fDiametro = fDiametro;
    }

    public long getiIDTipoMaterial() {
        return iIDTipoMaterial;
    }

    public void setiIDTipoMaterial(long iIDTipoMaterial) {
        this.iIDTipoMaterial = iIDTipoMaterial;
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

    public Double getfDensidade() {
        return fDensidade;
    }

    public void setfDensidade(Double fDensidade) {
        this.fDensidade = fDensidade;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(iIDTipoMaterial);
        dest.writeDouble(fDensidade);
        dest.writeDouble(fDiametro);
        dest.writeDouble(fPreco);
        dest.writeString(cNome);
    }
}
