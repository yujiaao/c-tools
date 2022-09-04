package com.bixuebihui.tablegen.dbinfo;

import com.bixuebihui.BeanFactory;
import com.bixuebihui.jdbc.IDbHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcedureUtilsTest {
	IDbHelper dbHelper;

	DatabaseMetaData metaData;
	String catalog = null;
	String schema = null;
	String tableOwner = "dbo";
	Map<String, String> includeList = null;
	Map<String, String> excludeList = new HashMap<>();



	@BeforeEach
	public void setUp() {
		try {
			 dbHelper= (IDbHelper) BeanFactory.createObjectById("dbHelper");
			metaData = dbHelper.getConnection().getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetProcedureColumns() throws SQLException {
		IProcedureInfo info;

		List<ProcedureInfo> res = ProcedureUtils.getProcedure(metaData,
				catalog, schema, tableOwner, includeList, excludeList);
		info = res.get(2);

		List<ProcedureParameterInfo> rs = ProcedureUtils.getProcedureColumns(metaData, info);
		for(ProcedureParameterInfo in:rs){
			System.out.println(in);
		}
	}

	@Test
	public void testProcess() throws SQLException{


		List<ProcedureInfo> res = ProcedureUtils.getProcedure(metaData,
				catalog, schema, tableOwner, includeList, excludeList);

		for(ProcedureInfo info:res){
			List<ProcedureParameterInfo> rs = ProcedureUtils.getProcedureColumns(metaData, info);
			String str = ProcedureGen.process(info, rs);
			System.out.println(str);
		}
	}

	@Test
	public void testGetProcedure() throws SQLException {

		List<ProcedureInfo> res = ProcedureUtils.getProcedure(metaData,
				catalog, schema, tableOwner, includeList, excludeList);

		System.out.println(res);
	}

	@Test
	public void testCallableStatement() throws SQLException{
		String procedure ="{call sp_xyz(?,?)}";
		Connection con = dbHelper.getConnection();
		CallableStatement cstmt = con.prepareCall(procedure);
		int abc=0;
		cstmt.setInt(1, abc);

		int sqlType =1;
		cstmt.registerOutParameter(2, sqlType );

		cstmt.execute();

		int count = cstmt.getInt(2);

		cstmt.close();
		con.close();

/**
 *
down vote
accepted


You need to get a ResultSet via:

ResultSet rs = stmt.executeQuery("SELECT * FROM setoffunc()");
while (rs.next()) {
   // read results
}
rs.close();
stmt.close();

Or:

// Procedure call.
CallableStatement proc = conn.prepareCall("{ ? = call refcursorfunc() }");
proc.registerOutParameter(1, Types.OTHER);
proc.execute();
ResultSet results = (ResultSet) proc.getObject(1);
while (results.next()) {
    // read results
}
results.close();
proc.close();
 */


	}

}
