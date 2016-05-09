package sd8z.scripts.sbonfire.jobs;

import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.rt6.ClientContext;
import sd8z.core.script.Job;
import sd8z.scripts.sbonfire.SBonfire;

import java.util.concurrent.Callable;

public class Bank extends Job<SBonfire, ClientContext> {

    private final int id;

    public Bank(SBonfire script, int id) {
        super(script);
        this.id = id;
    }

    @Override
    public boolean activate() {
        return ctx.backpack.select().id(id).isEmpty();
    }

    @Override
    public void execute() {
        if (ctx.bank.inViewport() || ctx.bank.opened()) {
            if (ctx.bank.open()) {
                if (ctx.bank.select().id(id).isEmpty()) {
                    script.log.info("[sBonfire] Logs not found in the bank, stopping.");
                    script.stop();
                    ctx.controller.stop();
                    return;
                }
                if (!ctx.backpack.select().isEmpty())
                    ctx.bank.depositInventory();
                if (ctx.bank.withdraw(id, org.powerbot.script.rt6.Bank.Amount.ALL)) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !activate();
                        }
                    });
                    ctx.bank.close();
                }
            }
        } else {
            Locatable bank = ctx.bank.nearest();
            if (bank.tile().distanceTo(ctx.players.local()) >= 7) {
                ctx.movement.step(bank);
            } else {
                ctx.camera.turnTo(bank);
            }
        }

    }
}
