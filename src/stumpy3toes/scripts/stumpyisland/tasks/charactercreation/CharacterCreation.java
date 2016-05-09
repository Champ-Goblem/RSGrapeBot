package stumpy3toes.scripts.stumpyisland.tasks.charactercreation;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.task.Task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class CharacterCreation extends Task {
    private static final int WIDGET_ID = 269;
    private static final int ACCEPT_BUTTON_ID = 100;
    private static final int MALE_BUTTON_ID = 136;
    private static final int FEMALE_BUTTON_ID = 137;
    private static final int GENDER_SELECTED_TEXTURE_ID = 294;

    private static final ArrowPair HEAD = new ArrowPair(106, 113);
    private static final ArrowPair JAW = new ArrowPair(107, 114);
    private static final ArrowPair TORSO = new ArrowPair(108, 115);
    private static final ArrowPair ARMS = new ArrowPair(109, 116);
    private static final ArrowPair HANDS = new ArrowPair(110, 117);
    private static final ArrowPair LEGS = new ArrowPair(111, 118);
    private static final ArrowPair FEET = new ArrowPair(112, 119);
    private static final ArrowPair HAIR_COLOR = new ArrowPair(105, 121);
    private static final ArrowPair TORSO_COLOR = new ArrowPair(123, 127);
    private static final ArrowPair LEGS_COLOR = new ArrowPair(122, 129);
    private static final ArrowPair FEET_COLOR = new ArrowPair(124, 130);
    private static final ArrowPair SKIN_COLOR = new ArrowPair(125, 131);
    private static final ArrowPair[] CHARACTER_FEATURES_ARROW_IDS = new ArrowPair[] {
            HEAD,
            JAW,
            TORSO,
            ARMS,
            HANDS,
            LEGS,
            FEET,
            HAIR_COLOR,
            TORSO_COLOR,
            LEGS_COLOR,
            FEET_COLOR,
            SKIN_COLOR
    };

    public Gender genderChoice = Gender.RANDOM;

    public boolean randomiseCreation = false;
    private boolean hasRandomisedCharacter = false;

    public CharacterCreation(ClientContext ctx) {
        super(ctx, "Character Creation");
    }

    @Override
    public boolean checks() {
        return characterCreationWidgetOpen();
    }

    @Override
    public void poll() {
        if (genderChoice == Gender.RANDOM) {
            genderChoice = Random.nextBoolean() ? Gender.MALE : Gender.FEMALE;
        }

        if (!genderSelected()) {
            setStatus("Selecting gender");
            if (!genderButton().click() || !Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return genderSelected();
                }
            }, 250, 4)) {
                return;
            }
        }

        if (!hasRandomisedCharacter && randomiseCreation) {
            setStatus("Randomising character features");
            int randomFeatureCount = Random.nextInt(1, CHARACTER_FEATURES_ARROW_IDS.length + 1);
            List<ArrowPair> featurePairs = new LinkedList<ArrowPair>(Arrays.asList(CHARACTER_FEATURES_ARROW_IDS));
            for (int i = 0; i < randomFeatureCount; i++) {
                int index = Random.nextInt(0, featurePairs.size());
                if (new CharacterFeature(widget(), featurePairs.get(index)).shuffle()) {
                    hasRandomisedCharacter = true;
                }
                featurePairs.remove(index);
            }
            if (!hasRandomisedCharacter) {
                return;
            }
        }

        setStatus("Clicking accept");
        if (acceptButton().click()) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !characterCreationWidgetOpen();
                }
            }, 250, 6);
        }
    }

    private boolean characterCreationWidgetOpen() {
        return acceptButton().visible();
    }

    private boolean genderSelected() {
        return genderButton().textureId() == GENDER_SELECTED_TEXTURE_ID;
    }

    private Widget widget() {
        return ctx.widgets.widget(WIDGET_ID);
    }

    private Component acceptButton() {
        return widget().component(ACCEPT_BUTTON_ID);
    }

    private Component genderButton() {
        int buttonID = -1;
        switch (genderChoice) {
            case MALE:
                buttonID = MALE_BUTTON_ID;
                break;
            case FEMALE:
                buttonID = FEMALE_BUTTON_ID;
                break;
        }
        return widget().component(buttonID);
    }
}
