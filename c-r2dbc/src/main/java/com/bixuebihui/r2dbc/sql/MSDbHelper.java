package com.bixuebihui.r2dbc.sql;


import com.bixuebihui.shardingjdbc.core.constant.SQLType;
import com.bixuebihui.shardingjdbc.core.hint.HintManagerHolder;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;


/**
 * Master-Slaver mode database helper class.
 * Master connection manager for update, insert and delete
 * Slaver connection for select only.
 *
 * @author Wanxiang.Xing@gmail.com
 * @version $Id: $Id
 */
public class MSDbHelper extends DbHelper {


    public MSDbHelper() {
    }

    /**
	 * The DML_FLAG adopted from ShadingJDBC
	 */
    private static final ThreadLocal<Boolean> DML_FLAG = ThreadLocal.withInitial(() -> false);
    /**
     * reset DML flag.
     */
    public static void resetDmlFlag() {
        DML_FLAG.remove();
    }

	/**
	 * master database for change
	 */
	private ConnectionFactory masterDatasource = null;

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Mono<Connection> getConnection(boolean readOnly)  {
            if (!readOnly) {
                DML_FLAG.set(true);
                return Mono.from(masterDatasource.create());
            } else if (isMasterRoute(SQLType.DQL)) {
                return Mono.from(masterDatasource.create());
            }
            return super.getConnection();
    }

    /**
     * <p>getConnection.</p>
     *
     * @param sqlType a {@link SQLType} object.
     * @return a {@link Connection} object.
     */
    public Mono<Connection> getConnection(final SQLType sqlType)  {
        if (isMasterRoute(sqlType)) {
            DML_FLAG.set(true);
            return Mono.from(masterDatasource.create());
        }
        return super.getConnection();
    }

    private boolean isMasterRoute(final SQLType sqlType) {
        return SQLType.DQL != sqlType || DML_FLAG.get() || HintManagerHolder.isMasterRouteOnly();
    }

    /**
     * <p>getConnection.</p>
     *
     * @return a {@link Connection} object.
     */
    @Override
    public Mono<Connection> getConnection() {
        return  getConnection(false);
    }

    /**
     * <p>Getter for the field <code>masterDatasource</code>.</p>
     *
     * @return a {@link ConnectionFactory} object.
     */
    public ConnectionFactory getMasterDatasource() {
        return masterDatasource;
    }

    /**
     * <p>Setter for the field <code>masterDatasource</code>.</p>
     *
     * @param masterDatasource a {@link ConnectionFactory} object.
     */
    public void setMasterDatasource(ConnectionFactory masterDatasource) {
        this.masterDatasource = masterDatasource;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        HintManagerHolder.clear();
        resetDmlFlag();
        super.close();
    }
}
