package stumpy3toes.scripts.stumpyisland.tasks.charactercreation;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;

public class CharacterFeature {
    private final Component leftArrowComponent;
    private final Component rightArrowComponent;

    public CharacterFeature(Widget parentWidget, ArrowPair arrowPair) {
        leftArrowComponent = parentWidget.component(arrowPair.leftArrowComponentID);
        rightArrowComponent = parentWidget.component(arrowPair.rightArrowComponentID);
    }

    public boolean left() {
        return leftArrowComponent.click();
    }

    public boolean right() {
        return rightArrowComponent.click();
    }

    public boolean shuffle() {
        final boolean leftClick = Random.nextBoolean();
        for (int i = 0; i < Random.nextInt(1, 20); i++) {
            if (leftClick) {
                if (!left()) {
                    return false;
                }
            } else if (!right()) {
                return false;
            }
            Condition.sleep(200);
        }
        return true;
    }
}
