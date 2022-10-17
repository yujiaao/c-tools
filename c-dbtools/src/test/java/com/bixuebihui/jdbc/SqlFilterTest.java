package com.bixuebihui.jdbc;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlFilterTest {

	@Test
	public void testToSqlObject() {
		SqlFilter filter = new SqlFilter();
		SqlObject obj0 = filter.toSqlObject();

		assertEquals("", obj0.getSqlString());

		filter.contain("name", "abc");

		SqlObject obj = filter.toSqlObject();
		assertEquals(1, obj.getParameters().length);
		assertEquals("where name like concat('%', ?, '%') ", obj.getSqlString());
	}

	@Test
	public void testAddFilter() {
		SqlFilter filter = new SqlFilter();

		filter.contain("name", "abc");
		filter.is("cnt", 100);

		SqlObject obj = filter.toSqlObject();

		assertEquals("where name like concat('%', ?, '%')  and cnt = ? ", obj.getSqlString());
		assertEquals(2, obj.getParameters().length);
	}

	@Test
	public void testOr(){
		SqlFilter filter = new SqlFilter();
		filter.contain("name", "abc");
		SqlFilter cond = new SqlFilter();
		cond.contain("name", "def");
		SqlFilter cond1 = new SqlFilter();
		cond1.contain("name", "dff");
		cond.or(cond1);
		filter.or(cond);

		SqlObject obj = filter.toSqlObject();

		assertEquals("where ( name like concat('%', ?, '%') ) or ( ( name like concat('%', ?, '%') ) or ( name like concat('%', ?, '%') ))",
				obj.getSqlString());
		assertEquals(3, obj.getParameters().length);
	}



	@Test
	public void testSubGroupOperators(){
		SqlFilter filter = new SqlFilter();

		SqlFilter cond = new SqlFilter();
		cond.contain("name", "def");

		SqlFilter cond1 = new SqlFilter();
		cond1.contain("name", "dff");

		filter.addSubGroupCondition(SqlFilter.Operator.OR, cond, cond1);

		SqlObject obj = filter.toSqlObject();

		assertEquals("where ( name like concat('%', ?, '%') ) OR ( name like concat('%', ?, '%') )",
				obj.getSqlString());
		assertEquals(2, obj.getParameters().length);
	}

	@Test
	public void testAddFilters(){
		SqlFilter sf = new 	SqlFilter();
		Map<String, Object> condition = new HashMap<>();
		condition.put("province", 1L);
		condition.put("city", 12L);
		condition.put("count", null);

		sf.addFilters(condition);
		SqlObject obj = sf.toSqlObject();
		assertEquals("where province = ?  and city = ? ", obj.getSqlString());
		assertEquals(2, obj.getParameters().length);

		Object[] objs = obj.getParameters();
		assertEquals(1L, objs[0]);
		assertEquals(12L, objs[1]);
	}


	@Test
	public void testInjection(){
		assertEquals("", SqlFilter.transactSQLInjection("flksdfjlk ' and 1=1 --"));
		assertEquals("flksdfjlk sfs",SqlFilter.transactSQLInjection("flksdfjlk sfs"));
	}

    @Test
    void transactSQLInjection() {
		String res = SqlFilter.transactSQLInjection("';--");
		assertEquals("", res);
    }

    void dumpArray(Object[] arr){
		for(Object o:arr){
			if(o instanceof Object[]){
				dumpArray((Object[]) o);
			}else{
				System.out.println(o);
			}
		}
	}
    public void foo(Object...values){
		dumpArray(values);
		bar(1,values);
	}

	public void bar(int a, Object...values){
		System.out.println(a);

		dumpArray(values);
	}

    @Test
	public void varargsTest(){
		foo(1,2);
	}
}
