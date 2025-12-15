package com.bixuebihui.tablegen.generator;

import com.bixuebihui.dbcon.DatabaseConfig;
import com.bixuebihui.tablegen.ProjectConfig;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author xwx
 */
public interface Generator {
    void init(ProjectConfig config, DatabaseConfig dbConfig, TableSetInfo setInfo, Properties prop);

    String generate(String tableName, int index) throws IOException;

    String getTargetFileName(String tableName);

    default boolean wantToGenerate(String fileName){
        return true;
    }

    /**
     * Generates 生成文件
     * 对于需要保留用户修改的文件（如 Dto、SearchCmd、Converter），
     * 会在 generate() 方法中进行智能合并，保留用户的修改。
     * 
     * @param tableName 表名
     * @param index 索引
     * @throws IOException 如果写入文件失败
     */
    default void generateToFile(String tableName, int index) throws IOException {

        String fileName = getTargetFileName(tableName);
        File file = new File(fileName);
        if (file.getParentFile()!=null && !file.getParentFile().exists()) {
            boolean res = file.getParentFile().mkdirs();
            if (!res) {
                throw new IOException("fail to create directory");
            }
        }
        if(wantToGenerate(fileName)) {
            // generate() 方法会检查文件是否存在，如果存在会进行智能合并
            // 对于 DtoGenerator、SearchCmdGenerator、MapperGenerator 等，
            // 它们会在 generate() 方法中调用 JavaSourceMerger.mergeSource() 进行合并
            String sourceCode = generate(tableName, index);
            
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName),
                    StandardCharsets.UTF_8)) {
                IOUtils.write(sourceCode, writer);
            } catch (IOException e) {
                throw new IOException("fail to write file: " + fileName, e);
            }
        }
    }
}
