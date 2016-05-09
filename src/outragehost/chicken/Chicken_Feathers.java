package outragehost.chicken;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GeItem;
import org.powerbot.script.rt6.GroundItem;

public class Chicken_Feathers extends Task<ClientContext> {

    public Chicken_Feathers(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return Fighter.killed == 10;
    }

    @Override
    public void execute() {
        GeItem featherPrice = new GeItem(314);
        Random rnd = new Random();
        if (ctx.players.local().idle()) {
            for (GroundItem feather : ctx.groundItems.id(314)) {
                Fighter.displayStatus = ("Collecting Feathers");
                if (feather.inViewport()) {
                    int totalFeathers = ctx.backpack.select().name("Feather").count(true);
                    feather.interact("Take", "Feather");
                    Condition.sleep(rnd.nextInt(1000, 1250));
                    int totalFeathers_new = ctx.backpack.select().name("Feather").count(true);
                    int finalFeathers = totalFeathers_new - totalFeathers;
                    Fighter.currentFeathers = Fighter.currentFeathers + finalFeathers;
                    Fighter.displayFeathers = "Feathers collected: " + Fighter.currentFeathers;
                    System.out.println(Fighter.displayFeathers);
                    Fighter.displayMoney = ("Money gained: " + featherPrice.price * Fighter.currentFeathers);
                } else {
                    ctx.camera.turnTo(feather);
                    int totalFeathers = ctx.backpack.select().name("Feather").count(true);
                    feather.interact("Take", "Feather");
                    Condition.sleep(rnd.nextInt(1000, 1250));
                    int totalFeathers_new = ctx.backpack.select().name("Feather").count(true);
                    int finalFeathers = totalFeathers_new - totalFeathers;
                    Fighter.currentFeathers = Fighter.currentFeathers + finalFeathers;
                    Fighter.displayFeathers = "Feathers collected: " + Fighter.currentFeathers;
                    System.out.println(Fighter.displayFeathers);
                    Fighter.displayMoney = ("Money gained: " + featherPrice.price * Fighter.currentFeathers);
                }
            }
        }
        Fighter.killed = 0;
    }
}