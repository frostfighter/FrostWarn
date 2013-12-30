package tm.info.felix;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public final class Warn extends JavaPlugin implements Listener {
	
	boolean ban;
	boolean kickonwarn;
	int warnstoban;
	
	public boolean cfgExists() {
		File f = new File("./" + this.getName() + "/config.yml");
		return f.exists();
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		if(!cfgExists()) {
			saveDefaultConfig();
		}
		getConfig();
		
		
		kickonwarn = getConfig().getBoolean("kickonwarn");
		ban = getConfig().getBoolean("ban");
		warnstoban = getConfig().getInt("warnstoban");
	}
	
	@Override
	public void onDisable() {
	}

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("warn")){
    		if(args.length >= 2) {
                  getConfig();
                  saveConfig();
                  Player warnedplayer = this.getServer().getPlayer(args[0]);
                  String reason = "";
                  for(int i = 1; i < args.length; i++) {
                	  reason = reason + args[i] + " ";
                  }
                  System.out.print("Player " + args[0] + " was warned!");
                  
                  List<String> warnings = getConfig().getStringList("warnings.warningsof" + args[0]);
                  warnings.add(sender.getName() + ":" + reason);
                  
                  try {
                	  warnedplayer.sendMessage(ChatColor.YELLOW + "[Warnung] You were warned: " + reason);
                  }
                  catch(Exception e) {
                  }
                  sender.sendMessage(ChatColor.YELLOW + "[Warning] Player " + args[0] + " was warned!");
                  getConfig().set("warnings.warningsof" + args[0], warnings);
                  saveConfig();
                  
                  if(ban) {
                	  if(getConfig().getList("warnings.warningsof" + args[0]).size() >= warnstoban) {
                		  try {
                			  warnedplayer.kickPlayer("Ban - You have recieved too many warnings: " + reason);
                		  }
                	  	  catch(Exception e) { }
                		  getServer().dispatchCommand(getServer().getConsoleSender(), getConfig().getString("bancommand") + " " + args[0] + " 3 warnings");
                	  }
                  }
                  
                  if(kickonwarn) {
                	  try {
                		  warnedplayer.kickPlayer("You recieved a warning: " + reason);
                	  }
                	  catch(Exception e) { }
                  }
                  
                  return true;
    		}
    		else {
    			sender.sendMessage("Usage: /warn [Player] <Reason>");
    			return true;
    		}
    	}
    	
    	
    	else if(cmd.getName().equalsIgnoreCase("warnings")) {
    		if(args.length == 1) {
    			if(!sender.hasPermission("frostwarn.warnings.others")) {
    				sender.sendMessage("No permission!");
    				return true;
    			}
    			String player = args[0];
    			List<String> warnings = getConfig().getStringList("warnings.warningsof" + player);
    			for(int i=0; i < warnings.size(); i++) {
    				String content = warnings.get(i);
    				String by = content.split(":")[0];
    				String reason = content.split(":")[1];
    				sender.sendMessage("Warning " + (i+1) + " by " + by + ": " + reason);
    			}
    			return true;
    		}
    		else if(args.length == 0) {
    			String player = sender.getName();
    			List<String> warnings = getConfig().getStringList("warnings.warningsof" + player);
    			sender.sendMessage("Your warnings:");
    			for(int i=0; i < warnings.size(); i++) {
    				String content = warnings.get(i);
    				String by = content.split(":")[0];
    				String reason = content.split(":")[1];
    				sender.sendMessage("Warning " + (i+1) + " by " + by + ": " + reason);
    			}
    			return true;
    		}
    		else {
    			sender.sendMessage("Usage: /warnings [Player]");
    		}
    	}
    	
    	
    	else if(cmd.getName().equalsIgnoreCase("clearwarns")) {
    		if(args.length == 1) {
    			String player = args[0];
    			getConfig().set("warnings.warningsof" + player, Arrays.asList());
    			saveConfig();
    			sender.sendMessage(player + "'s warnings cleared!");
    			return true;
    		}
    	}
    	
    	
		return false;
    }
	
	


	
	
}
