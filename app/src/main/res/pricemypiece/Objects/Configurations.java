package pricemypiece.Objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mendes on 23/08/2016.
 */
public class Configurations implements Parcelable {
    long iIDConfiguracao;
    String cNomeConfig;
    Double fTarifa,fReparo,fHoraTrabalho,fTaxaFalhas,fPrecoImpressora,fMediaUso,fTempoVida;
    int iConsumo;
    public Configurations(){}

    @Override
    public String toString() {
        return getcNomeConfig();
    }

    protected Configurations(Parcel in) {
        iIDConfiguracao = in.readLong();
        fTarifa = in.readDouble();
        fReparo = in.readDouble();
        fHoraTrabalho = in.readDouble();
        fTaxaFalhas = in.readDouble();
        fPrecoImpressora = in.readDouble();
        fMediaUso = in.readDouble();
        fTempoVida = in.readDouble();
        iConsumo = in.readInt();
        cNomeConfig = in.readString();
    }

    public void setDefaultValues(){
        DefaultConfigurations.setDefaultConfiguracao(this);

    }
    public static final Creator<Configurations> CREATOR = new Creator<Configurations>() {
        @Override
        public Configurations createFromParcel(Parcel in) {
            return new Configurations(in);
        }

        @Override
        public Configurations[] newArray(int size) {
            return new Configurations[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(iIDConfiguracao);
        dest.writeDouble(fTarifa);
        dest.writeDouble(fReparo);
        dest.writeDouble(fHoraTrabalho);
        dest.writeDouble(fTaxaFalhas);
        dest.writeDouble(fPrecoImpressora);
        dest.writeDouble(fMediaUso);
        dest.writeDouble(fTempoVida);
        dest.writeInt(iConsumo);
        dest.writeString(cNomeConfig);
    }

    public String getcNomeConfig() {
        return cNomeConfig;
    }

    public void setcNomeConfig(String cNomeConfig) {
        this.cNomeConfig = cNomeConfig;
    }

    public int getiConsumo() {
        return iConsumo;
    }

    public void setiConsumo(int iConsumo) {
        this.iConsumo = iConsumo;
    }

    public Double getfPrecoImpressora() {
        return fPrecoImpressora;
    }

    public void setfPrecoImpressora(Double fPrecoImpressora) {
        this.fPrecoImpressora = fPrecoImpressora;
    }

    public long getiIDConfiguracao() {
        return iIDConfiguracao;
    }

    public void setiIDConfiguracao(long iIDConfiguracao) {
        this.iIDConfiguracao = iIDConfiguracao;
    }

    public Double getfTarifa() {
        return fTarifa;
    }

    public void setfTarifa(Double fTarifa) {
        this.fTarifa = fTarifa;
    }

    public Double getfReparo() {
        return fReparo;
    }

    public void setfReparo(Double fReparo) {
        this.fReparo = fReparo;
    }

    public Double getfHoraTrabalho() {
        return fHoraTrabalho;
    }

    public void setfHoraTrabalho(Double fHoraTrabalho) {
        this.fHoraTrabalho = fHoraTrabalho;
    }

    public Double getfTaxaFalhas() {
        return fTaxaFalhas;
    }

    public void setfTaxaFalhas(Double fTaxaFalhas) {
        this.fTaxaFalhas = fTaxaFalhas;
    }

    public Double getfMediaUso() {
        return fMediaUso;
    }

    public void setfMediaUso(Double fMediaUso) {
        this.fMediaUso = fMediaUso;
    }

    public Double getfTempoVida() {
        return fTempoVida;
    }

    public void setfTempoVida(Double fTempoVida) {
        this.fTempoVida = fTempoVida;
    }


}
