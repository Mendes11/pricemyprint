package br.com.cozinheirodelivery.pricemyprint.Adapters.ImportAdapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 02/01/2017.
 */

public class ImportConfigAdapter extends BaseAdapter {
    LayoutInflater inflator;
    List<Configurations> configurationsList;
    onAdapterListener mListener;
    public interface onAdapterListener{
        void onItemStateChanged(Boolean isChecked,Configurations configurationsList);
    }
    public ImportConfigAdapter(Context c, List<Configurations> configurationsList, onAdapterListener mListener){
        this.configurationsList = configurationsList;
        this.mListener = mListener;
        inflator = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
    }
    public void uncheckAll(){

    }
    @Override
    public int getCount() {
        return configurationsList.size();
    }

    @Override
    public Object getItem(int i) {
        return configurationsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return configurationsList.get(i).getiIDConfiguracao();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ImportConfigAdapter.ViewHolder holder;
        if (view == null) {
            holder = new ImportConfigAdapter.ViewHolder();
            view = inflator.inflate(R.layout.list_import_dialog, null);
            view.setTag(holder);
            holder.cNome = (TextView) view.findViewById(R.id.cNome);
            holder.checkBox = (AppCompatCheckBox) view.findViewById(R.id.checkbox);
        }else{
            holder = (ImportConfigAdapter.ViewHolder) view.getTag();
        }
        holder.cNome.setText(configurationsList.get(i).getcNomeConfig());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mListener.onItemStateChanged(b,configurationsList.get(i));
            }
        });
        return view;
    }

    public class ViewHolder{
        CheckBox checkBox;
        TextView cNome;
    }
}
