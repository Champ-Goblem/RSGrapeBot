package Leroux.FreeWorldChopper.Enums;

public enum Trees {

    NORMAL (new int[]{38787,38785,38760,38783,1282,1289,47600,47598}, new int[]{-128, 128, -256, 0, -128, 128} ,1511, 1),
    OAK (new int[]{38731, 38732}, new int[]{-128, 128, -256, 0, -128, 128}, 1521, 15),
    WILLOW (new int[]{58006, 38627, 38616}, new int[]{-128, 128, -256, 0, -128, 128}, 1519, 30),
    YEW (new int[]{0}, new int[]{-128, 128, -256, 0, -128, 128}, 1515, 60);

    private final int[] treeIDs, treeBounds;
    private final int logInvID, minLV;

    public int[] getTreeIDs() { return treeIDs; }
    public int[] getTreeBounds() { return treeBounds; }
    public int getLogInvID() { return logInvID; }
    public int getMinLV() { return minLV; }

    Trees(int[] treeIDs, int[] treeBounds, int logInvID, int minLV) {
        this.treeIDs = treeIDs;
        this.treeBounds = treeBounds;
        this.logInvID = logInvID;
        this.minLV = minLV;
    }
}
