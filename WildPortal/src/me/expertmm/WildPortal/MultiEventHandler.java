/**
 * @author Jacob Gustafson
 *
 */
package me.expertmm.WildPortal;

/*

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
*/
//this paradigm requires:
//import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.event.player.PlayerShearEntityEvent;
//import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.event.player.PlayerQuitEvent; //if you want to do public void onQuit9PlayerQuitEvent event) {}
import org.bukkit.configuration.file.FileConfiguration;

import me.expertmm.WildPortal.WildPortalPortal;














//this specific plugin requires:
//import org.bukkit.Server;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
//import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;

import com.google.common.base.Strings;



//import java.awt.Color;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.ByteArrayInputStream;
//import java.io.FileInputStream;
//import java.io.FileWriter;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.io.StringWriter;
//utilities:
import java.util.Random;
import java.util.List;
import java.util.ArrayList;//implements list (instantiate List objects with this)
//import java.util.ListIterator;
import java.util.logging.Logger;


public class MultiEventHandler implements CommandExecutor, Listener {
//public class ClickListener extends JavaPlugin implements Listener {
	
	//NOTE: you canNOT do getConfig from here, nor make this extend JavaPlugin
	
	//Thanks to Both class by JPG2000F on forums.bukkit.org
	
	//static Player thisPlayer = null;
	public static String usagePlayerMsg=ChatColor.YELLOW+"/wildportal return"+ChatColor.WHITE+": return to your personal wild location where the wildportal initially dropped you";
	public static String usageManageMsg=ChatColor.YELLOW+"/wildportal"+ChatColor.WHITE+": creates portal from next punched sign,\n" + 
	                                    ChatColor.YELLOW+"/wildportal remove"+ChatColor.WHITE+": remove portal status from next punched sign,\n" + 
	                                    ChatColor.YELLOW+"/wildportal select"+ChatColor.WHITE+": select (use before commands listed next...),\n" + 
	                                    ChatColor.YELLOW+"/wildportal set destination center <coordinates>"+ChatColor.WHITE+": modify portal rect so center is at specified location--can be x,z or x,y,z or world,x,y,z but y will be ignored and automatically found for wild (inhabitable) destination.\n" +
	                                    ChatColor.YELLOW+"/wildportal set destination width|depth <value>"+ChatColor.WHITE+": (only type width or depth not both) modify portal rect's x width or z depth (automatically size from center, though stored x and z are minimums).\n" +
	                                    ChatColor.YELLOW+"/wildportal get destination"+ChatColor.WHITE+": See destination of current wildportal.\n" +
	                                    ChatColor.YELLOW+"/wildportal set return <player> null"+ChatColor.WHITE+": clear return location of player\n" +
	                                    ChatColor.YELLOW+"/wildportal verbose true|false"+ChatColor.WHITE+": reload data files (other than config) from drive, ignoring loaded data\n" +
	                                    ChatColor.YELLOW+"/wildportal reload"+ChatColor.WHITE+": reload data files (other than config) from drive, ignoring loaded data\n" +
	                                    ChatColor.YELLOW+"/wildportal save"+ChatColor.WHITE+": overwrite data files (other than config) with currently loaded data\n" +
	                                    ChatColor.YELLOW+"/wildportal help"+ChatColor.WHITE+": show this help screen";
	private main plugin = null; //public main InstanceOfMain = null; 
	private Logger thisLogger = null;
	private Server thisServer = null;
	private FileConfiguration config = null;
	private WildPortalData data = null;
	//private WildPortalPortal LastCreatedWildPortal = null;
	private Location LastCreatedWildPortalFromPlayerLocation = null;
	private String selectedWildPortalWorldName = null;
	private String selectedWildPortalID = null;
	
	private static String ConfigVariable_LastCreatedInWorld_VariableName="LastCreatedInWorld";
	
	public List<String> aboutToMakeTeleporterWithNextClickPlayerList = null;
	public List<String> aboutToBreakTeleporterWithNextClickPlayerList = null;
	public List<String> aboutToSelectTeleporterWithNextClickPlayerList = null;
	//private static String AuthorDebugWorldBeingUsedByPlayerName="Abiyahh";
	//public static String AuthorDebugWorld="WorldLand";
	//private static String csvFileFullName = null;
	//private static String csvFileName="WildPortal.csv";
	private static String ConfigVariable_IsVerbose_VariableName="Debug.IsVerbose";
	
	static Material LastUsedMaterial = Material.AIR;
	static Material LastCheckedMaterial = Material.AIR;
	static int LastUsedY = -1;
	static int StartAboveGroundByInt = 10;
	
	
	//TEKKIT block ids (from 1.2.5--NOT applicable to 1.5.2 such as Hexxit):
	static boolean IsVerbose=false;
	static int blockid_air=0;
	static int blockid_stone=1;
	static int blockid_grass=2;
	static int blockid_flowing_water=8;
	static int blockid_water=9;
	static int blockid_flowing_lava=10;
	static int blockid_lava=11;
	static int blockid_sand=12;
	static int blockid_web=30;
	static int blockid_tnt=46;
	static int blockid_fire=51;
	static int blockid_ice=79;
	static int blockid_snow=80;
	static int blockid_mycel=110;
	static int blockid_standingsign=63;
	static int blockid_wallsign=68;
	//static Location LastCreatedSignLocation = null;

