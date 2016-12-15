package ua.pp.oped.aromateque.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FilterParameterValue extends EntityIdName implements Parcelable {
    private String name;
    private int id;

    FilterParameterValue(String name, int id) {
        this.name = name;
        this.id = id;
    }

    FilterParameterValue(Parcel in) {

        this.name = in.readString();
        this.id = in.readInt();
    }


    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static final Parcelable.Creator<FilterParameterValue> CREATOR
            = new Parcelable.Creator<FilterParameterValue>() {
        public FilterParameterValue createFromParcel(Parcel in) {
            return new FilterParameterValue(in);
        }

        public FilterParameterValue[] newArray(int size) {
            return new FilterParameterValue[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
    }
}
