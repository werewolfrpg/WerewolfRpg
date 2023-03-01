package net.aesten.wwrpg.commands.admin;

import net.azalealibrary.command.CommandNode;

public class GameCommand extends CommandNode {
    public GameCommand() {
        super("game",
                new StartCommand(),
                new StopCommand(),
                new MapCommand(),
                new PlayersCommand());
    }


    private static final class StartCommand extends CommandNode {
        public StartCommand() {
            super("start");
        }
    }

    private static final class StopCommand extends CommandNode {
        public StopCommand() {
            super("stop");
        }
    }

    private static final class MapCommand extends CommandNode {
        public MapCommand() {
            super("map");
        }
    }

    private static final class PlayersCommand extends CommandNode {
        public PlayersCommand() {
            super("players",
                    new CountCommand(),
                    new ListCommand(),
                    new AddCommand(),
                    new AddAllCommand(),
                    new RemoveCommand(),
                    new RemoveAllCommand());
        }

        private static final class CountCommand extends CommandNode {
            public CountCommand() {
                super("count");
            }
        }

        private static final class ListCommand extends CommandNode {
            public ListCommand() {
                super("list");
            }
        }

        private static final class AddCommand extends CommandNode {
            public AddCommand() {
                super("add");
            }
        }

        private static final class AddAllCommand extends CommandNode {
            public AddAllCommand() {
                super("add-all");
            }
        }

        private static final class RemoveCommand extends CommandNode {
            public RemoveCommand() {
                super("remove");
            }
        }

        private static final class RemoveAllCommand extends CommandNode {
            public RemoveAllCommand() {
                super("remove-all");
            }
        }

    }
}
