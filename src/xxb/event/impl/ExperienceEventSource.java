package xxb.event.impl;

import org.powerbot.script.rt4.ClientContext;
import xxb.event.EventSource;

import java.util.EventListener;

public class ExperienceEventSource implements EventSource<ExperienceEvent> {

    private final int[] experienceCache;

    public ExperienceEventSource(ClientContext ctx) {
        experienceCache = new int[23];

        for (int i = 0; i < experienceCache.length; i++) {
            experienceCache[i] =  ctx.skills.experience(i);
        }
    }

    @Override
    public void dispatch(ClientContext ctx, ExperienceEvent evt) {
        for(EventListener l : ctx.dispatcher) {
            if(l instanceof ExperienceListener)
                ((ExperienceListener) l).onExperienceChanged(evt);
        }
    }

    @Override
    public void process(ClientContext ctx) {
        for (int i = 0; i < experienceCache.length; i++) {
            int oldExperience = experienceCache[i];
            int newExperience =  ctx.skills.experience(i);

            if (oldExperience != newExperience) {
                dispatch(ctx, new ExperienceEvent(i, oldExperience, newExperience));

                experienceCache[i] = newExperience;
            }
        }
    }

}
