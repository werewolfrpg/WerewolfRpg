package net.aesten.wwrpg.commands.admin;

import net.azalealibrary.command.CommandNode;

public class GameCommand extends CommandNode {
    public GameCommand() {
        super("game");
    }


    private static final class StartCommand extends CommandNode {
        public StartCommand(String name, CommandNode... children) {
            super("start", children);
        }
    }

    private static final class StopCommand extends CommandNode {
        public StopCommand(String name, CommandNode... children) {
            super("stop", children);
        }
    }

    private static final class MapCommand extends CommandNode {
        public MapCommand(String name, CommandNode... children) {
            super("map", children);
        }
    }

    private static final class PlayersCommand extends CommandNode {
        public PlayersCommand(String name, CommandNode... children) {
            super("players", children);
        }


        private static final class CountCommand extends CommandNode {
            public CountCommand(String name, CommandNode... children) {
                super(name, children);
            }
        }
    }
}
