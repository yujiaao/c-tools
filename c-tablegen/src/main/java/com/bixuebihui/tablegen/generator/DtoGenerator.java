package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.TableUtils;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.bixuebihui.tablegen.generator.DalGenerator.getParamGattersString;

/**
 * @author xwx
 */
public class DtoGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "Dto";
    private static final Logger LOG = LoggerFactory.getLogger(DtoGenerator.class);

    @Override
    String getTemplateFileName(String tableName) {
        return "dto.java";
    }

    @Override
    public boolean wantToGenerate(String fileName) {
        // 如果 enableMerge 为 false，只生成不存在的文件，不修改已存在的文件
        if (!config.isEnableMerge()) {
            return !new File(fileName).exists();
        }
        // enableMerge 为 true 时，正常生成并智能合并
        return super.wantToGenerate(fileName);
    }

    @Override
    public String generate(String tableName, int index) throws IOException {
        // Generate the new source code using the template
        String generatedSource = super.generate(tableName, index);

        // Check if merge is enabled via ProjectConfig
        if (!config.isEnableMerge()) {
            // Merge disabled, return generated source as-is
            return generatedSource;
        }

        // Get the target file path
        String targetFileName = getTargetFileName(tableName);
        File targetFile = new File(targetFileName);

        // If file exists, merge with existing content
        if (targetFile.exists()) {
            LOG.info("Merging generated DTO with existing file: {}", targetFileName);
            return JavaSourceMerger.mergeSource(targetFile, generatedSource);
        }

        return generatedSource;
    }

    protected String getSourceDir() {
        return config.getBaseDtoDir();
    }

    public static String createKeyObjects(List<String> params) {
        return getParamGattersString(params);
    }

    protected String getInterface(String tableName) {
        // DTO keep lightweight interface
        return " implements Serializable";
    }

    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);
        List<ColumnData> cols = getColsInfo(tableName);
        List<String> keys = getKeysInfo(cols, tableName);

        v.put("hasVersionCol", containsColName(cols, config.getVersionColName()));

        v.put("firstKeyType", this.getPrimaryKeyType(tableName));
        v.put("firstKeyName", this.getFirstKeyName(tableName));

        v.put("whereNoLike", TableGen.createPreparedWhereClause(keys,
                false, cols));
        v.put("keyObjects", createKeyObjects(keys));

        v.put("isView", isView());
        v.put("sharedModelGroup", config.getSharedModelGroup());

        return v;
    }

    @Override
    String getClassSuffix() {
        return CLASS_SUFFIX;
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("dto", tableName);
    }

    @Override
    String getClassName(String tableName) {
        return getPojoClassName(tableName) + CLASS_SUFFIX;
    }

    @Override
    protected boolean columnAnnotationWithConstraints() {
        return false;
    }

    @Override
    protected void additionalSetting(Handlebars handlebars) {
        super.additionalSetting(handlebars);
        // usage: {{type tableName colName}}
        handlebars.registerHelper("colType",
                (tableName, options) -> getColType(options.param(0), this.setInfo.getTableCols((String) tableName)));
        handlebars.registerHelper("isKey",
                (tableName, options) -> this.setInfo.getTableKeys((String) tableName).contains(options.param(0)));

        // usage: {{where tableName}}
        handlebars.registerHelper("where", (tableName, options) -> {
            boolean like = options.param(0);
            return TableGen.createPreparedWhereClause(this.setInfo.getTableKeys((String) tableName), like,
                    this.setInfo.getTableCols((String) tableName));
        });

    }

}
