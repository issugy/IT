package com.fedorenko.db.models;

import java.util.*;
import java.util.stream.Collectors;

public class Table {
    private Integer id;
    private String name;
    private Base base;
    private List<Attribute> attributes;
    private Map<Integer, Row> rows;

    public Table(){}

    public Table(Integer id, String name, Base base) {
        this.id = id;
        this.name = name;
        this.base = base;
        this.attributes = new ArrayList<>();
        this.rows = new HashMap<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<Integer, Row> getRows() {
        return rows;
    }

    public void setRows(Map<Integer, Row> rows) {
        this.rows = rows;
    }

    public Integer getLastRowId(){
        if(rows.size() == 0)
            return 0;
        else return new ArrayList<>(rows.keySet()).get(rows.size() - 1);
    }

    public void addRow(List<Object> values){
        Integer id = getLastRowId() + 1;
        Row row = new Row(id, this);
        row.setValues(values);
        rows.put(id, row);
    }

    public void updateRow(Row row, List<Object> values){
        rows.get(row.getId()).setValues(values);
    }

    public void deleteRow(Row row){
        rows.remove(row.getId());
    }

    public void deleteDuplicateRows(){
        Map<Integer, Row> rowMap = new HashMap<>();
        List<Row> uniqueRows = rows.values().stream().distinct().collect(Collectors.toList());

        for(Row row : uniqueRows){
            rowMap.put(row.getId(), row);
        }

        setRows(rowMap);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Table) {
            Table temp = (Table) obj;
            if(this.id == temp.id && this.rows.equals(temp.rows))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode() + this.rows.hashCode();
    }
}
