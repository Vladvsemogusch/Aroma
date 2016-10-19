package ua.pp.oped.aromateque.model;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import ua.pp.oped.aromateque.R;

public class Category {
    @SerializedName("category_id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("parent_id")
    private int parentId;
    @SerializedName("child_id")
    private ArrayList<Integer> childrenIds;
    @SerializedName("active")
    boolean isActive;
    @SerializedName("children_categories")
    private List<Category> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(ArrayList<Integer> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public ArrayAdapter<String> getAdapter(Context context) {
        List<String> childrenNames = new ArrayList<>();
        for (Category cat : children) {
            childrenNames.add(cat.getName());
            //Log.d("asd",cat.getName());
        }
        return new ArrayAdapter<>(context, R.layout.category_list_item, R.id.txt_category, childrenNames);
    }

}
