package xxb.event.impl;

import java.util.EventObject;

public class ExperienceEvent extends EventObject {

    private final int skillIndex;
    private final int oldExperience;
    private final int newExperience;

    public ExperienceEvent(int skillIndex, int oldExperience, int newExperience) {
        super(skillIndex);
        this.skillIndex = skillIndex;
        this.oldExperience = oldExperience;
        this.newExperience = newExperience;
    }

    public int getSkillIndex() {
        return skillIndex;
    }

    public int getOldExperience() {
        return oldExperience;
    }

    public int getNewExperience() {
        return newExperience;
    }

    public int getExperienceChange() {
        return newExperience - oldExperience;
    }

    @Override
    public String toString() {
        return String.format("xpgain[:idx %d :old %d :new %d :diff %d]", getSkillIndex(), getOldExperience(), getNewExperience(), getExperienceChange());
    }

}
 