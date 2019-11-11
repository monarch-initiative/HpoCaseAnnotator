package org.monarchinitiative.hpo_case_annotator.db;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseInitializerTest {

    private DataSource dataSource;

    private DatabaseInitializer initializer;

    private static List<String> getTableNames(DataSource dataSource, String tableSchema) throws SQLException {
        String getTablesSql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = ?";
        List<String> tableNames = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement getTablesPs = connection.prepareStatement(getTablesSql)) {
            getTablesPs.setString(1, tableSchema);
            ResultSet rs = getTablesPs.executeQuery();
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        }
        return tableNames;
    }

    @Before
    public void setUp() throws Exception {
        dataSource = TestConfiguration.dataSource();
        initializer = new DatabaseInitializer(dataSource);
    }

    @Test
    public void initialize() throws Exception {
        List<String> namesBefore = getTableNames(dataSource, "HCA");
        assertThat(namesBefore.size(), is(0));

        int count = initializer.initialize();
        assertThat(count, is(10));

        List<String> after = getTableNames(dataSource, "HCA");
        assertThat(after, hasItems("PUBLICATION", "GENE", "FAMILY_INFO", "DISEASE", "PHENOTYPE", "VARIANT"));
//        System.out.println(after);
    }
}