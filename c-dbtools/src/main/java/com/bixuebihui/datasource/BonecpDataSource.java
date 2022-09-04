//package com.bixuebihui.datasource;
//
//
//
//import com.bixuebihui.dbcon.DatabaseConfig;
//import com.jolbox.bonecp.BoneCPDataSource;
//
//
///**
// * <p>BonecpDataSource class.</p>
// *
// * @author xingwx
// * @version $Id: $Id
// */
//public class BonecpDataSource extends BoneCPDataSource implements  INamingPool {
//
//
//    /**
//     *
//     */
//    private static final long serialVersionUID = 649482223380426885L;
//
//    private String alias;
//
//    /**
//     * <p>Getter for the field <code>alias</code>.</p>
//     *
//     * @return a {@link java.lang.String} object.
//     */
//    @Override
//    public String getAlias() {
//        return alias;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void setAlias(String alias) {
//        this.alias = alias;
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void setDatabaseConfig(DatabaseConfig cfg) {
//        this.setAlias(cfg.getAlias());
//        this.setDriverClass(cfg.getClassName());
//        this.setJdbcUrl(cfg.getDburl());
//        this.setUsername(cfg.getUsername());
//        this.setPassword(cfg.getPassword());
//        this.setMaxConnectionsPerPartition(cfg.getMaxActive());
//        this.setMaxConnectionAgeInSeconds(cfg.getMaxWaitTime());
//        this.setIdleMaxAgeInSeconds(cfg.getMaxIdle());
//        this.setStatementsCacheSize(cfg.getMaxOpenPreparedStatements());
//    }
//
//
//}
