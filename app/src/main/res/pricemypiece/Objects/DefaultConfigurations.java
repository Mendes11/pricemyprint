package pricemypiece.Objects;

/**
 * Created by Mendes on 24/08/2016.
 */
public final class DefaultConfigurations {

    private static final long iIDConfiguracao = 0;
    private static final Double fTarifa = 0.6; //R$ CPFL - Sorocaba
    private static final Double fReparo = 0.1;
    private static final Double fHoraTrabalho = 0.0;
    private static final Double fTaxaFalhas = 0.1;
    private static final Double fMediaUso = 4.0; // Tempo médio de uso em horas
    private static final Double fTempoVida = 3.0; // Tempo médio de duração em anos.
    private static final Double fPrecoImpressora = 2200.00;
    private static final Integer iConsumo = 360;
    private static final String cNome = "Minha Config 1";

    public static String getcNome() {
        return cNome;
    }
    public static Configurations setDefaultConfiguracao(Configurations oConf){
        oConf.setiIDConfiguracao(iIDConfiguracao);
        oConf.setfTarifa(fTarifa);
        oConf.setcNomeConfig("");
        oConf.setfPrecoImpressora(fPrecoImpressora);
        oConf.setfReparo(fReparo);
        oConf.setfTaxaFalhas(fTaxaFalhas);
        oConf.setfMediaUso(fMediaUso);
        oConf.setfTempoVida(fTempoVida);
        oConf.setfPrecoImpressora(fPrecoImpressora);
        oConf.setiConsumo(iConsumo);
        oConf.setfHoraTrabalho(fHoraTrabalho);
        return oConf;
    }
    public static Integer getiConsumo() {
        return iConsumo;
    }

    public static long getiIDConfiguracao() {
        return iIDConfiguracao;
    }

    public static Double getfPrecoImpressora() {
        return fPrecoImpressora;
    }

    public static Double getfTarifa() {
        return fTarifa;
    }

    public static Double getfReparo() {
        return fReparo;
    }

    public static Double getfHoraTrabalho() {
        return fHoraTrabalho;
    }

    public static Double getfTaxaFalhas() {
        return fTaxaFalhas;
    }

    public static Double getfMediaUso() {
        return fMediaUso;
    }

    public static Double getfTempoVida() {
        return fTempoVida;
    }
}
