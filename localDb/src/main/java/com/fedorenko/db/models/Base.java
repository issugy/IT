package com.fedorenko.db.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Base {
    private String name;
    private Map<Integer, Table> tables;

    public Base(){}

    public Base(String name) {
        this.name = name;
        tables = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Table> getTables() {
        return tables;
    }

    public void setTables(Map<Integer, Table> tables) {
        this.tables = tables;
    }

    public Integer getLastTableId(){
        if(tables.size() == 0)
            return 0;
        else return new ArrayList<>(tables.keySet()).get(tables.size() - 1);
    }

    public Table findTableByName(String name){
        for(Table table : tables.values())
            if(table.getName().toLowerCase().equals(name.toLowerCase()))
                return table;

        return null;
    }

    public void addTable(String name, List<String> names, List<Type> types) throws Exception {
        if(name.trim().equals(""))
            throw new Exception("Table name is required");
        if(findTableByName(name) != null){
            throw new Exception("Table with this name already exists");
        }

        int id = getLastTableId() + 1;

        Table table = new Table(id, name, this);

        List<Attribute> attributes = new ArrayList<>();

        for(int i = 0; i < names.size(); i++){
            String attrName = names.get(i);
            Type attrType = types.get(i);
            if(attrName.trim().equals("") || attrType == null)
                throw new Exception("Fill in all table attributes");
            attributes.add(new Attribute(attrName, attrType, table));
        }

        List<Attribute> uniqueAttributes = attributes.stream().distinct().collect(Collectors.toList());

        table.setAttributes(uniqueAttributes);

        this.tables.put(id, table);
    }

    public void deleteTable(Table table){
        tables.remove(table.getId());
    }

    public void saveToFile(File file){
        JSONArray tableList = new JSONArray();

        for (Map.Entry<Integer, Table> entry : tables.entrySet()){
            JSONObject tableObject = new JSONObject();

            Integer tableId = entry.getKey();
            Table table = entry.getValue();

            tableObject.put("id", tableId);
            tableObject.put("name", table.getName());

            JSONArray attributes = new JSONArray();
            for (Attribute attribute : table.getAttributes()) {
                JSONObject attrDetails = new JSONObject();
                attrDetails.put("column", attribute.getName());
                attrDetails.put("type", attribute.getType().toString());
                attributes.add(attrDetails);
            }

            tableObject.put("attributes", attributes);

            JSONArray rows = new JSONArray();

            for (Map.Entry<Integer, Row> rowEntry : table.getRows().entrySet()){
                JSONObject rowDetails = new JSONObject();

                Integer rowId = rowEntry.getKey();
                Row row = rowEntry.getValue();

                rowDetails.put("id", rowId);

                JSONArray values = new JSONArray();
                for(Object obj : row.getValues()){
                    values.add(obj.toString());
                }

                rowDetails.put("values", values);
                rows.add(rowDetails);
            }

            tableObject.put("rows", rows);
            tableList.add(tableObject);
        }

        try (FileWriter writer = new FileWriter(file + "/" + name + ".json")) {
            writer.write(tableList.toJSONString());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDataFromFile(File file) throws Exception {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(file))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray tableList = (JSONArray) obj;
            Map<Integer, Table> tables = new LinkedHashMap<>();

            for(int i = 0; i < tableList.size(); i++){
                JSONObject tableDetails = (JSONObject) tableList.get(i);
                Integer tableId = Integer.parseInt(tableDetails.get("id").toString());
                String tableName = (String) tableDetails.get("name");
                Table table = new Table(tableId, tableName, this);

                JSONArray attributesList = (JSONArray) tableDetails.get("attributes");
                List<Attribute> attributes = new ArrayList<>();

                for (Object o : attributesList) {
                    JSONObject attrDetails = (JSONObject) o;
                    String attributeName = (String) attrDetails.get("column");
                    String attributeType = (String) attrDetails.get("type");

                    Optional<Type> optionalType = Type.getIfPresent(attributeType);
                    if (!optionalType.isPresent()) {
                        throw new Exception();
                    }

                    Attribute attribute = new Attribute(attributeName, optionalType.get(), table);
                    attributes.add(attribute);
                }

                table.setAttributes(attributes);

                JSONArray rowList = (JSONArray) tableDetails.get("rows");
                Map<Integer, Row> rows = new LinkedHashMap<>();

                for (Object item : rowList) {
                    JSONObject rowDetails = (JSONObject) item;
                    Integer rowId = Integer.parseInt(rowDetails.get("id").toString());
                    Row row = new Row(rowId, table);

                    JSONArray valueList = (JSONArray) rowDetails.get("values");
                    List<String> strValues = new ArrayList<>();

                    for (Object o : valueList) {
                        strValues.add(o.toString());
                    }

                    List<Object> values = new ArrayList<>();

                    if (attributes.size() != strValues.size())
                        throw new Exception();

                    for (int k = 0; k < attributes.size(); k++) {
                        try {
                            Object value = TypeManager.parseObjectByType(strValues.get(k), attributes.get(k).getType());
                            values.add(value);
                        } catch (NumberFormatException e) {
                            throw new Exception();
                        }
                    }

                    row.setValues(values);
                    rows.put(rowId, row);
                }
                table.setRows(rows);
                tables.put(tableId, table);
            }
            setTables(tables);

        } catch (Exception e) {
            throw new Exception("\n\n!!!!Error: invalid data in db!!!!");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Base) {
            Base temp = (Base) obj;
            if(this.name.equals(temp.name) && this.tables.equals(temp.tables))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + this.tables.hashCode();
    }
}
