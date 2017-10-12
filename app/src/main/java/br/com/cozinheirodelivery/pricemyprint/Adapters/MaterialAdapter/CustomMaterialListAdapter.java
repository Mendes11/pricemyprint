package br.com.cozinheirodelivery.pricemyprint.Adapters.MaterialAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 15/09/2016.
 */
public class CustomMaterialListAdapter extends BaseAdapter {
    LayoutInflater inflator;
    List<TiposMateriais> list;

    public CustomMaterialListAdapter(Context c, List<TiposMateriais> list){
        inflator = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getiIDTipoMaterial();
    }
    public void setList(List<TiposMateriais> list){
        this.list = list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflator.inflate(R.layout.list_material, null);
            convertView.setTag(holder);
            holder.cNome = (TextView) convertView.findViewById(R.id.nome);
            holder.fDensidade = (TextView) convertView.findViewById(R.id.densidade);
            holder.fDiametro = (TextView) convertView.findViewById(R.id.diametro);
            holder.fPreco = (TextView) convertView.findViewById(R.id.preco);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        TiposMateriais obj = list.get(position);
        holder.cNome.setText(obj.getcNome());
        holder.fDiametro.setText(obj.getfDiametro()+" mm");
        holder.fDensidade.setText(obj.getfDensidade()+ "g/cm³");
        holder.fPreco.setText(NumberFormat.getCurrencyInstance().format(obj.getfPreco()));

        return convertView;
    }
    class ViewHolder{
        TextView cNome,fDiametro,fPreco,fDensidade;
    }
}
