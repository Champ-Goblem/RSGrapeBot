package stumpy3toes.scripts.stumpyisland.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.task.Task;

import java.util.concurrent.Callable;

public class RoofSetting extends Task {
    private static final int OPTION_SELECTED_TEXTURE_ID = 762;

    private static final int OPTIONS_WIDGET_ID = 261;
    private static final int OPTIONS_TABS_COMPONENT_ID = 1;
    private static final int OPTIONS_TAB_DISPLAY_COMPONENT_ID = 0;
    private static final int OPTIONS_TAB_DISPLAY_ADVANCED_OPTIONS_COMPONENT_ID = 21;

    private static final int ADVANCED_OPTIONS_WIDGET_ID = 60;
    private static final int ADVANCED_OPTIONS_ROOFTOPS_SETTING_COMPONENT_ID = 8;
    private static final int ADVANCED_OPTIONS_MAIN_COMPONENT_ID = 2;
    private static final int ADVANCED_OPTIONS_MAIN_CLOSE_COMPONENT_ID = 11;

    private Boolean previousSetting = null;
    private boolean revertingToPrevious = false;

    public RoofSetting(ClientContext ctx) {
        super(ctx, "Rooftops Setting");
    }

    @Override
    protected boolean checks() {
        return true;
    }

    @Override
    protected void poll() {
        if (!revertingToPrevious || (revertingToPrevious && !previousSetting)) {
            if (!advancedOptionsOpen()) {
                setStatus("Opening advanced options");
                if (!ctx.game.tab(Game.Tab.OPTIONS) || !selectComponent(displayTab(), true)) {
                    return;
                }
                if (!ctx.widgets.component(OPTIONS_WIDGET_ID, OPTIONS_TAB_DISPLAY_ADVANCED_OPTIONS_COMPONENT_ID).click()
                        || !Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return advancedOptionsOpen();
                    }
                }, 100, 5)) {
                    return;
                }
            }
            if (previousSetting == null) {
                previousSetting = isSelected(rooftopsSetting());
            }
            setStatus(revertingToPrevious ? "Reverting rooftop setting to before script was ran"
                    : "Making sure rooftops are always transparent");
            if (!selectComponent(rooftopsSetting(), revertingToPrevious ? previousSetting : true)) {
                return;
            }
            if (advancedOptionsOpen()) {
                setStatus("Closing advanced options");
                if (!ctx.widgets.component(ADVANCED_OPTIONS_WIDGET_ID, ADVANCED_OPTIONS_MAIN_COMPONENT_ID)
                        .component(ADVANCED_OPTIONS_MAIN_CLOSE_COMPONENT_ID).click()
                        || !Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !advancedOptionsOpen();
                    }
                }, 100, 5)) {
                    return;
                }
            }
            revertingToPrevious = true;
        }
        pause(true);
    }

    private Component displayTab() {
        return ctx.widgets.component(OPTIONS_WIDGET_ID, OPTIONS_TABS_COMPONENT_ID).component(OPTIONS_TAB_DISPLAY_COMPONENT_ID);
    }

    private Component rooftopsSetting() {
        return ctx.widgets.component(ADVANCED_OPTIONS_WIDGET_ID, ADVANCED_OPTIONS_ROOFTOPS_SETTING_COMPONENT_ID);
    }

    private boolean advancedOptionsOpen() {
        return  rooftopsSetting().visible();
    }

    private boolean isSelected(Component component) {
        return component.textureId() == OPTION_SELECTED_TEXTURE_ID;
    }

    private boolean selectComponent(final Component component, final boolean selected) {
        return isSelected(component) == selected || (component.click() && Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return isSelected(component) == selected;
            }
        }, 100, 5));
    }
}
