package com.bixuebihui;

import com.bixuebihui.tablegen.TableGen;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;


/**
 * @author xwx
 * @goal help
 */
public class TableGenHelpMojo extends AbstractMojo {


    @Override
    public void execute() {
        try {
            TableGen.main(new String[]{"--help"});
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

