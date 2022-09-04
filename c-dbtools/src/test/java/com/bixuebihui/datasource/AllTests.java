package com.bixuebihui.datasource;

import com.bixuebihui.ConnectionManagerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.runner.RunWith;

@SelectClasses( { DbcpDataSourceTest.class, BitmechanicDataSourceTest.class, ConnectionManagerTest.class } )
@Suite
public class AllTests {

}
