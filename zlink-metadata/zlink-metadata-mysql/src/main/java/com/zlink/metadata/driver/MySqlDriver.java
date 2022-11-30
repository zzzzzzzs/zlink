package com.zlink.metadata.driver;

import com.zlink.common.assertion.Asserts;
import com.zlink.common.model.Schema;
import com.zlink.metadata.query.IDBQuery;
import com.zlink.metadata.query.MySqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2022/11/28
 */
public class MySqlDriver extends AbstractDriver {

    protected static Logger logger = LoggerFactory.getLogger(MySqlDriver.class);

    @Override
    public IDBQuery getDBQuery() {
        return new MySqlQuery();
    }

    @Override
    public String getType() {
        return "mysql";
    }

    @Override
    public List<Schema> listSchemas() {
        List<Schema> schemas = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet results = null;
        String schemasSql = getDBQuery().schemaAllSql();
        try {
            preparedStatement = conn.get().prepareStatement(schemasSql);
            results = preparedStatement.executeQuery();
            while (results.next()) {
                String schemaName = results.getString(getDBQuery().schemaName());
                schemas.add(new Schema(schemaName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement, results);
        }
        return schemas;
    }
}