	// Minecraft 1.7+:			
	//int blockid_tripwire=132;
	// Minecraft 1.8+(or so):
	//int blockid_barrier=166;//slashed circle or not shown
	
	
	//constructor
    public MultiEventHandler() {
    	String participle="before initializing";
    	try {
    		participle="getting plugin";
	    	this.plugin=main.getPlugin();
	    	participle="getting server";
	    	thisServer=plugin.getServer();
	    	participle="getting logger";
	    	thisLogger=plugin.getLogger();
	    	thisLogger.info("Loading WildPortal "+main.myVersionString+"...");
	    	participle="getting config";
	    	config=plugin.getConfig();
	    	participle="getting plugin data folder";
	    	File dataFolder = plugin.getDataFolder();
	    	participle="making plugin directory";
	    	dataFolder.mkdirs();
	    	participle="creating data files";
	    	data=new WildPortalData(dataFolder);
	    	
	    	//participle="getting plugin data directory";
	    	//File folderOfThisPluginFullName=new File(dataFolder.getAbsolutePath()+"\\"+csvFileName);//FAILS: new File(dataFolder.getPath()+"/"+csvFileName);
	    	//participle="making plugin data directory";
	    	//folderOfThisPluginFullName.mkdirs();
	    	participle="generating full csv file path";
	    	//Path csvPath=Paths.get(dataFolder.getAbsolutePath(), csvFileName);
	    	//csvFileFullName=csvPath.toString();  // data.dataFolder.getAbsolutePath() +"/"+csvFileName;
	    	participle="checking config";
	    	if (config!=null) {
				//main.config.addDefault(ConfigVariable_IsVerbose_VariableName, "False");
	    		if (!config.contains(ConfigVariable_IsVerbose_VariableName)) config.set(ConfigVariable_IsVerbose_VariableName, IsVerbose);
	    		//LastCreatedWildPortal stuff is loaded from a player so that context of world is used
				MultiEventHandler.IsVerbose=config.getBoolean(ConfigVariable_IsVerbose_VariableName);
	    	}
	    	else {
	    		main.logWriteLine("ERROR: MultiEventHandler constructor could not load config variables because main.config was null");
	    	}
	    	participle="making lists";
			if (aboutToMakeTeleporterWithNextClickPlayerList==null) aboutToMakeTeleporterWithNextClickPlayerList=new ArrayList<String>();
			if (aboutToBreakTeleporterWithNextClickPlayerList==null) aboutToBreakTeleporterWithNextClickPlayerList=new ArrayList<String>();
			if (aboutToSelectTeleporterWithNextClickPlayerList==null) aboutToSelectTeleporterWithNextClickPlayerList=new ArrayList<String>();
    	}
    	catch (Exception e) {
    		this.thisLogger.info("Could not finish "+participle+" in MultiEventHandler constructor: "+e.getMessage());
    	}
    }
    
    private String ConfigVariable_LastCreated_CategoryName="LastCreatedWildPortal";//world name
    private String ConfigVariable_LastCreatedFromWorld_SubCategoryName="<this>";//world name
    private String ConfigVariable_LastCreatedFromX_VariableName="LastCreatedFromX";
    private String ConfigVariable_LastCreatedFromY_VariableName="LastCreatedFromY";
    private String ConfigVariable_LastCreatedFromZ_VariableName="LastCreatedFromZ";
    
    public void SaveOptionsToConfig() {
    	if (plugin!=null) {
    		if (config!=null) {
    			if (LastCreatedWildPortalFromPlayerLocation!=null) {
    				config.set(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedInWorld_VariableName, LastCreatedWildPortalFromPlayerLocation.getWorld().getName());
    				ConfigVariable_LastCreatedFromWorld_SubCategoryName=LastCreatedWildPortalFromPlayerLocation.getWorld().getName();
    				config.set(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromX_VariableName, LastCreatedWildPortalFromPlayerLocation.getBlockX());
    				config.set(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromY_VariableName, LastCreatedWildPortalFromPlayerLocation.getBlockY());
    				config.set(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromZ_VariableName, LastCreatedWildPortalFromPlayerLocation.getBlockZ());
    			}
    			config.set(ConfigVariable_IsVerbose_VariableName, IsVerbose);
    		}
    		else if (thisLogger!=null) thisLogger.info("ERROR in SaveOptionsToConfig: config was null");
    		plugin.saveConfig(); //main.saveThisConfig();
    		data.save();
    	}
    	else {
    		if (thisLogger!=null) thisLogger.info("ERROR in SaveOptionsToConfig: plugin was null!");
    	}
    }
	
	
	/* 
	 below is from  Plo124. "On punch block, send a message?" Bukkit.org. <https://forums.bukkit.org/threads/on-punch-block-send-a-message.177435/> 25 Sep 2013. 21 Oct 2014.
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(event.getMaterial() == Material.GOLD_BLOCK && e.getAction() == Action.LEFT_CLICK_BLOCK){
			player.sendMessage("Stop breaking the budder >:D")
		}
	}
	*/	

