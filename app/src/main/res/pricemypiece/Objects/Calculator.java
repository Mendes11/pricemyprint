package pricemypiece.Objects;

import android.content.Context;

import br.com.cozinheiro.pricemypiece.Database.DB;

/**
 * Created by Mendes on 24/08/2016.
 */
public class Calculator {
    Piece piece;
    Context c;
    DB db;
    public Calculator(Context c,Piece p){
        this.piece = p;
        this.c = c;
        db = new DB(c);
    }
    public static Double getPrice(Context c, Piece piece){
        Double preco = 0.0;
        try {
            //@TODO: Rever a lógica da calculator. (valida-la)
            Configurations conf = piece.getoConfigurations();
            // ######### DADOS FILAMENTO ##########
            Double densidade = piece.getiIDTipoMaterial().getfDensidade(); // g/cm^3
            Double precoPorG = piece.getiIDTipoMaterial().getfPreco() / 1000.0; // R$/1000g
            Double d = piece.getiIDTipoMaterial().getfDiametro(); // mm
            int comp = piece.getiFilamento(); //mm
            // ######### PREÇO FILAMENTO USADO (g) #############
            Double vol = ((Math.PI * (Math.pow(d, 2) / 4.0)) * comp) / 1000; //cm^3
            Double massa = vol * densidade; // g
            preco += massa * precoPorG; // Preço do filamento
            // ######### VIDA ÚTIL DA IMPRESSORA ###############
            Double tempoUtil = conf.getfMediaUso()*(conf.getfTempoVida()*365); // Tempo de vida útil da impressora em horas.
            // ######### PREÇO POR DEPRECIACAO (% DO TEMPO UTIL) ##########
            Double depreciacao = piece.fTempo/tempoUtil;
            preco += depreciacao*conf.getfPrecoImpressora();
            // ######### PREÇO POR CONSUMO ELÉTRICO ################
            preco += (piece.fTempo*conf.getiConsumo()/1000.0)*conf.getfTarifa(); // Consumo de Energia.
            // ######## CALCULO DO PREÇO DE REPARO ################
            Double precoReparo = conf.getfReparo()*conf.getfPrecoImpressora(); // Do total de vida útil da impressora, 10% do seu valor será destinado para reparos durante esse período.
            preco += depreciacao*precoReparo;
            // ######## ADICIONAL HORA-TRABALHO ##################
            preco += conf.fHoraTrabalho*piece.fTempo;
            // ######## % DE FALHAS AO PREÇO FINAL (RETRABALHO) #############
            preco += conf.fTaxaFalhas*preco;

        }catch(Exception e){
            e.printStackTrace();
        }
        return preco;
    }
}
