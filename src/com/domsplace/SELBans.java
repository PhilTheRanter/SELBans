package com.domsplace;

import com.domsplace.Events.SELBansCommandEvent;
import com.domsplace.commands.*;
import com.domsplace.listeners.BanListener;
import java.io.InputStream;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SELBans extends JavaPlugin {
    public static boolean isPluginEnabled = false;
    public static YamlConfiguration pluginYML;
    
    public PluginManager pluginManager;
    
    public static BanListener listenerBan;
    
    public static CommandBan commandBan;
    public static CommandKick commandKick;
    public static CommandMute commandMute;
    public static CommandDemote commandDemote;
    public static CommandPardon commandPardon;
    public static CommandPlayerInfo commandPlayerInfo;
    
    @Override
    public void onEnable() {
        BansUtils.dataFolder = this.getDataFolder();
        
        InputStream is = this.getResource("plugin.yml");
        pluginYML = YamlConfiguration.loadConfiguration(is);
        
        pluginManager = getServer().getPluginManager();
        
        //Add Mockup Muted Commands//
        BansBase.mutedCommands.add("msg");
        BansBase.mutedCommands.add("tell");
        BansBase.mutedCommands.add("whisper");
        BansBase.mutedCommands.add("m");
        BansBase.mutedCommands.add("reply");
        BansBase.mutedCommands.add("r");
        
        if(!BansDataManager.checkConfig(this)) {
            disable();
            return;
        }
        
        /*** Register Commands ***/
        commandBan = new CommandBan(this);
        commandKick = new CommandKick(this);
        commandMute = new CommandMute(this);
        commandDemote = new CommandDemote(this);
        commandPardon = new CommandPardon(this);
        commandPlayerInfo = new CommandPlayerInfo(this);
        
        getCommand("ban").setExecutor(commandBan);
        getCommand("kick").setExecutor(commandKick);
        getCommand("warn").setExecutor(commandKick);
        getCommand("mute").setExecutor(commandMute);
        getCommand("demote").setExecutor(commandDemote);
        getCommand("pardon").setExecutor(commandPardon);
        getCommand("playerinfo").setExecutor(commandPlayerInfo);
        getCommand("SELBans").setExecutor(commandPlayerInfo);
        
        /*** Hook into plugins ***/
        BansHookUtils.HookIntoPlugins();
        
        /*** Register Listeners and start Threads ***/
        listenerBan = new BanListener();
        
        isPluginEnabled = true;
        Bukkit.broadcastMessage("§dLoaded " + pluginYML.getString("name") + " version " + pluginYML.getString("version") + " successfully.");
    }
    
    @Override
    public void onDisable() {
        /*** Cancel Threads ***/
        if(listenerBan != null) {
            listenerBan.checkBans.cancel();
        }
        
        if(!isPluginEnabled) {
            BansUtils.msgConsole(BansBase.ChatError + "Plugin failed to load!");
            return;
        }
        
        BansUtils.sqlClose();
    }
    
    public void disable() {
        pluginManager.disablePlugin(this);
    }
    
    public static com.domsplace.SELBans getPlugin() {
        try {
            Plugin p = Bukkit.getPluginManager().getPlugin("SELBans");
            if(p == null || !(p instanceof com.domsplace.SELBans)) {
                return null;
            }
            
            return (com.domsplace.SELBans) p;
        } catch(NoClassDefFoundError e) {
            return null;
        }
    }
    
    public void dispatchCommand(SELBansCommandEvent e) {
        if(e.getCommand().getName().equalsIgnoreCase("ban")) {
            commandBan.onCommand(e.getPlayer(), e.getCommand(), e.getLabel(), e.getArgs());
        }
        if(e.getCommand().getName().equalsIgnoreCase("kick")) {
            commandKick.onCommand(e.getPlayer(), e.getCommand(), e.getLabel(), e.getArgs());
        }
        if(e.getCommand().getName().equalsIgnoreCase("warn")) {
            commandKick.onCommand(e.getPlayer(), e.getCommand(), e.getLabel(), e.getArgs());
        }
        if(e.getCommand().getName().equalsIgnoreCase("mute")) {
            commandMute.onCommand(e.getPlayer(), e.getCommand(), e.getLabel(), e.getArgs());
        }
        if(e.getCommand().getName().equalsIgnoreCase("pardon")) {
            commandPardon.onCommand(e.getPlayer(), e.getCommand(), e.getLabel(), e.getArgs());
        }
        if(e.getCommand().getName().equalsIgnoreCase("playerinfo")) {
            commandPlayerInfo.onCommand(e.getPlayer(), e.getCommand(), e.getLabel(), e.getArgs());
        }
        if(e.getCommand().getName().equalsIgnoreCase("SELBans")) {
            commandPlayerInfo.onCommand(e.getPlayer(), e.getCommand(), e.getLabel(), e.getArgs());
        }
    }
}
