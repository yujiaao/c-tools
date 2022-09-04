package com.bixuebihui.tablegen;

import java.util.Hashtable;
import java.util.Map;

import com.bixuebihui.generated.tablegen.pojo.T_metacolumn;
import com.bixuebihui.generated.tablegen.pojo.T_metatable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PojoPropertiesParserTest {



	@Test
	public void testMergeTableSetting() {
		Map<String, T_metatable> to=null;
		Map<String, T_metatable> from =new Hashtable<String, T_metatable>();

		T_metatable value = new T_metatable();
		Map<String, T_metacolumn> columns = new Hashtable<String, T_metacolumn>();
		T_metacolumn mycol = new T_metacolumn();
		mycol.setCname("mycol");
		mycol.setAnnotation("@NotNull\n@Size(max=10)");
		columns.put("mycol", mycol );


		value.setColumns(columns );
		from.put("test", value );

		Map<String, T_metatable> res = PojoPropertiesParser.mergeTableSetting(to, from);

		assertEquals(mycol.getAnnotation(), res.get("test").getColumns().get("mycol").getAnnotation());

	}

}
