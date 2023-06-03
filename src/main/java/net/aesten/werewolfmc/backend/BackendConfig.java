package net.aesten.werewolfmc.backend;

import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.ListProperty;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;

import java.util.ArrayList;
import java.util.List;

public class BackendConfig implements Configurable {
    public static final List<String> SUPPORTED_DRIVERS = List.of("com.mysql.cj.jdbc.Driver", "org.postgresql.Driver", "org.sqlite.JDBC", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "org.h2.Driver");

    private final Property<String> jdbcUrl = Property.create(PropertyType.STRING, "jdbc.url", () -> "jdbc:h2:file:./plugins/Werewolf/database/werewolf").done();
    private final Property<String> jdbcDriver = Property.create(PropertyType.STRING, "jdbc.driver", () -> "org.h2.Driver")
            .description("supported drivers: [ "+ String.join(", ", SUPPORTED_DRIVERS) +" ]")
            .done();
    private final Property<String> jdbcUsername = Property.create(PropertyType.STRING, "jdbc.username", () -> "sa").done();
    private final Property<String> jdbcPassword = Property.create(PropertyType.STRING, "jdbc.password", () -> "werewolf").done();
    private final Property<Integer> backendPort = Property.create(PropertyType.INTEGER, "backend.port", () -> 8085).done();
    private final Property<String> backendUrl = Property.create(PropertyType.STRING, "backend.url", () -> "backend.werewolf.com").done();
    private final Property<Boolean> backendShowSql = Property.create(PropertyType.BOOLEAN, "backend.hibernate_show_sql", () -> false)
            .description("can be enabled for debugging database issues")
            .done();
    private final Property<Integer> backendTokenValidity = Property.create(PropertyType.INTEGER, "backend.token_validity", () -> 1)
            .description("the pace at which the admin access token is re-generated (in hours)")
            .done();
    private final Property<Boolean> backendCorsEnabled = Property.create(PropertyType.BOOLEAN, "backend.cors.enabled", () -> false).done();
    private final ListProperty<String> backendCorsAllowedHosts = ListProperty.create(PropertyType.STRING, "backend.cors.allowed_hosts", () -> new ArrayList<>(List.of("http://localhost:8080")))
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

    public Property<String> getBackendUrl() {
        return backendUrl;
    }

    public Property<Boolean> getBackendShowSql() {
        return backendShowSql;
    }

    public Property<Integer> getBackendTokenValidity() {
        return backendTokenValidity;
    }

    public Property<Boolean> getBackendCorsEnabled() {
        return backendCorsEnabled;
    }

    public ListProperty<String> getBackendCorsAllowedHosts() {
        return backendCorsAllowedHosts;
    }

    @Override
    public String getName() {
        return "werewolf-backend-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(jdbcUrl, jdbcDriver, jdbcUsername, jdbcPassword, backendPort, backendShowSql, backendUrl, backendTokenValidity, backendCorsEnabled, backendCorsAllowedHosts);
    }
}
