package com.bixuebihui.r2dbc.sql;

import org.apache.commons.beanutils.Converter;

/**
 * <p>EnumConverter class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class EnumConverter implements Converter {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convert(@SuppressWarnings("rawtypes") Class type, Object value) {

        if(value==null) {
            return null;
        }

        if(type.isEnum()) {
            int v = Integer.parseInt(value.toString());
            return type.getEnumConstants()[v];
        }else{
            System.out.println("WARN: error registed EnumConverter for type:"+type.getCanonicalName());
        }
        return null;
    }

}
