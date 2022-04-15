package de.jakllp.jaklutils.logging;

import de.jakllp.jaklutils.main.JaklUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomLogger {
    public Boolean coloured = true;
    ConsoleCommandSender console = null;
    Logger logger = null;

    public CustomLogger(ConsoleCommandSender console, Logger logger) {
        this.console = console;
        this.logger = logger;
    }

    public String getMsg(String raw) {
        String colour = Colors.colorise(raw);
        if (!coloured) {
            return ChatColor.stripColor(colour);
        }
        return colour;
    }

    public void log(String message, Level level) {
        print(message);
        log(level);
    }

    public void defaultLog(String msg, Level level) {
        logger.log(level, msg);
    }

    public void log(Level level) {
        logger.log(level, "");
    }

    public void error(Exception e) {
        print(JaklUtils.colors.getError() + e.getLocalizedMessage());
        e.printStackTrace();
    }

    public void error(String msg, Exception e) {
        print(JaklUtils.colors.getError() + msg);
        e.printStackTrace();
    }

    public void info(String message) {
        print(JaklUtils.colors.getInfo() + message);
    }

    public void print(String message) {
        if (coloured) {
            console.sendMessage(ChatColor.RED + "[JaklUtils] "
                    + ChatColor.RESET + getMsg(message));
        } else {
            logger.info(getMsg(message));
        }
        return;
    }
}
