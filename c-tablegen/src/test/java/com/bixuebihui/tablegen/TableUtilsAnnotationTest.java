package com.bixuebihui.tablegen;

import com.bixuebihui.tablegen.entry.ColumnData;
import com.bixuebihui.tablegen.entry.TableSetInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableUtilsAnnotationTest {

    @Test
    void getColumnAnnotationStripsLeadingNewlineFromBlankCustomAnnotation() {
        ProjectConfig config = new ProjectConfig();
        config.useAnnotation = true;
        config.useSwagger = true;
        config.setSwaggerVersion("3");

        ColumnData cd = new ColumnData("created_stime", "timestamp", 0, false);
        cd.setComment("创建时间");

        String ann = TableUtils.getColumnAnnotation(config, new TableSetInfo(), "t", cd, true);
        assertEquals("@Schema(description = \"创建时间\")", ann);
        assertFalse(ann.startsWith("\n"));
    }

    @Test
    void getColumnAnnotationBlockFormatsSingleAnnotationLine() {
        ProjectConfig config = new ProjectConfig();
        config.useAnnotation = true;
        config.useSwagger = true;
        config.setSwaggerVersion("3");

        ColumnData cd = new ColumnData("created_stime", "timestamp", 0, false);
        cd.setComment("创建时间");

        String block = TableUtils.getColumnAnnotationBlock(config, new TableSetInfo(), "t", cd, true);
        assertEquals("    @Schema(description = \"创建时间\")\n", block);
        assertFalse(block.startsWith("\n"));
        assertFalse(block.endsWith("\n\n"));
    }
}
