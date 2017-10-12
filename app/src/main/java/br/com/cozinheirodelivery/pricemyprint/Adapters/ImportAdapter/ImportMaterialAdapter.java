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

import br.com.cozinheirodelivery.pricemyprint.Adapters.MaterialAdapter.CustomMaterialListAdapter;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 02/01/2017.
 */

public class ImportMaterialAdapter extends BaseAdapter {
    LayoutInflater inflator;
    List<TiposMateriais> materiaisList;
    onAdapterListener mListener;

    public interface onAdapterListener{
        void onItemStateChanged(Boolean isChecked,TiposMateriais oMaterial);
    }
    public ImportMaterialAdapter(Context c,List<TiposMateriais> materiaisList, onAdapterListener mListener){
        this.materiaisList = materiaisList;
        this.mListener = mListener;
        inflator = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return materiaisList.size();
    }

    @Override
    public Object getItem(int i) {
        return materiaisList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return materiaisList.get(i).getiIDTipoMaterial();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ImportMaterialAdapter.ViewHolder holder;
        if (view == null) {
            holder = new ImportMaterialAdapter.ViewHolder();
            view = inflator.inflate(R.layout.list_import_dialog, null);
            view.setTag(holder);
            holder.cNome = (TextView) view.findViewById(R.id.cNome);
            holder.checkBox = (AppCompatCheckBox) view.findViewById(R.id.checkbox);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.cNome.setText(materiaisList.get(i).getcNome());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mListener.onItemStateChanged(b,materiaisList.get(i));
            }
        });
        return view;
    }

    public class ViewHolder{
        CheckBox checkBox;
        TextView cNome;
    }
}
