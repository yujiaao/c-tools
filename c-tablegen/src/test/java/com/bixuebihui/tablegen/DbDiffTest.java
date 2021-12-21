package com.bixuebihui.tablegen;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bixuebihui.dbcon.DatabaseConfig;

import com.bixuebihui.tablegen.entry.ColumnData;
import org.junit.Assert;
import org.junit.Test;

public class DbDiffTest {

	@Test
	public void testDumpDiffTabs() throws IOException {
		List<String> tab1 = new ArrayList<>();
		tab1.add("tab1");
		tab1.add("tab3");
		List<String> tab2 =new ArrayList<>();
		tab2.add("tab2");
		tab2.add("tab4");

		tab2.add("tab1");
		DbDiff dd = new DbDiff();

		dd.dumpDiffTabs(tab1, tab2);

	}

	@Test
	public void testDumpDiffCols(){
		DbDiff dd = new DbDiff();

		List<ColumnData> cols1 = new ArrayList<>();
		ColumnData col1 = new ColumnData("col1", "int", 10, true);
		ColumnData col2 = new ColumnData("col2", "varchar", 20, true);
		ColumnData col3 = new ColumnData("col3", "varchar", 30, true);
		ColumnData col4 = new ColumnData("col3", "varchar", 60, true);
		ColumnData col5 = new ColumnData("col5", "varchar", 60, true);
		cols1.add(col1);
		cols1.add(col2);
		cols1.add(col4);
		cols1.add(col5);

		List<ColumnData> cols2 = new ArrayList<>();

		cols2.add(col1);
		cols2.add(col2);
		cols2.add(col3);
		dd.dumpDiffCols(cols1, cols2);
	}


	@Test
	public void testDbDiff() throws SQLException, IOException {

		DatabaseConfig config = new DatabaseConfig();
		String className="com.mysql.cj.jdbc.Driver";
		config.setClassName(className);

		String dburl="jdbc:mysql://localhost:3306/ssll";
		String username="ssll";
		String password="ssll123";

		config.setDburl(dburl);
		config.setUsername(username);
		config.setPassword(password);
		config.setAlias("config");

		System.out.println(config);


		DatabaseConfig dstConfig= new DatabaseConfig();
		dstConfig.setClassName(className);
		dstConfig.setAlias("dstConfig");


		dburl="jdbc:mysql://localhost:3306/ssllalpha";
		username="ssllalpha";
		password="ssllalpha123";

		dstConfig.setDburl(dburl);
		dstConfig.setUsername(username);
		dstConfig.setPassword(password);

		System.out.println(dstConfig);

		DbDiff dd = new DbDiff();
		dd.db1 = new DbDiff.Database(dd.makeDataSource(config).getConnection().getMetaData());
		dd.db2 = new DbDiff.Database(dd.makeDataSource(dstConfig).getConnection().getMetaData());
		dd.compareTables(false);


	}

}
