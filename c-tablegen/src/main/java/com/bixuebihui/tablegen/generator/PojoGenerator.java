package com.bixuebihui.tablegen.generator;

import com.bixuebihui.generated.tablegen.pojo.T_metatable;
import com.bixuebihui.tablegen.TableUtils;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import com.github.jknack.handlebars.Handlebars;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author xwx
 */
public class PojoGenerator extends BaseGenerator{

    public static String getExtendsClasses(TableSetInfo setInfo, String tableName) {
        if (setInfo.getTableDataExt() != null) {
            T_metatable tab = setInfo.getTableDataExt().get(tableName);
            if (tab != null) {
                StringBuilder sb = new StringBuilder();
                if (StringUtils.isNotBlank(tab.getExtrasuperclasses())) {
                    sb.append(", extends ").append(tab.getExtrasuperclasses());
                }
                return sb.toString();
            }
        }
        return "";
    }

    /**
     *         import io.swagger.annotations.ApiModel;
     *         import io.swagger.annotations.ApiModelProperty;
     *
     *         @ApiModel(value = "用户数据传输对象")
     *         public class User {
     *             @ApiModelProperty(value = "用户id")
     *             private int id;
     *             @ApiModelProperty(value = "用户昵称")
     *             private String nickname;
     *
     *            https://blog.csdn.net/sdyy321/article/details/40298081
     *
     *             import com.fasterxml.jackson.annotation.JsonProperty;
     *
     *             @JsonAlias can be used!
     *             @JsonAlias
     *         这个注解是在JackSon 2.9版本中才有的。作用是在反序列化的时候可以让Bean的属性接收多个json字段的名称。可以加在字段上或者getter和setter方法上。
     *
     *         @JsonProperty("template_cnode")
     *         @JsonIgnoreProperties
     */
    public String getAnnotationForClass(){
        return "io.swagger.annotations.ApiModel;";
    }


    @Override
    public String getTargetFileName(String tableName) {
        return  getTargetFileName("pojo", tableName);
    }

    @Override
    String getTemplateFileName(String tableName) {
        return "pojo.java";
    }

    @Override
    public String getClassName(String tableName) {
      return getPojoClassName(tableName);
    }

    @Override
    protected void additionalSetting(Handlebars handlebars) {
        super.additionalSetting(handlebars);
        // usage: {{columnAnnotation aColumnData tableName}}
        handlebars.registerHelper("columnAnnotation", (col, options) ->
                TableUtils.getColumnAnnotation(config, setInfo, options.param(0),(ColumnData) col, true));

        // usage: {{columnDescription aColumnData tableName}}
        handlebars.registerHelper("columnDescription", (col, options) -> TableUtils.getColumnDescription(config,
                setInfo.getColumnsExtInfo(options.param(0)), options.param(0), (ColumnData)col));

    }

    @Override
    protected String getExtendsClasses(String tableName) {
      return getExtendsClasses(setInfo, tableName);
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

        // If file exists, merge with existing content (保留用户修改，如自定义 setter 逻辑)
        if (targetFile.exists()) {
            LOG.info("Merging generated Pojo with existing file: {}", targetFileName);
            return JavaSourceMerger.mergeSource(targetFile, generatedSource);
        }

        return generatedSource;
    }

}
