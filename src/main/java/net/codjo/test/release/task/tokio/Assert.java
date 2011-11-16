/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release.task.tokio;
import net.codjo.test.release.task.AgfTask;
import net.codjo.tokio.JDBCScenario;
import net.codjo.tokio.model.Table;
import java.sql.SQLException;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
/**
 */
public class Assert extends AgfTask {
    private String order;
    private String refId;
    private String table;
    private boolean allTables;


    public void setOrder(String order) {
        this.order = order;
    }


    public String getOrder() {
        return order;
    }


    public void setRefId(String refId) {
        this.refId = refId;
    }


    public String getRefId() {
        return refId;
    }


    public void setTable(String table) {
        this.table = table;
    }


    public String getTable() {
        return table;
    }


    public void setAllTables(boolean allTables) {
        this.allTables = allTables;
    }


    public boolean getAllTables() {
        return allTables;
    }


    @Override
    public void execute() {
        JDBCScenario jdbcsc = getJdbcsc();
        try {
            if (getAllTables()) {
                executeAllTablesAssert(jdbcsc);
            }
            else if (getOrder() != null && !"".equals(getOrder().trim())) {
                executeOrderedTableAssert(jdbcsc);
            }
            else {
                executeTableAssert(jdbcsc);
            }
        }
        catch (SQLException e) {
            throw new BuildException(e);
        }
    }


    void executeAllTablesAssert(JDBCScenario jdbcsc) throws SQLException {
        Iterator it = jdbcsc.getScenario().getOutputDataSet().tables();
        while (it.hasNext()) {
            String outputTable = ((Table)it.next()).getName();
            if (!jdbcsc.verifyOutputs(getConnection(), outputTable)) {
                throw new IllegalArgumentException("Erreur de comparaison sur la table " + outputTable + "\n"
                                                   + jdbcsc.getLastVerifyOutputsReport());
            }
        }
    }


    void executeOrderedTableAssert(JDBCScenario jdbcsc) throws SQLException {
        info("Verification de toutes les tables");
        if (!jdbcsc.verifyOutputs(getConnection(), getTable(), getOrder())) {
            throw new IllegalArgumentException("Erreur de comparaison sur la table " + getTable()
                                               + " avec le tri : " + getOrder() + "\n"
                                               + jdbcsc.getLastVerifyOutputsReport());
        }
    }


    void executeTableAssert(JDBCScenario jdbcsc) throws SQLException {
        info("Verification de la table " + getTable() + " sans ordre de tri");
        if (!jdbcsc.verifyOutputs(getConnection(), getTable())) {
            throw new IllegalArgumentException("Erreur de comparaison " + "sur la table " + getTable()
                                               + " avec le tri par d�faut\n"
                                               + jdbcsc.getLastVerifyOutputsReport());
        }
    }


    JDBCScenario getJdbcsc() {
        return ((SetDb)getReference(getRefId())).getJdbcsc();
    }
}
