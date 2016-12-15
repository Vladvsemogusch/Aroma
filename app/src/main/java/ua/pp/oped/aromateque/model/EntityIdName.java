package ua.pp.oped.aromateque.model;


import android.os.Parcel;
import android.os.Parcelable;

public class EntityIdName implements Parcelable {
    int id;
    String name;


    public EntityIdName() {
        //stub
    }

    public EntityIdName(Parcel in) {
        //stub
    }

    public static final Parcelable.Creator<EntityIdName> CREATOR
            = new Parcelable.Creator<EntityIdName>() {
        public EntityIdName createFromParcel(Parcel in) {
            return new EntityIdName(in);
        }

        public EntityIdName[] newArray(int size) {
            return new EntityIdName[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
