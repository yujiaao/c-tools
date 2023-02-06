package com.bixuebihui.jmesa;

import com.bixuebihui.jdbc.*;
import org.apache.commons.lang3.StringUtils;
import org.jmesa.limit.Limit;
import org.jmesa.model.PageItems;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author xwx
 */
public class ItemsProvider<T> implements PageItems {
    /**
     *
     */
    private final String uniquePropertyName;
    IReaderService<T> service;

    public ItemsProvider(String uniquePropertyName, IReaderService<T> service) {
        this.uniquePropertyName = uniquePropertyName;
        this.service = service;
    }

    @Override
    public int getTotalRows(Limit limit) {

        SqlFilter filter = AbstractWebUI.getFilter(limit);
        int totalRows = 0;
        try {
            SqlObject obj = filter.toSqlObject();

            totalRows = service.countWhere(obj.getSqlString(), obj.getParameters());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRows;

    }

    @Override
    public Collection<T> getItems(Limit limit) {
        SqlSort sort = AbstractWebUI.getSort(limit);
        SqlFilter filter = AbstractWebUI.getFilter(limit);

        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();

        String sortSql = sort.toString();

        if (StringUtils.trimToNull(sortSql) == null) {
            if (uniquePropertyName != null) {
                sortSql = "order by " + uniquePropertyName;
            } else {
                sortSql = "";
            }
        }

        try {
            SqlObject obj = filter.toSqlObject();
             return service.select(obj.getSqlString(), obj.getParameters(), sortSql,
                    rowStart, rowEnd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();

    }
}
