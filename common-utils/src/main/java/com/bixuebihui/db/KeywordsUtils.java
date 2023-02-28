package com.bixuebihui.db;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.bixuebihui.db.Dialect.*;

/**
 * @author xwx
 */
public class KeywordsUtils {
	public static char KEYWORD_APPEND_CHAR = '_';

	public static Set<String> PROJECT_FIELD_KEYWORD = new HashSet<>(Arrays.asList("length", "start", "draw", "count"));

	public static Set<String> JAVA_KEYWORD = new HashSet<>(Arrays.asList("abstract",
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

	// https://learn.microsoft.com/en-us/sql/t-sql/language-elements/reserved-keywords-transact-sql?view=sql-server-ver16
	static  final Set<String> SQL_SERVER_KEYWORDS = Set.of(
			"ADD","EXTERNAL","PROCEDURE",
			"ALL","FETCH","PUBLIC",
			"ALTER","FILE","RAISERROR",
			"AND","FILLFACTOR","READ",
			"ANY","FOR","READTEXT",
			"AS","FOREIGN","RECONFIGURE",
			"ASC","FREETEXT","REFERENCES",
			"AUTHORIZATION","FREETEXTTABLE","REPLICATION",
			"BACKUP","FROM","RESTORE",
			"BEGIN","FULL","RESTRICT",
			"BETWEEN","FUNCTION","RETURN",
			"BREAK","GOTO","REVERT",
			"BROWSE","GRANT","REVOKE",
			"BULK","GROUP","RIGHT",
			"BY","HAVING","ROLLBACK",
			"CASCADE","HOLDLOCK","ROWCOUNT",
			"CASE","IDENTITY","ROWGUIDCOL",
			"CHECK","IDENTITY_INSERT","RULE",
			"CHECKPOINT","IDENTITYCOL","SAVE",
			"CLOSE","IF","SCHEMA",
			"CLUSTERED","IN","SECURITYAUDIT",
			"COALESCE","INDEX","SELECT",
			"COLLATE","INNER","SEMANTICKEYPHRASETABLE",
			"COLUMN","INSERT","SEMANTICSIMILARITYDETAILSTABLE",
			"COMMIT","INTERSECT","SEMANTICSIMILARITYTABLE",
			"COMPUTE","INTO","SESSION_USER",
			"CONSTRAINT","IS","SET",
			"CONTAINS","JOIN","SETUSER",
			"CONTAINSTABLE","KEY","SHUTDOWN",
			"CONTINUE","KILL","SOME",
			"CONVERT","LEFT","STATISTICS",
			"CREATE","LIKE","SYSTEM_USER",
			"CROSS","LINENO","TABLE",
			"CURRENT","LOAD","TABLESAMPLE",
			"CURRENT_DATE","MERGE","TEXTSIZE",
			"CURRENT_TIME","NATIONAL","THEN",
			"CURRENT_TIMESTAMP","NOCHECK","TO",
			"CURRENT_USER","NONCLUSTERED","TOP",
			"CURSOR","NOT","TRAN",
			"DATABASE","NULL","TRANSACTION",
			"DBCC","NULLIF","TRIGGER",
			"DEALLOCATE","OF","TRUNCATE",
			"DECLARE","OFF","TRY_CONVERT",
			"DEFAULT","OFFSETS","TSEQUAL",
			"DELETE","ON","UNION",
			"DENY","OPEN","UNIQUE",
			"DESC","OPENDATASOURCE","UNPIVOT",
			"DISK","OPENQUERY","UPDATE",
			"DISTINCT","OPENROWSET","UPDATETEXT",
			"DISTRIBUTED","OPENXML","USE",
			"DOUBLE","OPTION","USER",
			"DROP","OR","VALUES",
			"DUMP","ORDER","VARYING",
			"ELSE","OUTER","VIEW",
			"END","OVER","WAITFOR",
			"ERRLVL","PERCENT","WHEN",
			"ESCAPE","PIVOT","WHERE",
			"EXCEPT","PLAN","WHILE",
			"EXEC","PRECISION","WITH",
			"EXECUTE","PRIMARY","WITHIN", // WITHIN GROUP
			"EXISTS","PRINT","WRITETEXT",
			"EXIT","PROC"
	);

	public static boolean isYes(String str){
		return "Y".equals(str)
		|| "YES".equals(str);
	}

	public static boolean isSqlKeyword(String str){
		return SQL_SERVER_KEYWORDS.contains(str.toUpperCase());
	}

	public static String decorFieldIfIsKeyword(String str, int dbType){
		if(!isSqlKeyword(str)){
			return str;
		}
		if (dbType==SQLSERVER || dbType==SQLSERVER_2005_AND_UP){
			return "["+str+"]";

		}else if(dbType==MYSQL){
			return "`"+str+"`";
		}
		return str;
	}

}
