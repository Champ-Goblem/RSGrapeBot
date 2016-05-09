package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.task.Task;

public class Chat extends Task {
    private static final int OLD_CHAT_CONTINUE_WIDGET_ID = 162;
    private static final int[][] CHAT_CONTINUES = {
            {11, 3},
            {OLD_CHAT_CONTINUE_WIDGET_ID, 33},
            {193, 2},
            {233, 2}
    };

    public Chat(ClientContext ctx) {
        super(ctx, "Chat");
    }

    @Override
    public boolean checks() {
        return ctx.chat.canContinue() || getContinueComponent() != null;
    }

    @Override
    public void poll() {
        setStatus("Skipping through chat");
        if (ctx.chat.canContinue()) {
            ctx.chat.clickContinue(true);
        } else {
            Component continueComponent = getContinueComponent();
            if (continueComponent != null) {
                if (continueComponent.widget().id() == OLD_CHAT_CONTINUE_WIDGET_ID) {
                    continueComponent.click();
                } else {
                    ctx.input.send(" ");
                }
            }
        }
        Condition.sleep(500);
    }

    private Component getContinueComponent() {
        for (int[] chatContinues : CHAT_CONTINUES) {
            Component continueComponent = ctx.widgets.component(chatContinues[0], chatContinues[1]);
            if (continueComponent.visible()) {
                return continueComponent;
            }
        }
        return null;
    }
}
