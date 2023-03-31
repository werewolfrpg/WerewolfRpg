package net.aesten.werewolfrpg.commands.subcommands;

import net.aesten.werewolfbot.Session;
import net.aesten.werewolfbot.WerewolfBot;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BotCommand extends CommandNode {
    public BotCommand() {
        super("bot");
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        WerewolfBot bot = WerewolfRpg.getBot();
        if (bot == null) {
            WerewolfUtil.sendPluginText(sender, "Bot functionalities are disabled", ChatColor.RED);
        } else if (bot.getCurrentSession() == null) {
            WerewolfUtil.sendPluginText(sender, "There is currently no session", ChatColor.RED);
        } else {
            Session session = bot.getCurrentSession();
            WerewolfUtil.sendPluginText(sender, "Current session in server: " + session.getVc().getGuild().getName());
            WerewolfUtil.sendPluginText(sender, "Session started at: " + session.getTimestamp());
            WerewolfUtil.sendPluginText(sender, "Voice channel is: " + session.getVc().getName());
            WerewolfUtil.sendPluginText(sender, "Log channel is: " + session.getLc().getName());
        }
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.bot";
    }
}
