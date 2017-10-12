package br.com.cozinheirodelivery.pricemyprint.Objects;

import android.content.Context;
import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 24/08/2016.
 */
// @Todo Modificar o banco de dados para atualizar o peso de todas as peças.
    // TODO: 15/12/2016 adicionar Preferencias de Usuário para escolher a vizualização dos items em gramas/comprimento.
public class Calculator {
    Piece piece;
    Context c;
    DB db;
    public Calculator(Context c,Piece p){
        this.piece = p;
        this.c = c;
        db = new DB(c);
    }
    public static Double getWeight(TiposMateriais mat, int lenght){
        Double densidade = mat.getfDensidade(); // g/cm^3
        Double d = mat.getfDiametro(); // mm
        // ######### PREÇO FILAMENTO USADO (g) #############
        Double vol = ((Math.PI * (Math.pow(d, 2) / 4.0)) * lenght) / 1000; //cm^3
        Double massa = vol * densidade; // g
        return massa;
    }
    public static int getLenght(TiposMateriais mat, Double weight){
        Double vol = weight/mat.getfDensidade();
        int lenght = (int)((vol*1000)/(((Math.PI * (Math.pow(mat.getfDiametro(), 2) / 4.0)))));
        return lenght;
    }
    public static Double getPrice(Context c, Piece piece){
        Double preco = 0.0;
        try {
            Configurations conf = piece.getoConfigurations();
            // ######### DADOS FILAMENTO ##########
            Double massa = 0.0;
            Double densidade = piece.getiIDTipoMaterial().getfDensidade(); // g/cm^3
            Double precoPorG = piece.getiIDTipoMaterial().getfPreco() / 1000.0; // R$/1000g
            int qtd = piece.getiQuant();
            // #### Informação dada em mm ########
            if(piece.getiFilamento()!= 0) {
                Double d = piece.getiIDTipoMaterial().getfDiametro(); // mm
                int comp = piece.getiFilamento(); //mm
                // ######### PREÇO FILAMENTO USADO (g) #############
                Double vol = ((Math.PI * (Math.pow(d, 2) / 4.0)) * comp) / 1000; //cm^3
                massa = vol * densidade; // g
                massa *= qtd;
            }
             // ### Informação dada em Gramas ####
            if(piece.getfGramas()!= 0.0){
                massa = piece.getfGramas();
                massa *= qtd;
            }
            Double tempo = piece.getfTempo()*qtd;
            preco += massa * precoPorG; // Preço do filamento
            // ######### VIDA ÚTIL DA IMPRESSORA ###############
            Double tempoUtil = conf.getfMediaUso()*(conf.getfTempoVida()*365); // Tempo de vida útil da impressora em horas.
            // ######### PREÇO POR DEPRECIACAO (% DO TEMPO UTIL) ##########
            Double depreciacao = tempo/tempoUtil;
            preco += depreciacao*conf.getfPrecoImpressora();
            // ######### PREÇO POR CONSUMO ELÉTRICO ################
            preco += (tempo*conf.getiConsumo()/1000.0)*conf.getfTarifa(); // Consumo de Energia.
            // ######## CALCULO DO PREÇO DE REPARO ################
            Double precoReparo = conf.getfReparo()*conf.getfPrecoImpressora(); // Do total de vida útil da impressora, 10% do seu valor será destinado para reparos durante esse período.
            preco += depreciacao*precoReparo;
            // ######## ADICIONAL HORA-TRABALHO ##################
            preco += conf.getfHoraTrabalho()*tempo;
            // ######## % DE FALHAS AO PREÇO FINAL (RETRABALHO) #############
            preco += conf.getfTaxaFalhas()*preco;
            // ######## APLICAÇÃO DA TEORIA DE LUCRO ###############
            if(conf.getiLucro()>= 0 && conf.getiLucro() < 100) {
                preco = 100 * preco / (100 - conf.getiLucro());
            }else{
                throw new Exception(c.getString(R.string.pricing_lucro));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return preco;
    }
    public static PricingDetails getDetailsString(Context c, List<Piece> pieceList){
        PricingDetails oPricingDetail = new PricingDetails();
        Double fEnergia = 0.0;
        Double fDesgaste= 0.0;
        Double fReparos= 0.0;
        Double fFalhas= 0.0;
        Double fTotal= 0.0;
        Double fHoraTrabalho = 0.0;
        Double fFilamento = 0.0;
        Double fCusto = 0.0;
        Double fLucro = 0.0;
        try {
            for(Piece piece : pieceList) {
                Double fPreco = 0.0;
                Configurations conf = piece.getoConfigurations();
                // ######### DADOS FILAMENTO ##########
                Double massa = 0.0;
                Double densidade = piece.getiIDTipoMaterial().getfDensidade(); // g/cm^3
                Double precoPorG = piece.getiIDTipoMaterial().getfPreco() / 1000.0; // R$/1000g
                int qtd = piece.getiQuant();
                if (piece.getiFilamento() != 0) {
                    Double d = piece.getiIDTipoMaterial().getfDiametro(); // mm
                    int comp = piece.getiFilamento(); //mm
                    // ######### PREÇO FILAMENTO USADO (g) #############
                    Double vol = ((Math.PI * (Math.pow(d, 2) / 4.0)) * comp) / 1000; //cm^3
                    massa = vol * densidade; // g
                    massa *= qtd;
                }
                if (piece.getfGramas() != 0.0) {
                    massa = piece.getfGramas();
                    massa *= qtd;
                }
                Double tempo = piece.getfTempo()*qtd;
                fFilamento += massa*precoPorG;// Preço do filamento

                fPreco += massa * precoPorG;
                // ######### VIDA ÚTIL DA IMPRESSORA ###############
                Double tempoUtil = conf.getfMediaUso() * (conf.getfTempoVida() * 365); // Tempo de vida útil da impressora em horas.
                // ######### PREÇO POR DEPRECIACAO (% DO TEMPO UTIL) ##########
                Double depreciacao = tempo / tempoUtil;
                fDesgaste += depreciacao * conf.getfPrecoImpressora();

                fPreco += depreciacao * conf.getfPrecoImpressora();
                // ######### PREÇO POR CONSUMO ELÉTRICO ################
                fEnergia += (tempo * conf.getiConsumo() / 1000.0) * conf.getfTarifa();// Consumo de Energia.

                fPreco += (tempo * conf.getiConsumo() / 1000.0) * conf.getfTarifa();
                // ######## CALCULO DO PREÇO DE REPARO ################
                Double precoReparo = conf.getfReparo() * conf.getfPrecoImpressora(); // Do total de vida útil da impressora, 10% do seu valor será destinado para reparos durante esse período.
                fReparos += depreciacao*precoReparo;

                fPreco += depreciacao * precoReparo;
                // ######## ADICIONAL HORA-TRABALHO ##################
                fHoraTrabalho += conf.getfHoraTrabalho() * tempo;

                fPreco += conf.getfHoraTrabalho() * tempo;
                // ######## % DE FALHAS AO PREÇO FINAL (RETRABALHO) #############
                fFalhas += conf.getfTaxaFalhas() * fPreco;

                fPreco += conf.getfTaxaFalhas() * fPreco;
                fCusto += fPreco;
                Double valFinal = 100 * fPreco / (100 - conf.getiLucro());
                fLucro += valFinal - fPreco;
                fTotal += valFinal;
            }
            oPricingDetail.setfFilamento(fFilamento);
            oPricingDetail.setfDesgaste(fDesgaste);
            oPricingDetail.setfEnergia(fEnergia);
            oPricingDetail.setfReparos(fReparos);
            oPricingDetail.setfHoraTrabalho(fHoraTrabalho);
            oPricingDetail.setfFalhas(fFalhas);
            oPricingDetail.setfCusto(fCusto);
            oPricingDetail.setfTotal(fTotal);
            oPricingDetail.setfLucro(fLucro);
        }catch(Exception e){
            e.printStackTrace();
        }
        return oPricingDetail;
    }
    public static PricingDetails getProjectDetails(Context c, List<Project> projectList){
        PricingDetails mPricingDetail = new PricingDetails();
        Double fEnergia = 0.0;
        Double fDesgaste= 0.0;
        Double fReparos= 0.0;
        Double fFalhas= 0.0;
        Double fTotal= 0.0;
        Double fHoraTrabalho = 0.0;
        Double fFilamento = 0.0;
        Double fCusto = 0.0;
        Double fLucro = 0.0;
        for(Project oProject: projectList) {
            if(oProject.getChildList()!=null) {
                    PricingDetails aux = getDetailsString(c, oProject.getChildList());
                    fTotal += aux.getfTotal();
                    fDesgaste += aux.getfDesgaste();
                    fEnergia += aux.getfEnergia();
                    fFalhas += aux.getfFalhas();
                    fReparos += aux.getfReparos();
                    fHoraTrabalho += aux.getfHoraTrabalho();
                    fFilamento += aux.getfFilamento();
                    fCusto += aux.getfCusto();
                    fLucro += aux.getfLucro();
            }
        }
            mPricingDetail.setfFalhas(fFalhas);
            mPricingDetail.setfTotal(fTotal);
            mPricingDetail.setfReparos(fReparos);
            mPricingDetail.setfEnergia(fEnergia);
            mPricingDetail.setfDesgaste(fDesgaste);
            mPricingDetail.setfFilamento(fFilamento);
            mPricingDetail.setfHoraTrabalho(fHoraTrabalho);
            mPricingDetail.setfCusto(fCusto);
            mPricingDetail.setfLucro(fLucro);
        return mPricingDetail;
    }
}
