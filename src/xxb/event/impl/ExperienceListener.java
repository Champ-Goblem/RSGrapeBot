package xxb.event.impl;

import java.util.EventListener;

public interface ExperienceListener extends EventListener {
    void onExperienceChanged(ExperienceEvent evt);
}
