package ua.pp.oped.aromateque.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class FilterParameter extends EntityIdName implements Parcelable {
    private String name;
    private int id;
    private ArrayList<FilterParameterValue> values;
    private boolean isExtended;

    public FilterParameter(String name) {
        this.name = name;
        this.id = 0;
        values = new ArrayList<>();
    }

    public FilterParameter(String name, int id) {
        this.name = name;
        this.id = id;
        values = new ArrayList<>();
    }

    FilterParameter(Parcel in) {
        this.name = in.readString();
        this.id = in.readInt();
        in.readTypedList(values, FilterParameterValue.CREATOR);
    }

    public void addValue(String name, int id) {
        values.add(new FilterParameterValue(name, id));
    }

    public void addValue(String name) {
        values.add(new FilterParameterValue(name, 0));
    }

    public void addValue(FilterParameterValue value) {
        values.add(value);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<FilterParameterValue> getValues() {
        return values;
    }

    public boolean isExtended() {
        return isExtended;
    }

    public void setExtended(boolean extended) {
        isExtended = extended;
    }

    public static final Parcelable.Creator<FilterParameter> CREATOR
            = new Parcelable.Creator<FilterParameter>() {
        public FilterParameter createFromParcel(Parcel in) {
            return new FilterParameter(in);
        }

        public FilterParameter[] newArray(int size) {
            return new FilterParameter[size];
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
        parcel.writeTypedList(values);
    }
}

