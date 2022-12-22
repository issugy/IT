package com.fedorenko.db.models;


public class Attribute {
    private String name;
    private Type type;
    private Table table;

    public Attribute(){}

    public Attribute(String name, Type type, Table table) {
        this.name = name;
        this.type = type;
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Attribute) {
            return ((Attribute) obj).name.toLowerCase().equals(name.toLowerCase());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}

