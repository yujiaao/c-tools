package com.bixuebihui.tablegen.generator;

import com.bixuebihui.jdbc.IDbHelper;
import com.bixuebihui.tablegen.DbDiff;
import com.bixuebihui.tablegen.ProjectConfig;
import com.bixuebihui.tablegen.TableGen;
import com.bixuebihui.tablegen.diffhandler.DiffHandler;
import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableInfo;
import com.bixuebihui.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.bixuebihui.tablegen.TableGen.getTableDataFromLocalCache;
import static com.bixuebihui.tablegen.TableGen.saveTableDataToLocalCache;

/**
 * @author xwx
 */
public class GenerateAll implements DiffHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateAll.class);
    boolean generateSuccess = true;
    Generator[] perTableGenerator = new Generator[]{
           new PojoGenerator(),
            new DalGenerator(),
            new BusinessGenerator(),

            new SearchCmdGenerator(),
            new DelCmdGenerator(),

            new MapperGenerator(),
            new DtoGenerator(),
            new ServiceGenerator(),
            new ServiceImplGenerator(),
            new ControllerGenerator(),
    };

    Generator[] perIndexGenerator = new Generator[]{
            new EsJsonSchemaGenerator(),
    };

    Generator[] perViewGenerator = new Generator[]{
            new ViewPojoGenerator(),
            new ViewDalGenerator(),
            new ViewBusinessGenerator(),

            new ViewSearchCmdGenerator(),
            new ViewMapperGenerator(),
            new ViewDtoGenerator(),
            new ViewServiceGenerator(),
            new ViewServiceImplGenerator(),
            new ViewControllerGenerator(),
    };

    Generator[] onceGenerator = new Generator[]{
      new BaseListGenerator(),
            // new PomGenerator(),
    };


    private BaseListGenerator generator;


    public void run(String filename) {
        run(filename, System.out);
    }

    public void run(String filename, OutputStream out) {

        if (filename != null && !filename.isEmpty()) {
            init(filename);
        }


        try {

            if(generator.esConfig!=null) {
                generateJson(generator.esConfig.getProperty("index_list").split(","));
                return;
            }

            /*
             * 如果generator_all == yes，则生成所有表 否则，进行本地快照和数据库的比对
             */
            generateOnceFiles();
            if (generator.config.isGenerateAll()) {
                generateTables(generator.setInfo.getTableInfos());

                if(generator.setInfo.getViewInfos()!=null) {
                    generateViews(generator.setInfo.getViewInfos());
                }


               // generateWebUI();
               // generateTest();

//                if (config.jspDir != null) {
//                    generateJsp();
//                }
//                generateSpringXml();

                if (generator.config.isGenerateProcedures()) {
                   // generateStoreProcedures(meta);
                }
            } else {
                info("generate_all is off, generate incrementally, " +
                        "if want regenerate, you can remove cache file 'target/gen_table_data.cache'");

                IDbHelper helper = TableGen.getDbHelper(generator.dbConfig);


                try (Connection conn =  helper.getConnection()){
                    DbDiff dd = new DbDiff(helper, helper, getTableDataFromLocalCache(), conn, generator.config.getCatalog(),
                            generator.config.getSchema());
                    dd.addDiffHandler(this);
                    dd.compareTables(generator.config.getR2dc());
                }
            }

        } catch (SQLException | IOException e) {
            generateSuccess = false;
            LOG.error("", e);
        }

        info("Successfully complected!");
    }

    private void info(String s) {
        LOG.info(s);
    }

    private void generateTables(LinkedHashMap<String, TableInfo> tableInfos) throws IOException {
        for(Generator g: perTableGenerator){
            int i=0;
            for(TableInfo info: tableInfos.values()) {
                info(info.getName()+" - " +g.getTargetFileName(info.getName()));
                g.generateToFile(info.getName(), i++);
            }
        }
    }

    private void generateJson(String[] indices) throws IOException {
        for(Generator g: perIndexGenerator){
            int i=0;
            for(String indexName: indices) {
                info("index: "+indexName+" by "+ g.getClass().getSimpleName());
                g.generateToFile(indexName, i++);
            }
        }
    }

    private void generateViews(LinkedHashMap<String, TableInfo> viewInfos) throws IOException {
        for(Generator g: perViewGenerator){
            int i=0;
            for(TableInfo info: viewInfos.values()) {
                g.generateToFile(info.getName(), i++);
            }
        }
    }

    private void generateOnceFiles() throws IOException {
        int i=0;
        for(Generator g: onceGenerator){
            g.generateToFile("", i++);
        }
    }

    void init(String filename) {
        generator = new BaseListGenerator();
        boolean isEs = generator.init(filename);

        if(isEs){

            for(Generator g: perIndexGenerator){
                g.init(generator.config, generator.dbConfig, generator.setInfo, generator.esConfig);
            }

        }else {

            for (Generator g : perTableGenerator) {
                g.init(generator.config, generator.dbConfig, generator.setInfo, generator.esConfig);
            }

            for (Generator g : perViewGenerator) {
                g.init(generator.config, generator.dbConfig, generator.setInfo, generator.esConfig);
            }

            for (Generator g : onceGenerator) {
                g.init(generator.config, generator.dbConfig, generator.setInfo, generator.esConfig);
            }
        }
    }


    @Override
    public void processTableDiff(String tableName) throws IOException {
        ProjectConfig config = generator.config;
        if (config.getExcludeTablesList() != null &&
                config.getExcludeTablesList().containsKey(tableName)) {
            return;
        }

        if (!CollectionUtils.isEmpty(config.getTablesList()) && !config.getTablesList().containsKey(tableName)) {
            return;
        }

        int i=0;
        for(Generator g: perTableGenerator){
            g.generateToFile(tableName, i++);
        }


    }

    @Override
    public void processEnd(HashMap<String, List<ColumnData>> tableData) {
        if (generateSuccess) {
            saveTableDataToLocalCache(tableData);
        }
    }
}
