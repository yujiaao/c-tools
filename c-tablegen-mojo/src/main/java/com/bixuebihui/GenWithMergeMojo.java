package com.bixuebihui;

/**
 * Maven plugin for generating code with smart merge enabled.
 * This will preserve user modifications in Dto, SearchCmd, and Converter files.
 * @goal gen-with-merge
 * @author xwx
 */
public class GenWithMergeMojo extends TableGenMojo {


    @Override
    public void execute() {
        // Enable smart merge by setting system property
        // This will be read by ProjectConfig.readFrom() if not set in properties file
        System.setProperty("tablegen.enableMerge", "true");
        getLog().info("Smart merge enabled - user modifications in Dto, SearchCmd, and Converter will be preserved");
        super.execute();
    }
}