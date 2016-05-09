package sd8z.core.painter;

//More cancer

public class Detail {

    private String name;
    private boolean perHour;
    private Object object;

    public Detail(String detail, boolean calcPerHour) {
        this.name = detail;
        perHour = calcPerHour;
    }

    public String getName() {
        return name;
    }

    public boolean perHour() {
        return perHour;
    }

    public Object getObject() {
        return object;
    }

    public Detail setObject(Object o) {
        object = o;
        return this;
    }
}
