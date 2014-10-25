/**
 * @author Jacob Gustafson
 *
 */
package me.expertmm.WildPortal;

//import java.awt.Rectangle;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class WildPortalPortal {
	
	
	//LOCAL:
	private int X=-143;
	private int Y=80;
	private int Z=263;
	public String sourceWorldName="world";
	private String destinationWorldName="<this>";
	public String destinationKeyword="<wild>";
	public String allowedPlayerList="<Everyone>";
	private IRect destRect = null;
	
	
	//GLOBAL:
	public static String fieldDelimiter=",";
	public static String textDelimiter="\"";
	
    public static String SourceWorldColumnName="sourceWorld";
    public static String SourceXColumnName="sourceX";
    public static String SourceYColumnName="sourceY";
    public static String SourceZColumnName="sourceZ";
    public static String DestinationWorldColumnName="destinationWorld";
    public static String DestinationKeywordColumnName="destinationKeyword";
    public static String AllowedPlayerListColumnName="allowedPlayerList";
    
    public static String DestinationXColumnName="destination.X";
    public static String DestinationYColumnName="destination.Y";
    public static String DestinationWidthColumnName="destination.Width";
    public static String DestinationHeightColumnName="destination.Height";
	
    private static int Column_SourceWorld=-1;
    private static int Column_SourceX=-1;
    private static int Column_SourceY=-1;
    private static int Column_SourceZ=-1;
    private static int Column_DestinationWorld=-1;
    private static int Column_DestinationKeyword=-1;
    private static int Column_AllowedPlayerList=-1;
    
    private static int Column_DestinationX=-1;
    private static int Column_DestinationY=-1;
    private static int Column_DestinationWidth=-1;
    private static int Column_DestinationHeight=-1;

    public String getLocationCoords() {
		return String.valueOf(this.X)+","+String.valueOf(this.Y)+","+String.valueOf(this.Z);
	}
	public boolean equalsWildPortalSource(WildPortalPortal check_wildPortalPortal) {
		return (check_wildPortalPortal.getBlockX()==X) && (check_wildPortalPortal.getBlockY()==Y) && (check_wildPortalPortal.getBlockZ()==Z) && (check_wildPortalPortal.sourceWorldName.equalsIgnoreCase(sourceWorldName));
	}
	public boolean equalsBlockLocation(int check_X, int check_Y, int check_Z, String check_sourceWorldName) {
		return (check_X==X) && (check_Y==Y) && (check_Z==Z) && (check_sourceWorldName.equalsIgnoreCase(sourceWorldName));
	}
	public int getBlockX() {
		return X;
	}
	public int getBlockY() {
		return Y;
	}
	public int getBlockZ() {
		return Z;
	}
	public WildPortalPortal(int setX, int setY, int setZ, String set_sourceWorldName, String set_destinationWorldName, String set_destinationKeyword, int set_X, int set_Y, int set_Width, int set_Height) {
		this.Set(setX, setY, setZ, set_sourceWorldName, set_destinationWorldName, set_destinationKeyword,set_X,set_Y,set_Width,set_Height);
	}
	public World getDestinationWorld(Server server, World playerCurrentWorld) {
		//Location returnLocation=null;
		World returnWorld=null;
		if (server!=null) {
			if (playerCurrentWorld!=null) {
				//World world=null;
				if (destinationWorldName=="<this>") returnWorld=playerCurrentWorld;
				else returnWorld=server.getWorld(destinationWorldName);
				if (returnWorld!=null) {
					//returnLocation = new Location(world, this.X, this.Y, this.Z);
				}
				else {
					main.logWriteLine("ERROR: WildPortal getDestinationWorld failed to find a world matching the portal's destination world named \""+this.destinationWorldName+"\")");
				}
			}
			else {
				main.logWriteLine("ERROR: WildPortal needs non-null playerCurrentWorld object to get world object by name (in case value of destinationWorldName field is <this> instead of a world name).");
			}
		}
		else {
			main.logWriteLine("ERROR: WildPortal needs non-null server object to get world object by name.");
		}
		return returnWorld;//return returnLocation;
	}
	public void Set(int set_X, int set_Y, int set_Z, String set_sourceWorldName, String set_destinationWorldName, String set_destinationKeyword, int dest_X, int dest_Y, int dest_Width, int dest_Height) {
		this.X=set_X;
		this.Y=set_Y;
		this.Z=set_Z;
		this.sourceWorldName=set_sourceWorldName;
		this.destinationWorldName=set_destinationWorldName;
		this.destinationKeyword=set_destinationKeyword;
		this.destRect=new IRect(dest_X,dest_Y,dest_Width,dest_Height);
	}
	//public WildPortalPortal(Location set_blockLocation, String set_destinationWorldName, String set_destinationKeyword) {
	//	this.Set(set_blockLocation, set_destinationWorldName, set_destinationKeyword);
	//}
	//public void Set(Location set_blockLocation, String set_destinationWorldName, String set_destinationKeyword) {
	//	this.X=(int)set_blockLocation.getX();
	//	this.Y=(int)set_blockLocation.getY();
	//	this.Z=(int)set_blockLocation.getZ();
	//	this.sourceWorldName=set_blockLocation.getWorld().getName();
	//	this.destinationWorldName=set_destinationWorld;
	//	this.destinationKeyword=set_destinationKeyword;
	//}
	
	public boolean IsLikeSource(Location location) {
		return ( (location.getBlockX()==this.X)
				&& (location.getBlockY()==this.Y)
				&& (location.getBlockZ()==this.Z) 
				&& ( this.sourceWorldName.equalsIgnoreCase(location.getWorld().getName()) )
				);
	}
	public Location getSourceLocation(Server server) {
		Location thisLocation=null;
		if (server!=null) {
			World world=null;
			world=server.getWorld(destinationWorldName);
			if (world!=null) {
				thisLocation = new Location(world, this.X, this.Y, this.Z);
			}
			else {
				main.logWriteLine("ERROR: WildPortal getLocation failed to find a world matching the portal's destination named \""+this.destinationWorldName+"\")");
			}
		}
		return thisLocation;
	}
	public String toString_DestRectOnly_AsRangePair() {
		String returnString = "";
		if (this.destRect!=null) {
			returnString=this.destRect.getLeft()+" to "+this.destRect.getRightInclusive()
					+","+this.destRect.getTop()+" to "+this.destRect.getBottomInclusive();
		}
		return returnString;
	}
    public static boolean DetectColumnIndeces(String originalLine, String SayErrorWasInWhatSourceFileName, int SayErrorWasInWhatSourceFileLineNumber) {
    	boolean HasAll=true;
    	String trimmedLine=null;
    	try {
    		if (SayErrorWasInWhatSourceFileName==null) SayErrorWasInWhatSourceFileName="";
    		if (originalLine!=null) {
    		trimmedLine=originalLine.trim();
    		List<String> fields = RTable.getExplodedCSVLine(trimmedLine);
    		for (ListIterator<String> iter = fields.listIterator(); iter.hasNext(); ) {
    			String element = iter.next();
    			if (element!=null) iter.set(element.trim());
    		}
    		
			//column names on first row (-1 since the wildportalList item 0 is after that row)
			Column_SourceWorld=fields.indexOf(SourceWorldColumnName);
			if (Column_SourceWorld<0) {
				main.logWriteLine("No column named "+SourceWorldColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_SourceX=fields.indexOf(SourceXColumnName);
			if (Column_SourceX<0) {
				main.logWriteLine("No column named "+SourceXColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_SourceY=fields.indexOf(SourceYColumnName);
			if (Column_SourceY<0) {
				main.logWriteLine("No column named "+SourceYColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_SourceZ=fields.indexOf(SourceZColumnName);
			if (Column_SourceZ<0) {
				main.logWriteLine("No column named "+SourceZColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_DestinationWorld=fields.indexOf(DestinationWorldColumnName);
			if (Column_DestinationWorld<0) {
				main.logWriteLine("No column named "+DestinationWorldColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_DestinationKeyword=fields.indexOf(DestinationKeywordColumnName);
			if (Column_DestinationKeyword<0) {
				main.logWriteLine("No column named "+DestinationKeywordColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_AllowedPlayerList=fields.indexOf(AllowedPlayerListColumnName);
			if (Column_AllowedPlayerList<0) {
				main.logWriteLine("No column named "+AllowedPlayerListColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}

			Column_DestinationX=fields.indexOf(DestinationXColumnName);
			if (Column_DestinationX<0) {
				main.logWriteLine("No column named "+DestinationXColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_DestinationY=fields.indexOf(DestinationYColumnName);
			if (Column_DestinationY<0) {
				main.logWriteLine("No column named "+DestinationYColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_DestinationWidth=fields.indexOf(DestinationWidthColumnName);
			if (Column_DestinationWidth<0) {
				main.logWriteLine("No column named "+DestinationWidthColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
			Column_DestinationHeight=fields.indexOf(DestinationHeightColumnName);
			if (Column_DestinationHeight<0) {
				main.logWriteLine("No column named "+DestinationHeightColumnName+" in "+SayErrorWasInWhatSourceFileName+" was found on line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				HasAll=false;
			}
    		}
    		else {
				main.logWriteLine("Line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+" was read as null String in "+SayErrorWasInWhatSourceFileName+".");
				HasAll=false;
    		}
    	}
    	catch (Exception e) {
    		HasAll=false;
		    main.logWriteLine("doLoadWildPortalData could not finish parsing line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+":"+e.getMessage());//System.err.println(e.getMessage());
    	}
    	return HasAll;
    }//end DetectColumnIndeces
	public static WildPortalPortal FromCSVLine(String originalLine, String SayErrorWasInWhatSourceFileName, int SayErrorWasInWhatSourceFileLineNumber) {
		String participle="starting row";
		boolean HasAll=true;
	    String trimmedLine=null;
	    WildPortalPortal newWildPortal = null;
	    String sourceWorldName = null;
	    String destWorldName = null;
	    String sourceXString = null;
	    String sourceYString = null;
	    String sourceZString = null;
	    int sourceX=0;
	    int sourceY=0;
	    int sourceZ=0;
	    String destXString=null;
	    String destYString=null;
	    String destWidthString=null;
	    String destHeightString=null;
	    if (originalLine!=null) {
	    	try {
		    	trimmedLine=originalLine.trim();
		    	if (trimmedLine.length()>0) {
		    		List<String> fields = RTable.getExplodedCSVLine(trimmedLine);
		    		if (Column_SourceWorld<0) HasAll=false;
		    		else sourceWorldName = fields.get(Column_SourceWorld);
	    			if (sourceWorldName!=null) {
		    			//World sourceWorld = thisServer.getWorld(sourceWorldName);
		    			//if (sourceWorld!=null) {
		    				//participle="getting world name field value";
		    				if (Column_DestinationWorld<0) HasAll=false;
		    				else destWorldName=fields.get(Column_DestinationWorld);
		    				if (destWorldName!=null) {
		    					//participle="getting world";
			    				//World destWorld = thisServer.getWorld(destWorldName);
			    				//if (destWorld!=null) {
			    					/*
			    					participle="parsing location integers";
					    			Location sourceLocation = new Location(sourceWorld,
					    					(double) Integer.parseInt(fields.get(Column_SourceX)), //parse as int for safety, since was saved without decimal point
					    					(double) Integer.parseInt(fields.get(Column_SourceY)), //parse as int for safety, since was saved without decimal point
					    					(double) Integer.parseInt(fields.get(Column_SourceZ)) //parse as int for safety, since was saved without decimal point
					    					);
					    			participle="after parsing location integers";
					    			*/
			    					participle="loading location integers";
			    					if (Column_SourceX<0) HasAll=false;
			    					else sourceXString=fields.get(Column_SourceX);
			    					if (Column_SourceY<0) HasAll=false;
			    					else sourceYString=fields.get(Column_SourceY);
			    					if (Column_SourceZ<0) HasAll=false;
			    					else sourceZString=fields.get(Column_SourceZ);
					    			participle="parsing location integers";
			    					sourceX=Integer.parseInt(sourceXString);
			    					sourceY=Integer.parseInt(sourceYString);
			    					sourceZ=Integer.parseInt(sourceZString);
					    			participle="after parsing location integers";
					    			
					    			if (HasAll) {
				    					participle="loading destination integers";
						    			if (Column_DestinationX<0) HasAll=false;
						    			else destXString=fields.get(Column_DestinationX);
						    			if (Column_DestinationY<0) HasAll=false;
						    			else destYString=fields.get(Column_DestinationY);
						    			if (Column_DestinationWidth<0) HasAll=false;
						    			else destWidthString=fields.get(Column_DestinationWidth);
						    			if (Column_DestinationHeight<0) HasAll=false;
						    			else destHeightString=fields.get(Column_DestinationHeight);
						    			participle="parsing destination integers";
				    					int destX=Integer.parseInt(destXString);
				    					int destY=Integer.parseInt(destYString);
				    					int destWidth=Integer.parseInt(destWidthString);
				    					int destHeight=Integer.parseInt(destHeightString);
						    			participle="after parsing destination integers";
						    			if (HasAll) {
							    			if (Column_DestinationKeyword<0) HasAll=false;
						    				String destinationKeyword=fields.get(Column_DestinationKeyword);
						    				if (destinationKeyword!=null) {
						    					newWildPortal = new WildPortalPortal(sourceX,sourceY,sourceZ,sourceWorldName, destWorldName, destinationKeyword, destX, destY, destWidth, destHeight);
								    			participle="successfully returning a WildPortalPortal";
								    			//sourceLocation = null;
								    			//thisWildPortal = null;
						    				}
							    			else {
							    				main.logWriteLine("Column "+String.valueOf(Column_DestinationKeyword)+" ("+DestinationKeywordColumnName+") was read as null in "+SayErrorWasInWhatSourceFileName+" line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber));
							    			}
						    			}
						    			else {
						    				main.logWriteLine("Columns "+String.valueOf(Column_DestinationX)+","+String.valueOf(Column_DestinationY)+","+String.valueOf(Column_DestinationWidth)+" and "+String.valueOf(Column_DestinationHeight)+" ("+DestinationXColumnName+","+DestinationYColumnName+","+DestinationWidthColumnName+","+DestinationHeightColumnName+") made incomplete Rectangle in "+SayErrorWasInWhatSourceFileName+" line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber));
						    			}
					    			}
					    			else {
					    				main.logWriteLine("Columns "+String.valueOf(Column_SourceX)+","+String.valueOf(Column_SourceY)+" and "+String.valueOf(Column_SourceZ)+" ("+SourceXColumnName+","+SourceYColumnName+","+SourceZColumnName+") made incomplete vector in "+SayErrorWasInWhatSourceFileName+" line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber));
					    			}
			    				//}
				    			//else {
				    			//	main.logWriteLine("No actual world named \""+destWorldName+"\" for DestinationWorld in "+SayErrorWasInWhatSourceFileName+" line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
				    			//}
		    				}
			    			else {
			    				main.logWriteLine("Column "+String.valueOf(Column_DestinationWorld)+" ("+DestinationWorldColumnName+") was read as null in "+SayErrorWasInWhatSourceFileName+" line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber));
			    			}
		    			//}
		    			//else {
		    			//	main.logWriteLine("No actual world named \""+sourceWorldName+"\" for "+SourceWorldColumnName+" in "+SayErrorWasInWhatSourceFileName+" line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+".");
		    			//}
	    			}
	    			else {
	    				main.logWriteLine("Column "+String.valueOf(Column_SourceWorld)+" ("+SourceWorldColumnName+") was read as null in "+SayErrorWasInWhatSourceFileName+" line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber));
	    			}
	    		
		    		
		    	}//end if trimmedLine is not blank
	    	}
	    	catch (Exception e) {
			    main.logWriteLine("FromCSVLine could not finish "+participle+" while parsing line "+String.valueOf(SayErrorWasInWhatSourceFileLineNumber)+":"+e.getMessage());//System.err.println(e.getMessage());
	    	}
	    }
	    else {
	    	main.logWriteLine("FromCSVLine skipped null line.");
	    }
		return newWildPortal;
	}//end FromCSVLine
	public static String toCSVTitleRow() {
		//return "sourceX,sourceY,sourceZ,sourceWorldName,destinationWorldName,destinationKeyword,allowedPlayerList,destination.X,destination.Y,destination.Width,destination.Height";
		return SourceXColumnName
				+","+SourceYColumnName
				+","+SourceZColumnName
				+","+SourceWorldColumnName
				+","+DestinationWorldColumnName
				+","+DestinationKeywordColumnName
				+","+AllowedPlayerListColumnName
				+","+DestinationXColumnName
				+","+DestinationYColumnName
				+","+DestinationWidthColumnName
				+","+DestinationHeightColumnName
				;
	}
	public String toCSVLine() {
		String returnString=null;
		//fieldDelimiter textDelimiter
		boolean HasAll=true;
		String tempString="";
		String participle="before initializing";
		try {
			if (HasAll) {
				participle="initializing line with sourceX";
				//String sourceXString=String.valueOf(X);
				tempString=RTable.LiteralFieldToCSVField(String.valueOf(X));
				if (tempString==null) {
					main.logWriteLine("ERROR in WildPortalPortal toCSVLine: LiteralFieldToCSVField returned null");
					HasAll=false;
				}
			}
			else HasAll=false;
			if (HasAll) {
				participle="appending sourceY";
				tempString+=","+RTable.LiteralFieldToCSVField(String.valueOf(Y));
			}
			else HasAll=false;
			if (HasAll) {
				participle="appending sourceZ";
				tempString+=","+RTable.LiteralFieldToCSVField(String.valueOf(Z));
			}
			else HasAll=false;
			if (HasAll&&sourceWorldName!=null) {
				participle="appending sourceWorldName";
				tempString+=","+RTable.LiteralFieldToCSVField(String.valueOf(sourceWorldName));
			}
			else {
				main.logWriteLine("ERROR in WildPortalPortal toCSVLine: sourceWorldName was null!");
				HasAll=false;
			}
			if (HasAll&&destinationWorldName!=null) {
				participle="appending destinationWorldName";
				tempString+=","+RTable.LiteralFieldToCSVField(String.valueOf(destinationWorldName));
			}
			else {
				main.logWriteLine("ERROR in WildPortalPortal toCSVLine: destinationWorldName was null!");
				HasAll=false;
			}
			if (HasAll&&destinationKeyword!=null) {
				participle="appending destinationKeyword";
				tempString+=","+RTable.LiteralFieldToCSVField(String.valueOf(destinationKeyword));
			}
			else {
				main.logWriteLine("ERROR in WildPortalPortal toCSVLine: destinationKeyword was null!");
				HasAll=false;
			}
			if (HasAll&&allowedPlayerList!=null) {
				participle="appending allowedPlayerList";
				tempString+=","+RTable.LiteralFieldToCSVField(String.valueOf(allowedPlayerList));
			}
			else {
				main.logWriteLine("ERROR in WildPortalPortal toCSVLine: allowedPlayerList was null!");
				HasAll=false;
			}
			participle="before checking destRect";
			if (HasAll&&destRect!=null) {
				participle="converting destRect to string";
				String destRectString=destRect.toStringXYWidthHeight(",");
				if (HasAll&&destRectString!=null) {
					participle="appending destRect string";
					tempString+=","+destRectString;
					returnString=tempString;
				}
				else {
					main.logWriteLine("ERROR in WildPortalPortal toCSVLine: destRect returned a null string!");
					HasAll=false;
				}
			}
			else {
				main.logWriteLine("ERROR in WildPortalPortal toCSVLine: destRect was null!");
				HasAll=false;
			}			
		}
		catch (Exception e) {
			returnString=null;
			main.logWriteLine("Could not finish "+participle+" in WildPortalPortal toCSVLine:"+e.toString());
		}
		return returnString;
	}
	public int getDestXMin() {
		return (this.destRect!=null)?this.destRect.getLeft():0;
	}
	public int getDestZMin() {
		// TODO Auto-generated method stub
		return (this.destRect!=null)?this.destRect.getTop():0;
	}
	public int getDestXMax() {
		// TODO Auto-generated method stub
		return (this.destRect!=null)?this.destRect.getRightInclusive():0;
	}
	public int getDestZMax() {
		// TODO Auto-generated method stub
		return (this.destRect!=null)?this.destRect.getBottomInclusive():0;
	}
}
