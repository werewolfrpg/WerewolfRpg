package net.aesten.werewolfrpg.backend;

import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;

import java.util.List;

public class BackendConfig implements Configurable {
    private final Property<String> jdbcUrl = Property.create(PropertyType.STRING, "jdbc.url", () -> "jdbc:h2:file:./plugins/WerewolfRPG/database/wwrpg").done();
    private final Property<String> jdbcDriver = Property.create(PropertyType.STRING, "jdbc.driver", () -> "org.h2.Driver")
            .description("supported drivers: [com.mysql.cj.jdbc.Driver, org.postgresql.Driver, org.sqlite.JDBC, com.microsoft.sqlserver.jdbc.SQLServerDriver, org.h2.Driver]")
            .done();
    private final Property<String> jdbcUsername = Property.create(PropertyType.STRING, "jdbc.username", () -> "sa").done();
    private final Property<String> jdbcPassword = Property.create(PropertyType.STRING, "jdbc.password", () -> "wwrpg").done();
    private final Property<Integer> backendPort = Property.create(PropertyType.INTEGER, "backend.port", () -> 8085).done();
    private final Property<Boolean> backendShowSql = Property.create(PropertyType.BOOLEAN, "backend.hibernate_show_sql", () -> true).done();

    public Property<String> getJdbcUrl() {
        return jdbcUrl;
    }

    public Property<String> getJdbcDriver() {
        return jdbcDriver;
    }

    public Property<String> getJdbcUsername() {
        return jdbcUsername;
    }

    public Property<String> getJdbcPassword() {
        return jdbcPassword;
    }

    public Property<Integer> getBackendPort() {
        return backendPort;
    }

    public Property<Boolean> getBackendShowSql() {
        return backendShowSql;
    }

    @Override
    public String getName() {
        return "wwrpg-backend-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(jdbcUrl, jdbcDriver, jdbcUsername, jdbcPassword, backendPort, backendShowSql);
    }
}