	//implementations:
	
	
	
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent event) { //formerly onClickEvent(PlayerInteractEvent event) {
		//boolean IsGood=true;
		Player player = event.getPlayer();
		World world = player.getWorld();
		Action action = event.getAction();
		
		///NOTE: in 1.7+ (ID doesn't exist, and) you do stuff like:
		//Material[] blacklist = {Material.OBSIDIAN, Material.GLASS, Material.GRASS};
		//@EventHandler (priority = EventPriority.NORMAL)
		//public void onBlockBreak (BlockBreakEvent event) {
		//	for (Material blacklisted : blacklist) {
	    //    if (event.getBlock().getType() == blacklisted) {
	    //        event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't break that block!");
		//        event.setCancelled(true);
	    //    }
	    //	}
		//}
		
		if (action==Action.LEFT_CLICK_BLOCK) {
			Block clickedBlock = event.getClickedBlock();
			Location clickedLocation = clickedBlock.getLocation();
			if (clickedBlock.getTypeId()==blockid_wallsign
				|| clickedBlock.getTypeId()==blockid_standingsign) {
				//if (aboutToMakeTeleporterWithNextClickPlayerList==null) aboutToMakeTeleporterWithNextClickPlayerList=new ArrayList<String>();
				//if (aboutToBreakTeleporterWithNextClickPlayerList==null) aboutToBreakTeleporterWithNextClickPlayerList=new ArrayList<String>();
				if (aboutToMakeTeleporterWithNextClickPlayerList.contains(player.getName())) {//make WildPortal instead (aboutToMakeTeleporterWithNextClickPlayerList.contains(player.getName()))
					aboutToMakeTeleporterWithNextClickPlayerList.remove(player.getName());
					selectedWildPortalID=null;
					selectedWildPortalWorldName=null;
					boolean IsGood=addWildPortal(clickedLocation, world.getName(), "<wild>",-500,-500,1000,1000,true);
					if (IsGood) {
						player.sendMessage(ChatColor.GREEN+"Created WildPortal");
						LastCreatedWildPortalFromPlayerLocation=player.getLocation();
						SaveOptionsToConfig();
						data.save();
						//DumpWildPortalList();
						if (!Strings.isNullOrEmpty(selectedWildPortalID)) {
							if (!Strings.isNullOrEmpty(selectedWildPortalWorldName)) {
								player.sendMessage(ChatColor.GRAY+"selected wildportal "+selectedWildPortalWorldName+"."+selectedWildPortalID);
							}
							else {
								player.sendMessage(ChatColor.GRAY+"selected wildportal <null>."+selectedWildPortalID);
							}
						}
					}
					else {
						player.sendMessage(ChatColor.RED+"Failed to create WildPortal. That location may already be a WildPortal.");
					}
				}
				else if (aboutToBreakTeleporterWithNextClickPlayerList.contains(player.getName())) {
					aboutToBreakTeleporterWithNextClickPlayerList.remove(player.getName());
					String doneLocationString = clickedBlock.getLocation().toString();
					String prevSelectedID=selectedWildPortalID;
					String prevSelectedWorldName=selectedWildPortalWorldName;
					boolean IsRemoved=data.deletePortalData(player.getWorld().getName(),WildPortalPortal.getIDFromLocation(clickedBlock.getLocation()));
					//clickedBlock.breakNaturally();
					if (IsRemoved) {
						player.sendMessage(ChatColor.GREEN+"Block at "+doneLocationString+" is no longer a WildPortal.");
						if (!prevSelectedID.equals(selectedWildPortalID) || !prevSelectedWorldName.equals(selectedWildPortalWorldName)) {
							if (Strings.isNullOrEmpty(selectedWildPortalID) || Strings.isNullOrEmpty(selectedWildPortalWorldName)) player.sendMessage(ChatColor.GRAY+"There is now no WildPortal selected.");
							else player.sendMessage(ChatColor.GRAY+"WildPortal at "+selectedWildPortalWorldName+"."+selectedWildPortalID.replace("c", " ").replace("_", "-")+" is now selected.");
							//else player.sendMessage(ChatColor.GRAY+"WildPortal at "+WildPortalPortal.getFromConfigurationSection(data.getPortalSectionByID(player.getWorld().getName(), selectedWildPortalID), "portals.yml").toString_SignPointOnly()+" is now selected.");
						}
					}
					else player.sendMessage(ChatColor.RED+"Block at "+doneLocationString+" was not a WildPortal or failed to be removed.");
				}
				else if (aboutToSelectTeleporterWithNextClickPlayerList.contains(player.getName())) {
					aboutToSelectTeleporterWithNextClickPlayerList.remove(player.getName());
					String wpid=WildPortalPortal.getIDFromLocation(clickedBlock.getLocation());
					if (data.isPortal(clickedBlock.getLocation())) {
						selectedWildPortalID=wpid;
						selectedWildPortalWorldName=clickedBlock.getLocation().getWorld().getName();
						//clickedBlock.breakNaturally();
						player.sendMessage(ChatColor.GREEN+"Block at "+clickedBlock.getLocation().toString()+" (wildportal "+selectedWildPortalWorldName+"."+selectedWildPortalID+" is now selected.");
					}
					else player.sendMessage(ChatColor.RED+"Block at "+clickedBlock.getLocation().toString()+" was not a WildPortal or failed to be selected.");
				}
				else { //use the portal
					if (IsVerbose) main.logWriteLine("Checking whether sign is a WildPortal...");
					String wpid=WildPortalPortal.getIDFromLocation(clickedBlock.getLocation());
					if (wpid!=null) { //if (wildportalListContainsLocation(clickedBlock.getLocation())) {
						//ConfigurationSection wpCS = data.getPortalSectionByID(player.getWorld().getName(), wpid);
						if (IsVerbose) main.logWriteLine("Yes (is WildPortal).");
						GroundRect rect=data.getPortalDestination(player.getWorld().getName(), clickedBlock.getLocation());
						doWildPortal(player,rect);
					}
					else {
						if (IsVerbose) main.logWriteLine("No (not WildPortal).");
					}
				}
			}//if standing sign or wall sign
			else {
				if (aboutToMakeTeleporterWithNextClickPlayerList!=null
						&& aboutToMakeTeleporterWithNextClickPlayerList.contains(player.getName())) {
					aboutToMakeTeleporterWithNextClickPlayerList.remove(player.getName());
					player.sendMessage(ChatColor.RED+"You can only make a (standing or wall-mounted) sign into a WildPortal.");
				}
			}
		}//else not a block click action (may be action==Action.LEFT_CLICK_AIR)
		//return IsGood;
	}//end onInteractEvent
	
	public void usage(Player player, Boolean badCommandMsgEnable) {
		if (player.hasPermission("wildportal.manage")) {
			player.sendMessage(ChatColor.WHITE+"WildPortal "+main.myVersionString);
		}
		if (badCommandMsgEnable) player.sendMessage(ChatColor.RED+"Unknown WildPortal parameters. Try:");
		player.sendMessage(usagePlayerMsg);
		if (player.hasPermission("wildportal.manage")) {
			player.sendMessage(usageManageMsg);
			player.sendMessage("(press 't' then scroll up with scroll wheel to see previous lines you may have missed)");
		}
	}
	public static String getPrettyLocationString(Location location) {
		String result="null";
		if (location!=null) result=location.toString().replace(",",", ");
		return result;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		//PluginManager plg = Bukkit.getPluginManager(); //Assigns plg to Bukkit.getPluginManager()
		//Plugin plgname = main.getPlugin();//plg.getPlugin("WildPortal");
		if (IsVerbose) main.logWriteLine("getting player");
		Player player = ((Player) sender);
		//thisPlayer = player;
		if (IsVerbose) main.logWriteLine("getting world");
		//World world = player.getWorld();
		
		if (IsVerbose) main.logWriteLine("about to call IsWildPortalListLoaded");
		//if (!IsWildPortalListLoaded()) {
		//	if (IsVerbose) main.logWriteLine("about to call IsWildPortalListLoaded");
		//	doLoadWildPortalData();
		//}
		
		if (IsVerbose) main.logWriteLine("about to check whether command is wildportal");
		if (cmd.getName().equalsIgnoreCase("wildportal")) { // If the player typed /wildportal then do the following...
			if (IsVerbose) main.logWriteLine("about to check whether sender is a player");
			if (sender instanceof Player) {
				if (IsVerbose) main.logWriteLine("about to check args!=null && args.length>0");
				//if(args.length==0){sender.sendMessage(ChatColor.AQUA + "For help, use /wildportal help");}
				if (args!=null && args.length>0) {
					if (IsVerbose) main.logWriteLine("about to check whether args[0] is return");
					if (args[0].equalsIgnoreCase("return")) {
						if (IsVerbose) main.logWriteLine("about to check whether player has WildPortalReturn permission");
						if (player.hasPermission("wildportal.return")){
							Location returnLocation=null;
							returnLocation = data.getPlayerReturnLocation(player.getName(), thisServer);
							if (returnLocation!=null) {
								player.teleport(returnLocation);
								//Boolean IsDeleted=data.deletePlayerData(player.getName());
								//if (IsDeleted && IsVerbose) main.logWriteLine("Deleted old return location since returned to spawn");
							}
							else {
								boolean IsReturned=false;
								if (player.hasPermission("wildportal.manage")) { //need wildportal.manage to use a default portal
									if (LastCreatedWildPortalFromPlayerLocation!=null) {
										if (player.hasPermission("wildportal.manage")) { //need wildportal.manage to use a default portal
											player.teleport(LastCreatedWildPortalFromPlayerLocation);
											player.sendMessage(ChatColor.GRAY+"You have to use a WildPortal first during this run of the server in order to go back to where you were.");
											player.sendMessage(ChatColor.GRAY+"Since you have wildportal.manage, you have been sent to where player was standing who created the last-created WildPortal"+ChatColor.YELLOW+" (used location stored from this run of the server).");
											IsReturned=true;
										}
									}
									else if (config.contains(ConfigVariable_LastCreated_CategoryName)) {
						    			if (player.hasPermission("wildportal.manage")) {
						    				String config_returnWorldName=null;
						    				World config_returnWorld=null;
						    				boolean IsCompleteReturnData=true;
						    				config_returnWorldName=config.getString(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedInWorld_VariableName);
						    				ConfigVariable_LastCreatedFromWorld_SubCategoryName=config_returnWorldName;
						    				if (config_returnWorldName!=null) {
						    					config_returnWorld=thisServer.getWorld(config_returnWorldName);
						    					if (config_returnWorld!=null) {
						    						if (!config.contains(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromX_VariableName)) IsCompleteReturnData=false;
						    						if (!config.contains(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromY_VariableName)) IsCompleteReturnData=false;
						    						if (!config.contains(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromZ_VariableName)) IsCompleteReturnData=false;
						    						if (IsCompleteReturnData) {
									    				int config_returnX=config.getInt(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromX_VariableName);
									    				int config_returnY=config.getInt(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromY_VariableName);
									    				int config_returnZ=config.getInt(ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedFromWorld_SubCategoryName+"."+ConfigVariable_LastCreatedFromZ_VariableName);
									    				LastCreatedWildPortalFromPlayerLocation = new Location(config_returnWorld, (double)config_returnX, (double)config_returnY, (double)config_returnZ);
									    				player.teleport(LastCreatedWildPortalFromPlayerLocation);
									    				player.sendMessage(ChatColor.GRAY+"You have to use a WildPortal first during this run of the server in order to go back to where you were.");
														player.sendMessage(ChatColor.GRAY+"Since you have wildportal.manage, you have been sent to where player was standing who created the last-created WildPortal"+ChatColor.YELLOW+" (loaded location from config).");
									    				IsReturned=true;
						    						}
						    						else {
						    							thisLogger.info("Not all coordinates were in config for /wildportal return, though the world from config named "+config_returnWorldName+" currently exists on the server.");
						    						}
						    					}
						    					else {
						    						IsCompleteReturnData=false;
						    						thisLogger.info("The world named "+config_returnWorldName+" for /wildportal return was not found on the server, though a "+ConfigVariable_LastCreated_CategoryName+"."+ConfigVariable_LastCreatedInWorld_VariableName+" exists.");
						    					}
						    					//else thisLogger.info("The world name "++" for /wildportal return was not found");
						    				}
						    				else {
						    					IsCompleteReturnData=false;
						    					thisLogger.info(ConfigVariable_LastCreated_CategoryName+" exists for /wildportal return, but the world name was null");
						    				}
						    			}
						    		}
								}
								/*
								if (!IsReturned && player.getName().equals(AuthorDebugWorldBeingUsedByPlayerName)) {//NOTE: == does NOT work since that is the identity operator
									if (IsVerbose) main.logWriteLine("about to create teleport destination for player who is using author's debug world");
									returnLocation = new Location(world,-143.0,80.0,263.0);
									if (IsVerbose) main.logWriteLine("about to teleport player who is using author's debug world");
									player.teleport(returnLocation);
									IsReturned=true;
								}
								*/
								
								if (!IsReturned) {
									player.sendMessage(ChatColor.RED+"You have to use a WildPortal first.");
									player.sendMessage(ChatColor.GRAY+"This command goes back to last used WildPortal.");
								}
							}//else player does not have own return location
						}
						else {
							player.sendMessage(ChatColor.RED+"wildportal return is not available to your account (or rank) on this server.");
						}
					}
					else if (args[0].equalsIgnoreCase("remove")) {
						if (player.hasPermission("wildportal.manage")){
							//if (aboutToBreakTeleporterWithNextClickPlayerList==null) aboutToBreakTeleporterWithNextClickPlayerList=new ArrayList<String>();
							if (!aboutToBreakTeleporterWithNextClickPlayerList.contains(player.getName())) aboutToBreakTeleporterWithNextClickPlayerList.add(player.getName());
							player.sendMessage(ChatColor.AQUA+"Click a sign to remove the WildPortal");
							player.sendMessage(ChatColor.GRAY+"(will not destroy the sign)...");
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else if (args[0].equalsIgnoreCase("select")) {
						if (player.hasPermission("wildportal.manage")){
							if (!aboutToSelectTeleporterWithNextClickPlayerList.contains(player.getName())) aboutToSelectTeleporterWithNextClickPlayerList.add(player.getName());
							player.sendMessage(ChatColor.AQUA+"Click a sign to select the WildPortal");
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else if (args[0].equalsIgnoreCase("set") && args.length>1 && args[1].equalsIgnoreCase("return")) {
						if (player.hasPermission("wildportal.manage")){
							//wildportal set return <player> null
							//wildportal 0=set 1=return 2=<player> 3=null
							if (args.length>3) {
								//Player modifyPlayer=thisServer.getPlayer(args[2]);
								Location thisLocation=data.getPlayerReturnLocation(args[2], thisServer);
								if (args[3].equalsIgnoreCase("null")) {
									if (thisLocation!=null) {
										data.deletePlayerData(args[2]);
										thisLocation=data.getPlayerReturnLocation(args[2], thisServer);
										player.sendMessage(ChatColor.GREEN+" player "+args[2]+" return location is now "+getPrettyLocationString(thisLocation));
									}
									else {
										player.sendMessage(ChatColor.RED+" player "+args[2]+" does not yet have a return location.");;
									}
								}
								else player.sendMessage(ChatColor.RED+" you must specify playername and value (only null is implemented) after set return");;
							}
							else player.sendMessage(ChatColor.RED+" you must specify playername and null after set return");;
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else if (args[0].equalsIgnoreCase("set") && args.length>1 && args[1].equalsIgnoreCase("destination")) {
						if (player.hasPermission("wildportal.manage")) {
							if (!Strings.isNullOrEmpty(selectedWildPortalID)) {
								if (args.length>2 && args[2].equalsIgnoreCase("center")) {
									if (args.length>3) {
										String[] coords_strings=args[3].split(",");
										if (coords_strings!=null) {
											Boolean IsChanged=false;
											if (coords_strings.length==4) {
												//parseInt(String) returns a primitive int, valueOf(String) returns new Integer()
												//this.wildportalList.get(selectedWildPortalIndex).SetDestAsWild(coords_strings[0],Integer.parseInt(coords_strings[1]), Integer.parseInt(coords_strings[3]));
												//Location location = WildPortalPortal.getLocationFromID(player.getWorld(), selectedWildPortalID);
												int newCenterX=Integer.parseInt(coords_strings[1]);
												int newCenterZ=Integer.parseInt(coords_strings[3]);
												data.setPortalDestinationCenter(selectedWildPortalWorldName, selectedWildPortalID, newCenterX, newCenterZ);
												//NOTE: skip [2] since Y is not specified when wild
												//NOTE: skip [0] since that is world name
												//wildportalList[selectedWildPortalIndex].X=thisX;
												//wildportalList[selectedWildPortalIndex].Z=thisZ;
												IsChanged=true;
											}
											else if (coords_strings.length==3) {
												//Location location = WildPortalPortal.getLocationFromID(player.getWorld(), selectedWildPortalID);
												int newCenterX=Integer.parseInt(coords_strings[0]);
												int newCenterZ=Integer.parseInt(coords_strings[2]);
												//this.wildportalList.get(selectedWildPortalIndex).SetDestAsWild(this.wildportalList.get(selectedWildPortalIndex).getDestinationWorldName(), Integer.parseInt(coords_strings[0]), Integer.parseInt(coords_strings[2]));
												data.setPortalDestinationCenter(selectedWildPortalWorldName, selectedWildPortalID, newCenterX, newCenterZ);
												//NOTE: skip [1] since Y is not specified when wild
												IsChanged=true;
											}
											else if (coords_strings.length==2) {
												//Location location = WildPortalPortal.getLocationFromID(player.getWorld(), selectedWildPortalID);
												int newCenterX=Integer.parseInt(coords_strings[0]);
												int newCenterZ=Integer.parseInt(coords_strings[1]);
												//this.wildportalList.get(selectedWildPortalIndex).SetDestAsWild(this.wildportalList.get(selectedWildPortalIndex).getDestinationWorldName(), Integer.parseInt(coords_strings[0]), Integer.parseInt(coords_strings[1]));
												data.setPortalDestinationCenter(selectedWildPortalWorldName, selectedWildPortalID, newCenterX, newCenterZ);
												IsChanged=true;
											}
											else {
												player.sendMessage(ChatColor.RED+"no valid coordinates specified after center: try center <x>,<y>,<z> or center <worldname>,<x>,<y>,<z>");
											}
											
											if (IsChanged) {
												//NOTE: setPortalDestinationCenter DOES automatically save
												Location destLocation=data.getDestinationCenterIfPortalElseNull(thisServer.getWorld(selectedWildPortalWorldName), selectedWildPortalID, thisServer);
												player.sendMessage(ChatColor.GREEN+"Destination of selected WildPortal is now "+getPrettyLocationString(destLocation));
												GroundRect rect=data.getPortalDestination(selectedWildPortalWorldName, selectedWildPortalID);
												if (rect!=null) player.sendMessage(ChatColor.GRAY+" new rectangle: "+rect.toString());
												else player.sendMessage(ChatColor.RED+" ERROR: new rectangle is null");
												player.sendMessage(ChatColor.GRAY+" (selected WildPortal is at "+((selectedWildPortalID!=null)?selectedWildPortalID:"null")+")");
											}
										}
										else {
											player.sendMessage(ChatColor.RED+"no valid coordinates specified after center: try center <x>,<y>,<z> or center <worldname>,<x>,<y>,<z>");
										}
									}
									else player.sendMessage(ChatColor.RED+"no coordinates specified after center");
								}
								else if (args.length>2 && args[2].equalsIgnoreCase("width")) {
									if (args.length>3) {
										int newSize=Integer.parseInt(args[3]);
										data.setPortalDestinationWidth(selectedWildPortalWorldName, selectedWildPortalID, newSize);
										GroundRect rect=data.getPortalDestination(selectedWildPortalWorldName, selectedWildPortalID);
										player.sendMessage(ChatColor.GREEN+"Destination of selected WildPortal is now "+((rect!=null)?rect.toString(","):"null"));
									}
									else player.sendMessage(ChatColor.RED+"no value specified after "+args[2]);
								}
								else if (args.length>2 && args[2].equalsIgnoreCase("depth")) {
									if (args.length>3) {
										int newSize=Integer.parseInt(args[3]);
										data.setPortalDestinationDepth(selectedWildPortalWorldName, selectedWildPortalID, newSize);
										GroundRect rect=data.getPortalDestination(selectedWildPortalWorldName, selectedWildPortalID);
										player.sendMessage(ChatColor.GREEN+"Destination of selected WildPortal is now "+((rect!=null)?rect.toString(","):"null"));
									}
									else player.sendMessage(ChatColor.RED+"no value specified after "+args[2]);
								}
								else {
									player.sendMessage(ChatColor.RED+"WildPortal set commands available: /wildportal set destination center <x>,<z> [or <x>,<y>,<z>]");
								}
							}
							else {
								player.sendMessage(ChatColor.RED+"No WildPortal is selected. First try /wildportal select then punching a node");								
							}
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else if (args[0].equalsIgnoreCase("get") && args.length>1 && args[1].equalsIgnoreCase("destination")) {
						if (player.hasPermission("wildportal.manage")) {
							GroundRect rect=data.getPortalDestination(player.getWorld().getName(), selectedWildPortalID);
							if (rect!=null) {
								if (args.length>2 && args[2].equalsIgnoreCase("center")) {
									player.sendMessage(ChatColor.WHITE+"WildPortal destination center: " + rect.getCenterString());
								}
								else {
									player.sendMessage(ChatColor.WHITE+"WildPortal destination: " + rect.toString());
								}
								if (rect.key!=null) player.sendMessage(" keyword: "+ChatColor.WHITE+rect.key);
							}
							else {
								player.sendMessage(ChatColor.RED+"No WildPortal is selected. First try /wildportal select then punching a node");								
							}
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else if (args[0].equalsIgnoreCase("verbose")) {
						if (player.hasPermission("wildportal.manage")){
							boolean previousIsVerbose=IsVerbose;
							if (args.length>1) {
								IsVerbose=FrameworkDummy.Convert_ToBoolean(args[1]);
								player.sendMessage(ChatColor.GREEN+"WildPortal verbose debugging mode set to "+(IsVerbose?ChatColor.WHITE+"ON":ChatColor.RED+"OFF"));
							}
							else {
								IsVerbose=!IsVerbose;
								player.sendMessage(ChatColor.GREEN+"WildPortal verbose debugging mode toggled "+(IsVerbose?ChatColor.WHITE+"ON":ChatColor.RED+"OFF"));
							}
							if (previousIsVerbose!=IsVerbose) SaveOptionsToConfig();
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else if (args[0].equalsIgnoreCase("reload")) {
						if (player.hasPermission("wildportal.manage")){
							data.load();
							player.sendMessage(ChatColor.GREEN+"WildPortal settings have been reloaded.");
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else if (args[0].equalsIgnoreCase("save")) {
						if (player.hasPermission("wildportal.manage")){
							data.save();
							player.sendMessage(ChatColor.GREEN+"WildPortal settings have been saved.");
						}
						else player.sendMessage(ChatColor.RED+"You lack the necessary permission for WildPortal "+args[0]);
					}
					else {
						usage(player, !args[0].equalsIgnoreCase("help"));
					}
				}
				else {
					if (IsVerbose) main.logWriteLine("about to check whether player has create wildportal permission");
					if (player.hasPermission("wildportal.manage")){
						//if (aboutToMakeTeleporterWithNextClickPlayerList==null) aboutToMakeTeleporterWithNextClickPlayerList=new ArrayList<String>();
						if (!aboutToMakeTeleporterWithNextClickPlayerList.contains(player.getName())) aboutToMakeTeleporterWithNextClickPlayerList.add(player.getName());
						player.sendMessage(ChatColor.AQUA+"Click a sign to make it into a WildPortal");
						player.sendMessage(ChatColor.GRAY+"(Its text can say anything and won't be changed)...");
					}
					else {
						player.sendMessage(ChatColor.RED+"WildPortal: Sorry you do not have the necessary permission to create a WildPortal.");
					}
				}//end else no args
			}
			else {
				//sender.sendMessage
				main.logWriteLine("WildPortal: I think the SYSTEM is talking to me.");
			}
		}
		return true;
	}//end onCommand
	
	
	//general methods:
	
	//public void DumpWildPortalList() {
		
	//}

	public boolean addWildPortal(Location thisSignLocation, String destinationWorldName, String destinationKeyword, int dest_X, int dest_Y, int dest_Width, int dest_Depth, Boolean select_enable) {
		boolean IsGood=false;
		try {
			if (!data.isPortal(thisSignLocation)) {
				//WildPortalPortal newWildportalportal = new WildPortalPortal(thisSignLocation.getBlockX(), thisSignLocation.getBlockY(), thisSignLocation.getBlockZ(), thisSignLocation.getWorld().getName(), destinationWorldName,destinationKeyword,dest_X,dest_Y,dest_Width,dest_Depth);
				GroundRect destGroundRect=new GroundRect(dest_X, dest_Y, dest_Width, dest_Depth);
				destGroundRect.worldName=destinationWorldName;
				data.createPortalToWild(thisSignLocation, "<wild>", destGroundRect);
				if (select_enable) {
					selectedWildPortalID=WildPortalPortal.getIDFromLocation(thisSignLocation);
					selectedWildPortalWorldName=thisSignLocation.getWorld().getName();
				}
				IsGood=true;
		    	if (IsVerbose) {
					main.logWriteLine("verbose message: created WildPortal from sign at "+getPrettyLocationString(thisSignLocation)+".");
		    	}
		    	//NOTE: createPortalToWild DOES automatically save
			}
			else {
				main.logWriteLine("WARNING: was already a WildPortal--sign at "+getPrettyLocationString(thisSignLocation)+".");
			}
		}
		catch (Exception e) {
			IsGood=false;
			main.logWriteLine("Could not finish addWildPortal:"+e.getMessage());
		}
	 	return IsGood;
	}
/*
	private boolean wildportalListContains(WildPortalPortal thisWildPortalPortal) {
		boolean IsInList=false;
    	if (IsVerbose) {
    		main.logWriteLine("verbose message: wildportalList has "+String.valueOf(wildportalList.size())+" elements.");
    	}
		for (ListIterator<WildPortalPortal> iter = wildportalList.listIterator(); iter.hasNext(); ) {
			WildPortalPortal element = iter.next();
		    // 1 - can call methods of element
		    // 2 - can use iter.remove() to remove the current element from the list
		    // 3 - can use iter.add(...) to insert a new element into the list
		    //     between element and iter->next()
		    // 4 - can use iter.set(...) to replace the current element
		    //thanks Dave Newton. http://stackoverflow.com/questions/18410035/ways-to-iterate-over-a-list-in-java
		    // ...
		    if (element.equalsWildPortalSource(thisWildPortalPortal)) {
		    	//&& sourceSignInThisWorldName==element.sourceWorld ) { //NOTE: the Location has the world object
		    	IsInList=true;
		    	if (IsVerbose) {
		    		main.logWriteLine("verbose message: checking current world "+thisWildPortalPortal.sourceWorldName+" against WildPortal source "+element.getLocationCoords()+" in world \""+element.sourceWorldName+"\" was true.");
		    	}
		    	break;
		    }
		    else {
		    	if (IsVerbose) {
		    		main.logWriteLine("verbose message: checking current world "+thisWildPortalPortal.sourceWorldName+" against WildPortal source "+element.getLocationCoords()+" in world \""+element.sourceWorldName+"\" was false.");
		    	}
		    }
		}
		return IsInList;
	}//end wildportalListContains
	*/

	private void doWildPortal(Player player, GroundRect destRect) {
		Location alreadyHasLocation=data.getPlayerReturnLocation(player.getName(), thisServer);
		boolean IsTeleported=false;
		int TryCount=0;//tries so far
		if (alreadyHasLocation!=null) {
			TryCount=-1;
			player.teleport(alreadyHasLocation);
			IsTeleported=true;
		}
		else {
			//deprecated String WildPortalAdminName = main.config.getString("WildPortalAdmin.playerName");
			//deprecated main.logWriteLine("WildPortalAdmin.playerName is "+WildPortalAdminName);
			if (IsVerbose) main.logWriteLine("doWildPortal: getting rectangle");
			int minRandomX=destRect.x;
			int minRandomZ=destRect.z;
			int maxRandomX=destRect.x+destRect.width;
			int maxRandomZ=destRect.z+destRect.depth;
			int rangeX=maxRandomX-minRandomX;
			int rangeZ=maxRandomZ-minRandomZ;
			Random rnd = new Random();
			int randomCountingNumberX=0;
			int randomCountingNumberZ=0;
			int randomIntX=0;
			int randomIntZ=0;
			
			//Location sourceLocation = player.getLocation();
			
			if (IsVerbose) main.logWriteLine("doWildPortal: getting destination world");
			//World destWorld=thisWildPortal.getDestinationWorld(thisServer,player.getWorld());
			if (destRect.worldName==null || destRect.worldName.equals("<this>")) destRect.worldName=player.getWorld().getName();
			World destWorld=thisServer.getWorld(destRect.worldName);
			//Location destLocation = new Location(world, 0,68,0);
			//Location destLocation = new Location(world, sourceLocation.getX(), sourceLocation.getY()+20.0, sourceLocation.getZ() );
			Location randomLocation = null;
			double minY=49.0;
			LastUsedMaterial=Material.AIR;
			LastUsedY=-1;
			Location checkedLocation=null;
			int MaxTryCount=100;
			
			String LastYMsg="";
			while (TryCount<MaxTryCount) {
				if (IsVerbose) main.logWriteLine("doWildPortal: getting location on ground if ground is ok");
				randomCountingNumberX=rnd.nextInt(rangeX+1);//+1 since exclusive
				randomCountingNumberZ=rnd.nextInt(rangeZ+1);//+1 since exclusive
				randomIntX=randomCountingNumberX+minRandomX;//allows possibly negative (if minimum is negative)
				randomIntZ=randomCountingNumberZ+minRandomZ;//allows possibly negative (if minimum is negative)
				randomLocation = new Location(destWorld, (double)randomIntX,64.0,(double)randomIntZ);
				checkedLocation = getLocationOnGround(destWorld, randomLocation, minY, player);
				if (checkedLocation.getY()>=minY) {
					LastYMsg="try is at Y "+String.valueOf(checkedLocation.getY());
					checkedLocation.setY(checkedLocation.getY()+(double)StartAboveGroundByInt);
					//Boolean IsDeleted=data.deletePlayerData(player.getName());
					data.setPlayerReturnLocation(player.getName(),checkedLocation);
					if (thisServer!=null) thisServer.broadcastMessage(player.getName() + " is being born in "+destWorld.getName()+"...");
					int NoDamageSeconds=20;
					int TicksPerSecond=20;
					int NoDamageTicks=NoDamageSeconds*TicksPerSecond;
					if (player.getMaximumNoDamageTicks()<NoDamageTicks) player.setMaximumNoDamageTicks(NoDamageTicks);
					player.setNoDamageTicks(NoDamageTicks);//tick is 1/20th of a second (50ms)
					player.teleport(checkedLocation);
					player.sendMessage("WildPortal: now would be a good time to type /sethome");
					if (player.hasPermission("wildportal.return")) player.sendMessage("WildPortal: however, to get back here you can also type /wildportal return");
					IsTeleported=true;
					//Bukkit.broadcast(player.getName() + " has arrived.");
					if (thisServer!=null) thisServer.broadcastMessage((player.getName() + " has arrived."));
					main.logWriteLine("WildPortal destination in range ("+destRect.toString(",")+") for "+player.getName()+" was: "+getPrettyLocationString(checkedLocation));
					break;
				}
				else LastYMsg="couldn't try too low Y "+String.valueOf(checkedLocation.getY());
				TryCount++;
			}
			if (!IsTeleported) {
				player.sendMessage("WildPortal: Uh oh, portal couldn't find a suitable location for you yet--tried "+String.valueOf(TryCount)+"times--last try was at or above ("+getPrettyLocationString(checkedLocation)+") [last Y:"+String.valueOf(LastUsedY)+"; last used block id:"+String.valueOf(LastUsedMaterial)+"; LastCheckedBlockID:"+String.valueOf(LastCheckedMaterial)+"; LastYMsg:"+LastYMsg+"]. Please try again.");
			}
		}
	}//end doWildPortal
	
	private static Location getLocationOnGround(World world, Location tryLocation_XandZ, double minY, Player player) {
		//tryLocation_XandZ.setY(255);
		int tryX=(int)tryLocation_XandZ.getX();
		int tryY=255;
		int tryZ=(int)tryLocation_XandZ.getZ();
		LastUsedMaterial=Material.AIR;
		//if (IsVerbose) main.logWriteLine("Finding a place for "+player.getName() + " in "+world.getName()+"..."); //NOTE: careful since we are running every TRY not just every birth
		List<Material> inhabitable_materials = new ArrayList<Material>();
		inhabitable_materials.add(Material.DIRT);
		inhabitable_materials.add(Material.GRASS);
		inhabitable_materials.add(Material.SAND);
		inhabitable_materials.add(Material.ICE);
		inhabitable_materials.add(Material.SNOW);
		inhabitable_materials.add(Material.SNOW_BLOCK);
		inhabitable_materials.add(Material.MYCEL);
		
		List<Material> uninhabitable_materials = new ArrayList<Material>();
		uninhabitable_materials.add(Material.LAVA);
		uninhabitable_materials.add(Material.STATIONARY_LAVA);
		uninhabitable_materials.add(Material.STATIONARY_WATER);
		uninhabitable_materials.add(Material.WATER);
		uninhabitable_materials.add(Material.WATER_LILY);
		uninhabitable_materials.add(Material.WEB);
		uninhabitable_materials.add(Material.FIRE);
		
		List<Material> constructed_materials = new ArrayList<Material>();
		constructed_materials.add(Material.SOIL);
		constructed_materials.add(Material.CROPS);
		constructed_materials.add(Material.WOOD);
		constructed_materials.add(Material.TNT);
		//1.7: tripwire
		//1.8: barrier
				
		while (tryY>=minY) {
			LastUsedY=tryY;
			//DEPRECATED by Mojang int thisBlockTypeID=world.getBlockTypeIdAt(tryX,tryY,tryZ);
			Material mat = world.getBlockAt(tryX,tryY,tryZ).getType();
			//see https://dev.bukkit.org/projects/supplies/pages/material-list
			//LastCheckedBlockID=thisBlockTypeID;
			
			if (inhabitable_materials.contains(mat)) {
				int block1AboveID=world.getBlockTypeIdAt(tryX,tryY+1,tryZ);
				int block2AboveID=world.getBlockTypeIdAt(tryX,tryY+2,tryZ);
				if (block1AboveID==0 && block2AboveID==0) {
					LastUsedMaterial=mat;
					tryY+=1;//move from grass block to air block above it
					break;
				}
				else {
					//if (IsVerbose) main.logWriteLine("WildPortal: Skipping a potentially habitable area because of smothering block (ID "+String.valueOf(LastUsedMaterial)+") at ("+String.valueOf(tryX)+","+String.valueOf(tryY)+","+String.valueOf(tryZ)+")...");
				}
			}
			else if ( constructed_materials.contains(mat)
					|| uninhabitable_materials.contains(mat)) {
				LastUsedMaterial=mat;
				//Double.toString(tryX)
				//if (player!=null) {
				//if (IsVerbose) main.logWriteLine("WildPortal: Skipping a potentially covered area because of dangerous block (ID "+String.valueOf(LastUsedMaterial)+") at ("+String.valueOf(tryX)+","+String.valueOf(tryY)+","+String.valueOf(tryZ)+")...");
				//}
				tryY=0; //forces "not found" condition
				break;
			}
			tryY-=1.0;
		}
		if (tryY<minY) tryLocation_XandZ.setY(0.0);//0.0 is a FLAG that there is no ground here
		else tryLocation_XandZ.setY(tryY);
		return tryLocation_XandZ;
	}//end getLocationOnGround
	
	
}//end class ClickListener
