package stumpy3toes.api.script;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;

public interface InteractiveEntity extends org.powerbot.script.InteractiveEntity {
    boolean reachable();
    int distance();
    double tileDistance();
    boolean walk();
    boolean walkInViewport();
    boolean walkingInteraction(Filter<? super MenuCommand> f, Condition.Check check);
    boolean walkingInteraction(String action, String option, Condition.Check check);
    boolean walkingInteraction(String action, Condition.Check check);
}
