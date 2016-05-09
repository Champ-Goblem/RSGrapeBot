package Leroux.FreeWorldChopper.Enums;

public enum Bankers {

    BOOTHS(new int[] {25808,42377,42378,42217,782,2012,2015,2019});

    private final int[] boothIDs;

    public int[]getBoothIDs() { return boothIDs;}

    Bankers (int[]boothIDs) {
        this.boothIDs = boothIDs;
    }
}
