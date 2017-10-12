package br.com.cozinheirodelivery.pricemyprint.Adapters.PrintAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Adapters.SelectableAdapter;
import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Objects.Calculator;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 24/12/2016.
 */

public class PrintAdapter extends SelectableAdapter<PrintAdapter.ViewHolder> {
    List<Piece> pieceList;
    Context c;
    AdapterListener mListener;

    public PrintAdapter(Context c, List<Piece> pieceList, AdapterListener mListener){
        this.c = c;
        this.pieceList = pieceList;
        this.mListener = mListener;
    }

    public interface AdapterListener{
        public void onImageClickListener(Piece oPiece);
        public void onClickListener(int position);
        public void onLongClickListener(int position);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_print,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Piece oPiece = pieceList.get(position);
        int qtd = oPiece.getiQuant();
        holder.cNome.setText(oPiece.getcNome());
        holder.fGramas.setText(String.format("%.2f",oPiece.getfGramas()*qtd)+"g");
        int h,m;
        int tMin = (int) Math.round(oPiece.getfTempo()*60*qtd);
        m = (int) tMin%60;
        h = (int) (tMin/60);
        holder.fTempo.setText(h+"h "+m+"min");
        String quant = c.getString(R.string.quantidade,qtd);
        holder.iQntd.setText(quant);
        holder.fPreco.setText(NumberFormat.getCurrencyInstance().format(Calculator.getPrice(c,oPiece)));
        holder.cConf.setText(oPiece.getoConfigurations().getcNomeConfig());
        holder.cMat.setText(oPiece.getiIDTipoMaterial().getcNome());
        holder.selectedOverlay.setBackgroundResource(isSelected(position)? R.color.highlited_color:R.color.white);
        // Setando Imagem do Botão
        try {
            File imgFile = new File(oPiece.getcImagePath());
            Bitmap myBitmap = decodeFile(imgFile);
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.picture.setImageBitmap(myBitmap);
            holder.picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }catch(Exception e){
            holder.picture.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
            holder.picture.setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    @Override
    public int getItemCount() {
        return pieceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView cNome,fTempo,fPreco,fGramas,iQntd,cConf,cMat;
        public ImageButton picture;
        public View selectedOverlay,clickableLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            cNome = (TextView) itemView.findViewById(R.id.nome);
            fTempo = (TextView) itemView.findViewById(R.id.tempo);
            fPreco = (TextView) itemView.findViewById(R.id.preco);
            fGramas = (TextView) itemView.findViewById(R.id.comprimento);
            iQntd = (TextView) itemView.findViewById(R.id.quantidade);
            cConf = (TextView) itemView.findViewById(R.id.config);
            cMat = (TextView) itemView.findViewById(R.id.material);
            picture = (ImageButton) itemView.findViewById(R.id.imagem);
            selectedOverlay = (View) itemView.findViewById(R.id.card_view);
            clickableLayout = (View) itemView.findViewById(R.id.clickableLayout);
            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onImageClickListener(pieceList.get(getAdapterPosition()));
                }
            });
            clickableLayout.setOnLongClickListener(this);
            clickableLayout.setOnClickListener(this);
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
