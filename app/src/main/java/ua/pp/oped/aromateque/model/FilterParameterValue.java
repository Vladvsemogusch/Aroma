package ua.pp.oped.aromateque.model;

public class FilterParameterValue extends EntityIdName {
    private String name;
    private int id;

    FilterParameterValue(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
