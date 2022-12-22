package com.fedorenko.db;

import com.fedorenko.db.models.Base;
import com.fedorenko.db.models.Row;
import com.fedorenko.db.models.Table;
import com.fedorenko.db.models.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.*;


public class DatabaseTest {
    private static Base base;

    @BeforeClass
    public static void init() throws Exception {
        base = new Base("db1");
        File file = new File(DatabaseTest.class.getResource("/databases/" + base.getName() + ".json").getFile());
        base.loadDataFromFile(file);
    }

    @Test
    public void addTableTest() throws Exception {
        List<String> columns = new ArrayList<>();
        columns.add("name");
        columns.add("price");
        columns.add("color");

        List<Type> types = new ArrayList<>();
        types.add(Type.STRING);
        types.add(Type.INTEGER);
        types.add(Type.COLOR);

        Table table = base.findTableByName("new");
        Assert.assertNull(table);

        base.addTable("new", columns, types);
        table = base.findTableByName("new");
        Assert.assertNotNull(table);
        Assert.assertEquals(3, table.getAttributes().size());
    }

    @Test
    public void deleteDuplicateRowsTest(){
        Table table = base.findTableByName("product");
        table.getRows().clear();
        table.addRow(Arrays.asList("apple", 1000, 5.0, "rgb(0,0,0)"));
        table.addRow(Arrays.asList("dell", 2000, 9.0, "rgb(0,0,0)"));
        table.addRow(Arrays.asList("samsung", 1000, 7.0, "rgb(255,255,255)"));
        table.addRow(Arrays.asList("apple", 1000, 5.0, "rgb(0,0,0)"));
        table.addRow(Arrays.asList("dell", 2000, 9.0, "rgb(0,0,0)"));
        table.addRow(Arrays.asList("apple", 1000, 5.0, "rgb(255,0,0)"));
        table.addRow(Arrays.asList("apple", 2000, 5.0, "rgb(0,0,0)"));

        Assert.assertEquals(7, table.getRows().size());
        table.deleteDuplicateRows();

        Assert.assertEquals(5, table.getRows().size());
    }

    @Test
    public void saveTable() throws Exception {
        Base copy = new Base(base.getName() + "_copy");
        copy.setTables(base.getTables());
        copy.saveToFile(new File(getClass().getResource("/databases").getFile()));

        Base copyLoaded = new Base(copy.getName());
        copyLoaded.loadDataFromFile(new File(getClass().getResource(
                "/databases/" + copyLoaded.getName() + ".json").getFile())
        );

        Assert.assertEquals(copy, copyLoaded);
    }
}
