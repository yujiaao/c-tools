package com.bixuebihui;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * @author xwx
 * @goal init
 */
public class TableGenInitMojo extends AbstractMojo {

    /**
     * TableGen config file name.
     * @parameter
     *   propPath="propPath"
     *   default-value="tablegen.properties"
     */
    private String propPath;

    @Parameter(defaultValue = "${project}", required = true)
    MavenProject project;




    @Override
    public void execute() {

        List dependencies = project.getDependencies();
        List plugins = project.getBuildPlugins();

        long numDependencies = dependencies.stream().count();
        getLog().info("Number of dependencies: " + numDependencies);
        long numPlugins = plugins.stream().count();
        getLog().info("Number of plugins: " + numPlugins);

        try {
            project.writeModel(new PrintWriter(System.out));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//
//        TableGen gen = new TableGen();
//        getLog().info("Starting gen tables...");
//
//        if(StringUtils.isBlank(propPath)) {
//            propPath = System.getProperty("user.dir") + File.separator + "tablegen.properties";
//        }
//        getLog().info("propPath " + propPath);
//        try {
//            for(String path: propPath.split(",")) {
//                gen.run(StringUtils.trim(path));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}

