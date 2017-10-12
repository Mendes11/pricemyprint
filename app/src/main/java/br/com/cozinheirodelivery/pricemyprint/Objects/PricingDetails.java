package br.com.cozinheirodelivery.pricemyprint.Objects;

import android.content.Context;

import java.text.NumberFormat;

import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 02/11/2016.
 */

public class PricingDetails {
    Double fEnergia;
    Double fDesgaste;
    Double fReparos;
    Double fFalhas;
    Double fCusto;
    Double fLucro;
    Double fTotal;
    public String generateMessageString(Context c){
        String msg ="";
        msg += c.getString(R.string.filament_price_detail, NumberFormat.getCurrencyInstance().format(getfFilamento()));
        msg += c.getString(R.string.depreciation_price_detail, NumberFormat.getCurrencyInstance().format(getfDesgaste()));
        msg += c.getString(R.string.energy_price_detail, NumberFormat.getCurrencyInstance().format(getfEnergia()));
        msg += c.getString(R.string.repair_price_detail, NumberFormat.getCurrencyInstance().format(getfReparos()));
        msg += c.getString(R.string.hour_work_price_detail, NumberFormat.getCurrencyInstance().format(getfHoraTrabalho()));
        msg += c.getString(R.string.failures_price_detail, NumberFormat.getCurrencyInstance().format(getfFalhas()));
        msg += c.getString(R.string.total_cost_price_detail,NumberFormat.getCurrencyInstance().format(getfCusto()));
        msg += c.getString(R.string.profit_price_detail,NumberFormat.getCurrencyInstance().format(getfLucro()));
        msg += c.getString(R.string.total_price_detail, NumberFormat.getCurrencyInstance().format(getfTotal()));
        return msg;
    }
    public Double getfTotal() {
        return fTotal;
    }

    public Double getfCusto() {
        return fCusto;
    }

    public void setfCusto(Double fCusto) {
        this.fCusto = fCusto;
    }

    public Double getfLucro() {
        return fLucro;
    }

    public void setfLucro(Double fLucro) {
        this.fLucro = fLucro;
    }

    public void setfTotal(Double fTotal) {
        this.fTotal = fTotal;
    }

    public Double getfEnergia() {
        return fEnergia;
    }

    public void setfEnergia(Double fEnergia) {
        this.fEnergia = fEnergia;
    }

    public Double getfDesgaste() {
        return fDesgaste;
    }

    public void setfDesgaste(Double fDesgaste) {
        this.fDesgaste = fDesgaste;
    }

    public Double getfReparos() {
        return fReparos;
    }

    public void setfReparos(Double fReparos) {
        this.fReparos = fReparos;
    }

    public Double getfFalhas() {
        return fFalhas;
    }

    public void setfFalhas(Double fFalhas) {
        this.fFalhas = fFalhas;
    }

    public Double getfHoraTrabalho() {
        return fHoraTrabalho;
    }

    public void setfHoraTrabalho(Double fHoraTrabalho) {
        this.fHoraTrabalho = fHoraTrabalho;
    }

    public Double getfFilamento() {
        return fFilamento;
    }

    public void setfFilamento(Double fFilamento) {
        this.fFilamento = fFilamento;
    }

    Double fHoraTrabalho;
    Double fFilamento;
    public PricingDetails(){

    }

}
