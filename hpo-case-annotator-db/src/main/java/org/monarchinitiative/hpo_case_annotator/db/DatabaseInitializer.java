package org.monarchinitiative.hpo_case_annotator.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class DatabaseInitializer {

    private final DataSource dataSource;

    private final String locations;

    public DatabaseInitializer(DataSource dataSource) {
        this(dataSource, "classpath:db/migrations");
    }

    public DatabaseInitializer(DataSource dataSource, String locations) {
        this.dataSource = dataSource;
        this.locations = locations;
    }

    public int initialize() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .load();

        flyway.clean();
        return flyway.migrate();
    }
}
