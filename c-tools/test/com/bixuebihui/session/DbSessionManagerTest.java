package com.bixuebihui.session;

import com.bixuebihui.datasource.BitmechanicDataSource;
import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.jdbc.DbHelper;
import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.jdbc.RowMapper;
import com.bixuebihui.jdbc.RowMapperResultReader;
import com.bixuebihui.util.other.CMyException;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DbSessionManagerTest {

	IDbHelper dbh = new DbHelper();

	@Before
	public void setUp() {
		BitmechanicDataSource ds = new BitmechanicDataSource();
		ds.setDatabaseConfig(getConfigH2());
		dbh.setDataSource(ds);
		SimpleSession.SESSION_LIFE = 20000;
	}

	static DatabaseConfig getConfigH2() {

		DatabaseConfig cfg = new DatabaseConfig();
		cfg.setAlias("h2");
		cfg.setClassName("org.h2.Driver");
		cfg.setUsername("sa");
		cfg.setPassword("sa");
		cfg.setDburl("jdbc:h2:mem:test");

		return cfg;

	}

	@Test
	public void testDestroy() throws CMyException, SQLException {
		DbSessionManager sm = new DbSessionManager(dbh);
		SimpleSession ss;
		for (int i = 0; i < 10; i++) {
			ss = sm.createUUIDSession();
			sm.write(ss);
			sm.destroy(ss.getS_id());
			ss = sm.read(ss.getS_id());
			assertNull(ss.getS_id());
		}
		System.out.println("collected session: " + sm.gc((new Date()).getTime() + SimpleSession.SESSION_LIFE));
	}

	@Test
	public void testGc() throws SQLException {

		DbSessionManager sm = new DbSessionManager(dbh);
		sm.gc((new Date()).getTime());
	}

	@Test
	public void testRead() throws SQLException, CMyException {
		System.out.println("session bench begin>>>");
		DbSessionManager sm = new DbSessionManager(dbh);

		Date start = new Date();
		for (int i = 0; i < 1000; i++) {
			SimpleSession ss  = sm.createUUIDSession();
			ss.setUser_id(i+1024768);
			sm.write(ss);
			SimpleSession res = sm.read(ss.getS_id());

			assertEquals(res.getS_id(), ss.getS_id());
		}
		Date end = new Date();
		//System.out.println(ss.toXml());
		System.out
				.println("create & read 1000 sessions used time: " + (end.getTime() - start.getTime()) / 1000.0 + "s");
		System.out.println("collected session: " + sm.gc((new Date()).getTime() + SimpleSession.SESSION_LIFE));
		System.out.println("session bench done.");
	}

	@Test
	public void testWrite() throws SQLException, CMyException {
		DbSessionManager sm = new DbSessionManager(dbh);
		SimpleSession ss = sm.createUUIDSession();
		ss.setUser_id(123456789);
		sm.write(ss);
		System.out.println(ss.toXml());
	}

	class SessionRowMapper implements RowMapper<Object> {

		@Override
        public Object mapRow(ResultSet rs, int index, Set<String> set) throws SQLException {
			SimpleSession ss = new SimpleSession();
			ss.setS_id(rs.getString("s_id"));
			ss.setS_expire(rs.getLong("s_expire"));
			return ss;
		}

	}

	@Test
	public void testExecuteQueryStringObjectArrayRowMapperResultReader() throws SQLException {

		RowMapperResultReader handle = new RowMapperResultReader(new SessionRowMapper());
		String sql = "select * from t_session";
		List ls = dbh.executeQuery(sql, null, handle);

		for (int i = 0; i < ls.size(); i++) {
			System.out.println(((SimpleSession) ls.get(i)).toXml());

		}

	}

}
