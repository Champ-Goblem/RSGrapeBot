package sd8z.scripts.sbonfire.util;

public enum Log {

    NORMAL(1511), OAK(1521), WILLOW(1519), TEAK(6333), MAPLE(1517), MAHOGANY(6332), EUCALYPTUS(12581), YEW(1515),
    MAGIC(1513), ELDER(29556);

    private final int id;

    Log(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
