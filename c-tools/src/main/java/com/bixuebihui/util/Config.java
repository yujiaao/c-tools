/*
 * Config.java
 *
 */

package com.bixuebihui.util;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.DatabaseConfiguration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is the single entry point for accessing configuration properties
 *
 * @author Allen Gilliland
 * @author xwx
 */
public class Config implements AutoCloseable {

    private static final String DEFAULT_CONFIG = "/ApplicationResources.properties";
    private static final String CUSTOM_PROPERTIES = "/custom.properties";
    private static final String CUSTOM_JVM_PARAM = "custom.config";

    private static CompositeConfiguration config = null;
    private static DatabaseConfiguration dbconfig = null;

    private static final Logger mLogger = LoggerFactory.getLogger(Config.class);

    /**
     * 二阶初始化
     * 0- 未初始化,
     * 1-完成本地文件初始化,
     * 2-完成ZOOKEEPER初始化
     * 4-完成数据库初始化
     * 数据库初始化需要
     */
    private static final int CONFIG_LOCAL = 1;
    private static final int CONFIG_ZOOKEEPER = 1 << 1;
    private static final int CONFIG_DB = 1 << 2;

    private static volatile int STAGE = 0;

    private static volatile boolean dbInitPhase = false;

    /*
     * Static block run once at class loading
     *
     * We load the default properties and any custom properties we find
     */
    static {

        reload();

    }


    public static void reload() {
        try {


            config = new CompositeConfiguration();

            // -----
            boolean useZooKeeper = false;
                if (initFileBaseConfig()) {
                    STAGE |= CONFIG_LOCAL;

                }

                if (((STAGE & CONFIG_ZOOKEEPER) != 0)
                        || !useZooKeeper) {
                    if (initDbConfig()) {
                        STAGE |= CONFIG_DB;
                    }
                }


        } catch (Exception e) {
            mLogger.warn("Error when reload config",e);
        }
    }


    public static boolean initDbConfig() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (!dbInitPhase) {
            dbInitPhase = true;
        } else {
            mLogger.info("Config:数据库已初始化过，跳过重复执行初始化操作");
            return (STAGE & CONFIG_DB) > 0;
        }

        String dbsource = getProperty("config.datasource");

