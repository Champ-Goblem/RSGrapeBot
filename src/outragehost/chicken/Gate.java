package outragehost.chicken;

import org.powerbot.script.Condition;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

public class Gate extends Task<ClientContext> {

    public Gate(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        GameObject closedGate = ctx.objects.select().id(45208).nearest().limit(1).peek();
        return closedGate.interact("Open", "Gate");
    }

    @Override
    public void execute() {
        GameObject closedGate = ctx.objects.id(45208).nearest().limit(1).peek();
        if (closedGate.inViewport()) {
            if (closedGate.interact("Open", "Gate")) {
                Fighter.displayStatus = ("Opening Gate");
                closedGate.interact("Open");
                Condition.sleep(5000);
            }
        }else {
            ctx.camera.turnTo(closedGate);
            Fighter.displayStatus = ("Opening Gate");
            closedGate.interact("Open");
            Condition.sleep(5000);

        }
    }
}