package net.aesten.werewolfrpg.bot;

import net.aesten.werewolfrpg.plugin.core.WerewolfGame;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BotConfig implements Configurable {
    private final Property<String> token = Property.create(PropertyType.STRING, "token", () -> "").done();
    private final Property<Long> guildId = Property.create(LONG, "guild-id", () -> 0L).done();
    private final Property<Long> vcId = Property.create(LONG, "voice-channel-id", () -> 0L).done();
    private final Property<Long> lcId = Property.create(LONG, "log-channel-id", () -> 0L).done();

    public Property<String> getToken() {
        return token;
    }

    public Property<Long> getGuildId() {
        return guildId;
    }

    public Property<Long> getLcId() {
        return lcId;
    }

    public Property<Long> getVcId() {
        return vcId;
    }

    @Override
    public String getName() {
        return "wwrpg-bot-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(token, guildId, vcId, lcId);
    }

    private static final PropertyType<Long> LONG = new PropertyType<>(Long.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return WerewolfGame.getMapManager().getWorldManager().getWorlds().keySet().stream().toList();
        }

        @Override
        public Long parse(CommandSender sender, Arguments arguments) {
            return Long.parseLong(arguments.getLast());
        }

        @Override
        public Object serialize(Long object) {
            return object.toString();
        }

        @Override
        public Long deserialize(Object object) {
            return Long.parseLong((String) object);
        }

        @Override
        public String print(Long object) {
            return object.toString();
        }
    };
}
