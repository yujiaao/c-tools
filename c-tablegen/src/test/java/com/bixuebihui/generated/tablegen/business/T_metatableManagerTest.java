package com.bixuebihui.generated.tablegen.business;
/**
  * T_metatable
  *
  * WARNING! Automatically generated file!
  * Do not edit the pojo and dal packages,use AutoCode / bixuebihui-smartable!
  * Code Generator by J.A.Carter
  * Modified by Xing Wanxiang 2008-2012
  * (c) www.goldjetty.com
  */

import com.bixuebihui.BeanFactory;
import com.bixuebihui.generated.tablegen.pojo.T_metatable;
import com.bixuebihui.jdbc.IDbHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class T_metatableManagerTest {
  IDbHelper dbHelper = (IDbHelper) BeanFactory.createObjectById("dbHelper");
  T_metatableManager man = new T_metatableManager(dbHelper.getDataSource());

  /**
   * Select from the database for table "t_metatable"
   */
  @Test
  public void testSelect() {
    List<T_metatable> list = man.select("", "", 0, 10);
    for (T_metatable info : list) {
      System.out.println(info.toXml());
    }
  }

  @Test
  public void testCount() {
    assertTrue(man.count("") >= 0);
  }

  @Test
  public void testInsertdummy() {
    assertTrue(man.insertDummy());
}

}
