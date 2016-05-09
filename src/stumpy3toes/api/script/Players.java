package stumpy3toes.api.script;

import org.powerbot.script.rt4.PlayerQuery;
import stumpy3toes.api.script.wrappers.Player;

import java.util.ArrayList;
import java.util.List;

public class Players extends PlayerQuery<Player> {
    public final ClientContext ctx;

    public Players(final ClientContext ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    private Player wrap(org.powerbot.script.rt4.Player player) {
        return new Player(ctx, player);
    }

    @Override
    public List<Player> get() {
        final List<Player> players = new ArrayList<Player>();
        for (org.powerbot.script.rt4.Player player : ctx.pbPlayers.get()) {
            players.add(wrap(player));
        }
        return players;
    }

    public Player local() {
        return wrap(ctx.pbPlayers.local());
    }

    @Override
    public Player nil() {
        return wrap(ctx.pbPlayers.nil());
    }
}
