package stumpy3toes.api.script;

import org.powerbot.script.Condition;
import stumpy3toes.api.script.wrappers.Actor;

public class Checks extends ClientAccessor {
    public Checks(ClientContext ctx) {
        super(ctx);
    }

    public final Condition.Check animated(final int animationID) {
        return new Condition.Check() {
            @Override
            public boolean poll() {
                return ctx.players.local().animation() == animationID;
            }
        };
    }

    public final Condition.Check interacting(final Actor actor) {
        return new Condition.Check() {
            @Override
            public boolean poll() {
                return ctx.players.local().interacting().equals(actor);
            }
        };
    }

    public final Condition.Check animated = new Condition.Check() {
        @Override
        public boolean poll() {
            return ctx.players.local().animated();
        }
    };

    public final Condition.Check chatting = new Condition.Check() {
        @Override
        public boolean poll() {
            return ctx.chat.chatting();
        }
    };

    public final Condition.Check idle = new Condition.Check() {
        @Override
        public boolean poll() {
            return ctx.players.local().idle();
        }
    };

    public final Condition.Check inCombat = new Condition.Check() {
        @Override
        public boolean poll() {
            return ctx.players.local().inCombat();
        }
    };
}
