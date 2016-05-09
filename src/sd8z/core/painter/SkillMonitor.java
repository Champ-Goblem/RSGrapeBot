package sd8z.core.painter;

import org.powerbot.script.rt6.ClientContext;

//And even more cancer

public class SkillMonitor extends Detail {

    private int skill;
    private ClientContext ctx;
    private int start;

    public SkillMonitor(String detail, boolean calcPerHour) {
        super(detail, calcPerHour);
    }

    public SkillMonitor(String name, int skill, ClientContext ctx) {
        this(name, true);
        this.skill = skill;
        this.ctx = ctx;
        reset();
    }

    public void reset() {
        start = ctx.skills.experience(skill);
    }

    @Override
    public Object getObject() {
        return ctx.skills.experience(skill) - start;
    }
}
