package me.twister915.corelite.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.twister915.corelite.command.CLCommand;
import me.twister915.corelite.command.CommandMeta;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

public abstract class CLPlugin extends JavaPlugin {
    @Getter private Formatter formatter;
    @Getter private Gson gson = getNewGson();

    protected Gson getNewGson() {
        return getGsonBuilder().create();
    }

    protected GsonBuilder getGsonBuilder() {
        return new GsonBuilder();
    }

    @Override
    public final void onEnable() {
        try {
            saveDefaultConfig();
            if (getClass().isAnnotationPresent(UsesFormats.class)) {
                YAMLConfigurationFile formatsFile = new YAMLConfigurationFile(this, getClass().getAnnotation(UsesFormats.class).file());
                formatsFile.saveDefaultConfig();
                formatter = new Formatter(formatsFile);
            }
            else formatter = null;
            onModuleEnable();
        } catch (Exception e) {
            getLogger().severe("Unable to properly enable this plugin!");
            setEnabled(false);
            e.printStackTrace();
        }
    }

    public final Formatter.FormatBuilder formatAt(String path) {
        if (formatter == null) throw new IllegalStateException("This plugin is not marked to use formats!");
        return formatter.begin(path);
    }

    @Override
    public final void onDisable() {
        try {
            onModuleDisable();
        } catch (Exception e) {
            getLogger().severe("Unable to properly disable this plugin!");
            e.printStackTrace();
        }
    }

    public final <T extends Listener> T registerListener(T listener) {
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    /**
     * Registers a command for handling.
     * @param command The command to register.
     */
    public final <T extends CLCommand> T registerCommand(T command) {
        //Check if we have the command registered using the same name
        PluginCommand command1 = getCommand(command.getName(), this); //Create a command for force registration
        command1.setExecutor(command); //Set the exectuor
        command1.setTabCompleter(command); //Tab completer
        CommandMeta annotation = command.getClass().getAnnotation(CommandMeta.class); //Get the commandMeta
        if (annotation != null){
            command1.setAliases(Arrays.asList(annotation.aliases()));
            command1.setDescription(annotation.description());
            command1.setUsage(annotation.usage());
        }
        getCommandMap().register(this.getDescription().getName(), command1); //Register it with Bukkit
        return command;
    }

    /**
     * Creates a new instance of the command
     *
     * @return new PluginCommand instance of the requested command name
     */
    private PluginCommand getCommand(String name, Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor commandConstructor = PluginCommand.class.getDeclaredConstructor(new Class[]{String.class, Plugin.class});
            commandConstructor.setAccessible(true);
            command = (PluginCommand) commandConstructor.newInstance(name, plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return command;
    }

    /**
     * Gets the command map from bukkit
     *
     * @return The command map from bukkit
     */
    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            PluginManager pluginManager = Bukkit.getPluginManager();
            Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(pluginManager);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return commandMap;
    }

    protected void onModuleEnable() throws Exception {}
    protected void onModuleDisable() throws Exception {}
}