        if (StringUtils.isNotBlank(dbsource)) {
            Class<?> cls = Class.forName(dbsource);
            String table = getProperty("config.datasource.table");
            String nameColumn = getProperty("config.datasource.namecolumn");
            String keyColumn = getProperty("config.datasource.keycolumn");
            String valueColumn = getProperty("config.datasource.valuecolumn");
            String configurationNameColumn = nameColumn == null ? "c_name" : nameColumn;

            DataSource ds = (DataSource) (cls.getDeclaredConstructor().newInstance());
            dbconfig = new DatabaseConfiguration();
            dbconfig.setDataSource(ds);
            dbconfig.setTable(table);
            dbconfig.setKeyColumn(keyColumn);
            dbconfig.setValueColumn(valueColumn);
            dbconfig.setConfigurationNameColumn(configurationNameColumn);
            dbconfig.setConfigurationName("dbconfig");


            config.addConfiguration(dbconfig);
            return true;
        } else {
            mLogger.info("config.datasource没有设置，采用内存来保存属性");
        }
        return false;
    }

    public static boolean initFileBaseConfig() throws ClassNotFoundException, IOException{
        Properties props = new Properties();
        // we'll need this to get at our properties files in the classpath
        Class<?> configClass = Class.forName("com.bixuebihui.util.Config");

        // first, lets load our default properties
        loadPropertiesFromClasspathFile(DEFAULT_CONFIG, props, configClass);
        loadPropertiesFromClasspathFile(CUSTOM_PROPERTIES, props, configClass);


        // finally, check for an external config file
        String envFile = System.getProperty(CUSTOM_JVM_PARAM);

        if (envFile != null && envFile.length() > 0) {
            File customConfigFile = new File(envFile);
            // make sure the file exists, then try and load it
            if (customConfigFile.exists()) {
                try (InputStream is = new FileInputStream(customConfigFile)) {
                    props.load(is);
                    mLogger.info("successfully loaded custom properties from " + customConfigFile.getAbsolutePath());
                }
            } else {
                mLogger.warn("failed to load custom properties from " + customConfigFile.getAbsolutePath());
            }
        } else {
            mLogger.info("no custom properties file specified via jvm option");
        }

        // Now expand system properties for properties in the
        // config.expandedProperties list,
        // replacing them by their expanded values.
        String expandedPropertiesDef = (String) props.get("config.expandedProperties");
        if (expandedPropertiesDef != null) {
            String[] expandedProperties = expandedPropertiesDef.split(",");
            for (String expandedProperty : expandedProperties) {
                String propName = expandedProperty.trim();
                String initialValue = (String) props.get(propName);
                if (initialValue != null) {
                    String expandedValue = PropertyExpander.expandSystemProperties(initialValue);
                    props.put(propName, expandedValue);
                    if (mLogger.isDebugEnabled()) {
                        mLogger.info("Expanded value of " + propName + " from '" + initialValue + "' to '"
                                + expandedValue + "'");
                    }
                }
            }
        }
        // some debugging for those that want it
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Config looks like this ...");

            String key;
            Enumeration<Object> keys = props.keys();
            while (keys.hasMoreElements()) {
                key = (String) keys.nextElement();
                mLogger.debug(key + "=" + props.getProperty(key));
            }
        }

        config.addConfiguration(new MapConfiguration(props));

        return props.size() > 0;
    }

    private static void loadPropertiesFromClasspathFile(String fileName, Properties mConfig, Class<?> configClass) throws IOException {
        InputStream is = configClass.getResourceAsStream(fileName);
        if (is != null) {
            mConfig.load(is);
            mLogger.info("successfully loaded default properties.");
        } else {
            is = configClass.getClassLoader().getResourceAsStream(fileName);
            if (is != null) {
                mConfig.load(is);
                is.close();
                mLogger.info("successfully loaded " + fileName + " properties.");
            } else {
                mLogger.info("no properties file found in classpath " + fileName);
            }
        }
    }

    // no, you may not instantiate this class :p
    private Config() {
    }


    public static String title() {
        return getProperty("main.title");
    }

    /**
     * Retrieve a property value
     *
     * @param key Name of the property
     * @return String Value of property requested, null if not found
     */
    public static String getProperty(String key) {
        return config == null ? null : config.getString(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String result = config == null ? null : config.getString(key, defaultValue);
        if (StringUtils.isBlank(result)) {
            result = defaultValue;
        }
        return result;
    }

    public static Long getLong(String key) {
        return config.getLong(key);
    }

    public static List<Object> getList(String key) {
        return config.getList(key);
    }

    public static String[] getStringArray(String key) {
        return config.getStringArray(key);
    }

    public static Long[] getLongArray(String key) {
        List<Object> list = getList(key);

        // transform property values into longs
        Long[] tokens = new Long[list.size()];

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = Long.parseLong(list.get(i).toString());
        }

        return tokens;
    }

    /**
     * Retrieve a property as a boolean ... defaults to false if there is an
     * error
     */
    public static boolean getBooleanProperty(String name) {
        return config.getBoolean(name);
    }

    /**
     * Retrieve all property keys
     *
     * @return Enumeration A list of all keys
     */
    public static Iterator<String> keys() {
        return config.getKeys();
    }

    /**
     * 只有配置数据库时才会持久化,其他情况下只是修改内存里的配置信息.
     *
     * @param name  键名
     * @param value 键值
     */
    public static void setProperty(String name, Object value) {
        config.setProperty(name, value);

        // 设置数据库
        if (dbconfig != null) {
            dbconfig.setProperty(name, value);
        }

    }

    public static void addProperty(String name, Object value) {
        config.addProperty(name, value);

        // 设置数据库
        if (dbconfig != null) {
            dbconfig.addProperty(name, value);
        }
    }


    /**
     * Set the "uploads.dir" property at runtime.
     * <p>
     * Properties are meant to be read-only, but we make this one exception for
     * now because we know that some people are still writing their uploads to
     * the webapp context and we can only get that path at runtime.
     */
    public static void setUploadsDir(String path) {
        // only do this if the user wants to use the webapp context
        if ("${webapp.context}".equals(config.getProperty("uploads.dir"))) {
            config.setProperty("uploads.dir", path);
        }
    }

    /**
     * Set the "context.realpath" property at runtime.
     * <p>
     * Properties are meant to be read-only, but we make this one exception for
     * now because there are some classes which rely on having filesystem access
     * to files in the roller webapp context.
     * <p>
     * This property is *not* persisted in any way.
     */
    public static void setContextPath(String path) {
        config.setProperty("context.realpath", path);
    }

    public static int TimeDiff = 0;
    public static final String DATE_FORMAT_TODAY = "HH:mm";
    public static final String DATE_FORMAT_YESTODAY = "MM月dd日";
    public static final String DATE_FORMAT_THIS_YEAR = "MM月dd日";
    public static final String DATE_FORMAT = "yyyy年MM月dd日";

    public static String formatTime(String dt) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatTime(fmt.parse(dt));
        } catch (ParseException e) {
            mLogger.warn("",e);
        }
        return dt;

    }

    public static String formatTime(java.util.Date dt) {
        String text1;

        GregorianCalendar in = new GregorianCalendar();
        in.setTime(dt);

        GregorianCalendar thisday = new GregorianCalendar();
        thisday.set(thisday.get(Calendar.YEAR), thisday.get(Calendar.MONTH),
                thisday.get(Calendar.DAY_OF_MONTH), 0, 0);
        thisday.add(Calendar.HOUR, TimeDiff);

        GregorianCalendar yestoday = new GregorianCalendar();
        yestoday.set(thisday.get(Calendar.YEAR), thisday.get(Calendar.MONTH),
                thisday.get(Calendar.DAY_OF_MONTH), 0, 0);
        yestoday.add(Calendar.DAY_OF_MONTH, -1);

        GregorianCalendar thisyear = new GregorianCalendar(thisday.get(Calendar.YEAR), 1, 1);

        if (dt.compareTo(thisyear.getTime()) >= 0) {
            if (dt.compareTo(yestoday.getTime()) >= 0) {
                if (dt.compareTo(thisday.getTime()) >= 0) {
                    text1 = formatTime(dt, DATE_FORMAT_TODAY);
                    text1 += "<img src=../images/new.gif border=0>";

                } else {
                    text1 = formatTime(dt, DATE_FORMAT_YESTODAY);

                }
            } else {
                text1 = formatTime(dt, DATE_FORMAT_THIS_YEAR);
            }

        } else {
            text1 = formatTime(dt, DATE_FORMAT);
        }

        return text1;

    }

    public static String formatTime(Date dt, String fmt) {
        // "[ yyyy年M月d日]"
        return new SimpleDateFormat(fmt).format(dt);
    }

