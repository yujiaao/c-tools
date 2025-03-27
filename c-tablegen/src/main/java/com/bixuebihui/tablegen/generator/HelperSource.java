package com.bixuebihui.tablegen.generator;


import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.tablegen.NameUtils;
import com.bixuebihui.tablegen.ProjectConfig;
import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * HelperSource is a helper class for Handlebars template engine.
 *
 */
public class HelperSource {
  ProjectConfig config;
  TableSetInfo setInfo;

  public HelperSource(ProjectConfig config, TableSetInfo setInfo) {
    this.config = config;
    this.setInfo = setInfo;
  }

  //"firstUp", (name, options) -> NameUtils.firstUp((String) name)
    public static String firstUp(String name) {
        return NameUtils.firstUp(name);
    }
    //"firstLow", (name, options) -> NameUtils.firstLow((String) name)
    public static String firstLow(String name) {
        return NameUtils.firstLow(name);
    }

    //"getPojoClassName", (tableName, options) -> this.getPojoClassName((String) tableName)
    public String getPojoClassName(String tableName) {
        return BaseGenerator.getPojoClassName(tableName, config, setInfo);
    }

    //"constantName", (name, options) -> NameUtils.columnNameToConstantName((String) name)
    public static String constantName(String name) {
        return NameUtils.columnNameToConstantName(name);
    }

    //"join", (items, options) -> StringUtils.join((Object[]) items, options.param(0))
    public static String join(Object[] items, String separator) {
        return String.join(separator, (String[]) items);
    }
    //"typeDefaultValue", (typeName, options) -> TableGen.defaultTypeValue().get(typeName)
    public static String typeDefaultValue(String typeName) {
        return TableGen.defaultTypeValue().get(typeName);
    }

    //"unboxType", (type, options) -> TableGen.unboxType((String) type)
    public static String unboxType(String type) {
        return TableGen.unboxType(type);
    }

  // "let", (String variableName, Options options) -> {
  //            CharSequence finalValue = options.apply(options.fn);
  //            options.context.data(variableName, finalValue.toString().trim());
  //            return null;
  //        }
    public static String let(String variableName, Options options) throws IOException {
        CharSequence finalValue = options.apply(options.fn);
        options.context.data(variableName, finalValue.toString().trim());
        return null;
    }

    //"isKey", (tableName, options) -> this.setInfo.getTableKeys((String) tableName).contains(options.param(0))
    public boolean isKey(String tableName, String colName) {
        return setInfo.getTableKeys(tableName).contains(colName);
    }

    //"isRangeType", (typeName, options) ->
    //                List.of("Timestamp", "Date", "DateTime", "Time").contains((String)typeName)
    public static boolean isRangeType(String typeName) {
        return List.of("Timestamp", "Date", "DateTime", "Time").contains(typeName);
    }

    // contains
    //"contains", (items, options) -> List.of((String[]) items).contains(options.param(0))
    public static boolean contains(Object items, String item) {
      if(items instanceof String[]) {
          return List.of((String[]) items).contains(item);
      }else if(items instanceof Collection){
          return ((Collection<?>) items).contains(item);
      }else{
            return false;
      }
    }

}
