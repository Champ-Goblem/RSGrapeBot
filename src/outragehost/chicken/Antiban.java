package outragehost.chicken;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.*;

public class Antiban extends Task<ClientContext> {

    public Antiban(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return Fighter.killed > 1;
    }

    @Override
    public void execute() {
        Random rnd = new Random();
        int random = rnd.nextInt(1,100);
        if (random == 1) {
            System.out.println("Anti-Ban Started Pausing For 10 Seconds");
            Fighter.displayStatus = ("Anti-Ban");
            Condition.sleep(rnd.nextInt(10125,15015));
        }
        if (random == 2 || random == 3) {
            if (!ctx.backpack.collapsed()) {
                Item feathers = ctx.backpack.select().name("Feather").poll();
                System.out.println("Anti-Ban Started Examining");
                Fighter.displayStatus = ("Anti-Ban");
                feathers.interact("Examine", "Feather");
                Condition.sleep(rnd.nextInt(100, 1000));
            }
        }
    }
}