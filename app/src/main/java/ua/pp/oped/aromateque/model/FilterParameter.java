package ua.pp.oped.aromateque.model;


import java.util.ArrayList;

public class FilterParameter extends EntityIdName {
    private String name;
    private int id;
    private ArrayList<FilterParameterValue> values;

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

}