//
//    private static Configuration initZookeeper(String connectString, String nodePath) {
//        // Init Curator
//        client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
//        client.start();
//
//        try {
//            ZKPropertiesConfiguration config = new ZKPropertiesConfiguration(client, nodePath); // "/path/to/file.properties");
//
//            // add reloading strategy
//            // properties are reloaded when zookeeper node changes.
//            config.setReloadingStrategy(new ZKNodeChangeEventReloadingStrategy());
//
//            // add listener
//            EventListener<ConfigurationEvent> el = new EventListener<ConfigurationEvent>() {
//                public void onEvent(final ConfigurationEvent event) {
//                    if (!event.isBeforeUpdate()) {
//                        System.out.println(
//                                "Path '" + event.getPropertyValue() + "' has been " + event.getEventType() + " !");
//                    }
//                }
//            };
//
//            config.addEventListener(ZKPropertiesConfiguration.EVENT_NODE_CREATE, el);
//            config.addEventListener(ZKPropertiesConfiguration.EVENT_NODE_UPDATE, el);
//            config.addEventListener(ZKPropertiesConfiguration.EVENT_NODE_DELETE, el);
//
//            return config;
//        } catch (ConfigurationException e) {
//            mLogger.warn(e);
//        }
//        return null;
//    }

    @Override
    public void close() {
//        if (client != null) {
//            client.close();
//        }

    }

}
