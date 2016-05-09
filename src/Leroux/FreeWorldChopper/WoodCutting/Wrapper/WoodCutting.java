package Leroux.FreeWorldChopper.WoodCutting.Wrapper;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

public class WoodCutting {

    private static int[] treeIDs;
    private static int[] boothIDs;
    private static int logInvID;
    private static Area chopArea;
    private static Area bankArea;
    private static Tile[] pathToBank;
    private static Tile[] pathToSpot;
    private static int[] bankBounds = {-652, 144, -256, -4, -128, 592}; // The Booth
    private static int[] treeBounds;
    private static int minLevel;
    private static String treeName;

    public static int[] getTreeIDs() { return treeIDs; }
    public static void setTreeIDs(int[] treeIDs) { WoodCutting.treeIDs = treeIDs; }
    public static int[] getBoothIDs() { return boothIDs; }
    public static void setBoothIDs(int[] boothIDs) { WoodCutting.boothIDs = boothIDs; }
    public static int getLogInvID() { return logInvID; }
    public static void setLogInvID(int logInvID) { WoodCutting.logInvID = logInvID; }
    public static Area getChopArea() { return chopArea; }
    public static void setChopArea(Area chopArea) { WoodCutting.chopArea = chopArea; }
    public static Area getBankArea() { return bankArea; }
    public static void setBankArea(Area bankArea) { WoodCutting.bankArea = bankArea; }
    public static Tile[] getPathToBank() { return pathToBank; }
    public static void setPathToBank(Tile[] pathToBank) { WoodCutting.pathToBank = pathToBank; }
    public static Tile[] getPathToSpot() { return pathToSpot; }
    public static void setPathToSpot(Tile[] pathToSpot) { WoodCutting.pathToSpot = pathToSpot; }
    public static int[] getTreeBounds() {return treeBounds; }
    public static void setTreeBounds(int[] treeBounds) { WoodCutting.treeBounds = treeBounds; }
    public static int[] getBankBounds() { return bankBounds; }
    public static int getMinLevel() { return minLevel; }
    public static void setMinLevel(int minLevel) { WoodCutting.minLevel = minLevel; }
    public static String getTreeName() { return treeName; }
    public static void setTreeName(String treeName) { WoodCutting.treeName = treeName; }
}