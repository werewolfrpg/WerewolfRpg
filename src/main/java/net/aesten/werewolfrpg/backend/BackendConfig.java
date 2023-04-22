package net.aesten.werewolfrpg.backend;

import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.ListProperty;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;

import java.util.ArrayList;
import java.util.List;

public class BackendConfig implements Configurable {
    private final Property<String> jdbcUrl = Property.create(PropertyType.STRING, "jdbc.url", () -> "jdbc:h2:file:./plugins/WerewolfRPG/database/wwrpg").done();
    private final Property<String> jdbcDriver = Property.create(PropertyType.STRING, "jdbc.driver", () -> "org.h2.Driver")
            .description("supported drivers: [com.mysql.cj.jdbc.Driver, org.postgresql.Driver, org.sqlite.JDBC, com.microsoft.sqlserver.jdbc.SQLServerDriver, org.h2.Driver]")
            .done();
    private final Property<String> jdbcUsername = Property.create(PropertyType.STRING, "jdbc.username", () -> "sa").done();
    private final Property<String> jdbcPassword = Property.create(PropertyType.STRING, "jdbc.password", () -> "wwrpg").done();
    private final Property<Integer> backendPort = Property.create(PropertyType.INTEGER, "backend.port", () -> 8085).done();
    private final Property<Boolean> backendShowSql = Property.create(PropertyType.BOOLEAN, "backend.hibernate_show_sql", () -> false)
            .description("can be enabled for debugging database issues")
            .done();
    private final Property<Integer> backendTokenValidity = Property.create(PropertyType.INTEGER, "backend.token_validity", () -> 1)
            .description("the pace at which the admin access token is re-generated (in hours)")
            .done();
    private final ListProperty<String> backendCorsAllowed = ListProperty.create(PropertyType.STRING, "backend.cors_enabled_hosts", () -> new ArrayList<>(List.of("http://localhost:8080")))
            .description("list of hosts which will have cors enabled by Javalin")
            .done();

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

    public Property<Integer> getBackendTokenValidity() {
        return backendTokenValidity;
    }

    public ListProperty<String> getBackendCorsAllowed() {
        return backendCorsAllowed;
    }

    @Override
    public String getName() {
        return "wwrpg-backend-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(jdbcUrl, jdbcDriver, jdbcUsername, jdbcPassword, backendPort, backendShowSql, backendTokenValidity, backendCorsAllowed);
    }
}
