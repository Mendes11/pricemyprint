package pricemypiece.Objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by Mendes on 23/08/2016.
 */
public class Project implements Parcelable{
    long iIDProjeto;
    String cNome;
    List<Piece> childList; // Para o ExpandableAdapter
    public Project(){

    }

    protected Project(Parcel in) {
        iIDProjeto = in.readLong();
        cNome = in.readString();
        childList = in.createTypedArrayList(Piece.CREATOR);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public List<Piece> getChildList() {
        return childList;
    }

    public void setChildList(List<Piece> childList) {
        this.childList = childList;
    }

    public long getiIDProjeto() {
        return iIDProjeto;
    }

    public void setiIDProjeto(long iIDProjeto) {
        this.iIDProjeto = iIDProjeto;
    }

    public String getcNome() {
        return cNome;
    }

    public void setcNome(String cNome) {
        this.cNome = cNome;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(iIDProjeto);
        dest.writeString(cNome);
        dest.writeList(childList);
    }
}
