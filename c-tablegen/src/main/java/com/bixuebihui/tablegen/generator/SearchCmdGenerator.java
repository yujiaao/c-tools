package com.bixuebihui.tablegen.generator;

import com.bixuebihui.tablegen.TableUtils;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.github.jknack.handlebars.Handlebars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author xwx
 */
public class SearchCmdGenerator extends BaseGenerator {
    protected final static String CLASS_SUFFIX = "SearchCmd";
    private static final Logger LOG = LoggerFactory.getLogger(SearchCmdGenerator.class);

    @Override
    String getTemplateFileName(String tableName) {
        return "search-cmd.java";
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
            LOG.info("Merging generated SearchCmd with existing file: {}", targetFileName);
            return JavaSourceMerger.mergeSource(targetFile, generatedSource);
        }

        return generatedSource;
    }

    @Override
    protected Map<String, Object> getContextMap(String tableName) {
        Map<String, Object> v = super.getContextMap(tableName);
        getColsInfo(tableName);
        return v;
    }

    @Override
    String getClassSuffix() {
        return CLASS_SUFFIX;
    }

    @Override
    public String getTargetFileName(String tableName) {
        return getTargetFileName("cmd", tableName);
    }

    @Override
    String getClassName(String tableName) {
        return getPojoClassName(tableName) + CLASS_SUFFIX;
    }

    protected String getSourceDir() {
        return config.getBaseCmdDir();
    }

    @Override
    protected void additionalSetting(Handlebars handlebars) {
        super.additionalSetting(handlebars);

        handlebars.registerHelper("columnAnnotation", (col, options) -> TableUtils.getColumnAnnotation(config, setInfo,
                options.param(0), (ColumnData) col, false));

    }

}
