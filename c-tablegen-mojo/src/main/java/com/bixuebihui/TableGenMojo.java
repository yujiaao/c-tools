package com.bixuebihui;

import com.bixuebihui.tablegen.TableGen;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;

import java.io.File;
import java.sql.SQLException;


/**
 * @author xwx
 * @goal gen
 */
public class TableGenMojo extends AbstractMojo {

    /**
     * TableGen config file name.
     * @parameter
     *   propPath="propPath"
     *   default-value="tablegen.properties"
     */
    private String propPath;


    @Override
    public void execute() {
        TableGen gen = new TableGen();
        getLog().info("Starting gen tables...");

        if(StringUtils.isBlank(propPath)) {
            propPath = System.getProperty("user.dir") + File.separator + "tablegen.properties";
        }
        getLog().info("propPath " + propPath);
        try {
            for(String path: propPath.split(",")) {
                gen.run(StringUtils.trim(path));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

