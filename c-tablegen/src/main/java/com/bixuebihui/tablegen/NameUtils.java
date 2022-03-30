package com.bixuebihui.tablegen;

import com.google.common.base.CaseFormat;
import org.apache.commons.text.CaseUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xwx
 */
public class NameUtils {
	static char KEYWORD_APPEND_CHAR = '_';

	static Set<String> PROJECT_FIELD_KEYWORD = new HashSet<>(Arrays.asList("length", "start", "draw", "count"));
	static Set<String> JAVA_KEYWORD = new HashSet<>(Arrays.asList("abstract",
			"assert",
			"boolean",
			"break",
			"byte",
			"case",
			"catch",
			"char",
			"class",
			"const",
			"continue", "default", "do", "double", "else",
			"enum", "extends", "final", "finally", "float",
			"for", "goto", "if", "implements", "import",
			"instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public",
			"return", "strictfp", "short", "static", "super",
			"switch", "synchronized", "this", "throw", "throws",
			"transient", "try", "void", "volatile", "while"));

	public static boolean isYes(String str){
		return "Y".equals(str)
		|| "YES".equals(str);
	}

	public static String columnNameToFieldName(String columnName){
		if (JAVA_KEYWORD.contains(columnName) || (PROJECT_FIELD_KEYWORD.contains(columnName))) {
			columnName = KEYWORD_APPEND_CHAR + columnName;
		}
		return CaseUtils.toCamelCase(columnName,false,'_','-');
	}

	public static String tableNameToClassName(String tableName){
		if (JAVA_KEYWORD.contains(tableName) || (PROJECT_FIELD_KEYWORD.contains(tableName))) {
			tableName = KEYWORD_APPEND_CHAR + tableName;
		}
		return CaseUtils.toCamelCase(tableName,true,'_','-');
	}


	public static String columnNameToConstantName(String columnName){
		if(columnName==null){
			return "";
		}
		if (JAVA_KEYWORD.contains(columnName) || (PROJECT_FIELD_KEYWORD.contains(columnName))) {
			columnName = KEYWORD_APPEND_CHAR + columnName;
		}
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, columnName);
	}

    /**
     * Upper cases the first character in a String.
	 * @Since 1.1 snake case to camel case
     * @param p "camel_case"
	 * @return  returns "camelCase"
     */
	public static String firstUp(String p) {
        // Guava
		// CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "camel_case"); // returns camelCase
		// CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "CAMEL_CASE"); // returns CamelCase
		// CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, "camelCase"); // returns CAMEL_CASE
		// CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "CamelCase"); // returns camel-case


		return CaseUtils.toCamelCase(p, true, '_', '-');
	}

	public static String firstLow(String p) {
		return CaseUtils.toCamelCase(p, false, '_','-');

	}

	public static String getConfigBaseDir(String propertiesFilename) {
		int i = propertiesFilename.lastIndexOf(File.separator) + 1;
		return  propertiesFilename.substring(0, i);
	}

}
