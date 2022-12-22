package com.fedorenko.db.models;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private Integer id;
    private Table table;
    private List<Object> values;

    public Row(){}

    public Row(Integer id, Table table) {
        this.id = id;
        this.table = table;
        this.values = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<Object> getAllValues(){
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.addAll(values);
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Row) {
            Row temp = (Row) obj;
            if(this.values.equals(temp.values))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }
}

