package br.com.cozinheirodelivery.pricemyprint.Adapters.ProjectAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Adapters.SelectableAdapter;
import br.com.cozinheirodelivery.pricemyprint.Objects.Calculator;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 20/12/2016.
 */

public class ProjectAdapter extends SelectableAdapter<ProjectAdapter.ViewHolder> {
    private List<Project> projectList;
    private Context mContext;
    private AdapterListener mListener;

    public interface AdapterListener{
        public void onImageClickListener(Project oProject);
        public void onClickListener(int position);
        public void onLongClickListener(int position);
    }

    public ProjectAdapter(Context mContext,List<Project> projectList,AdapterListener mListener){
        this.mContext = mContext;
        this.projectList = projectList;
        this.mListener = mListener;
    }

    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_projeto,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProjectAdapter.ViewHolder holder, int position) {
        final Project oProject = projectList.get(position);
        holder.cNome.setText(oProject.getcNome());
        //Fazer a soma dos comprimentos e preços das peças para informar aqui:
        Double fPreco = 0.0;
        Double fTempo = 0.0;
        int iFilamento = 0;
        Double fPeso = 0.0;
        if(oProject.getChildList() !=null) {
            for (Piece obj : oProject.getChildList()) {
                fPreco += Calculator.getPrice(mContext,obj);
                //fPreco += obj.getfPreco();
                fTempo += obj.getfTempo()*obj.getiQuant();
                //iFilamento += obj.getiFilamento();
                fPeso += obj.getfGramas()*obj.getiQuant();
            }
        }
        int h,m;
        int tMin = (int) Math.round(fTempo*60);
        m = (int) tMin%60;
        h = (int) (tMin/60);

        holder.fTempo.setText(h+"h "+m+"min");
        holder.fPreco.setText(NumberFormat.getCurrencyInstance().format(fPreco));
        holder.fGramas.setText(String.format("%.2f",fPeso)+"g");

        holder.selectedOverlay.setBackgroundResource(isSelected(position)? R.color.highlited_color:R.color.white);
        //holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        // Setando Imagem do Botão
        try {
            File imgFile = new File(oProject.getcPicturePath());
            Bitmap myBitmap = decodeFile(imgFile);
            holder.picture.setImageBitmap(myBitmap);
            holder.picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }catch(Exception e){
            holder.picture.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
            holder.picture.setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        public TextView cNome,fTempo,fPreco,fGramas;
        public ImageButton picture;
        public View selectedOverlay;
        LinearLayout clickableLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            cNome = (TextView) itemView.findViewById(R.id.nome);
            fTempo = (TextView) itemView.findViewById(R.id.tempo);
            fPreco = (TextView) itemView.findViewById(R.id.preco);
            fGramas = (TextView) itemView.findViewById(R.id.comprimento);
            picture = (ImageButton) itemView.findViewById(R.id.imagem);
            clickableLayout = (LinearLayout) itemView.findViewById(R.id.teste);
            selectedOverlay = (View) itemView.findViewById(R.id.card_view);
            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onImageClickListener(projectList.get(getAdapterPosition()));
                }
            });
            clickableLayout.setOnClickListener(this);
            clickableLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == picture.getId()) {
                Log.d("Click", "ITEM PRESSED = " + String.valueOf(getAdapterPosition()));
            }else{
                Log.d("Click", "ROW PRESSED = " + String.valueOf(getAdapterPosition()));
                mListener.onClickListener(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onLongClickListener(getAdapterPosition());
            return true;
        }
    }
    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=140;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }

}
