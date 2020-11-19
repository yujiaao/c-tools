package com.bixuebihui.tablegen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NameUtils {
	static char KEYWORD_APPEND_CHAR = '_';

	static Set<String> PROJECT_FIELD_KEYWORD = new HashSet<>(Arrays.asList(new String[]{"length", "start", "draw", "count"}));
	static Set<String> JAVA_KEYWORD = new HashSet<>(Arrays.asList(new String[]{"abstract",
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
			"transient", "try", "void", "volatile", "while"}));

	public static boolean isYes(String str){
		return "Y".equals(str)
		|| "YES".equals(str);
	}

	public static String columnNameToFieldName(String columnName){
		if (JAVA_KEYWORD.contains(columnName) || (PROJECT_FIELD_KEYWORD.contains(columnName))) {
			columnName = KEYWORD_APPEND_CHAR + columnName;
		}
		return columnName;
	}

    /**
     * Upper cases the first character in a String. Includes a compatibility
     * flag for earlier users. This will be removed at some stage.
     * @param keepCase TODO
     */
	public static String firstUp(String p, boolean keepCase) {
        return keepCase ? (p.substring(0, 1).toUpperCase() + p.substring(1, p.length())) :
                (p.substring(0, 1).toUpperCase() + p.substring(1, p.length()).toLowerCase());
    }

	public static String firstLow(String p, boolean keepCase) {
		return keepCase ? (p.substring(0, 1).toLowerCase() + p.substring(1, p.length())) :
		 (p.substring(0, 1).toLowerCase() + p.substring(1, p.length()).toLowerCase());
    }
}
