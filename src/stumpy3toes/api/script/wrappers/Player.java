package stumpy3toes.api.script.wrappers;

import org.powerbot.script.Tile;
import stumpy3toes.api.script.ClientContext;

public class Player extends Actor {
    private final org.powerbot.script.rt4.Player player;

    public Player(ClientContext ctx, org.powerbot.script.rt4.Player player) {
        super(ctx, player);
        this.player = player;
    }

    @Override
    public Player setReachableTile(Tile tile) {
        return (Player)super.setReachableTile(tile);
    }

    @Override
    public Player setBounds(int[] bounds) {
        return (Player)super.setBounds(bounds);
    }

    @Override
    public String name() {
        return player.name();
    }

    @Override
    public int combatLevel() {
        return player.combatLevel();
    }

    public int team() {
        return player.team();
    }

    public int[] appearance() {
        return player.appearance();
    }

    @Override
    public boolean valid() {
        return player.valid();
    }

    @Override
    public String toString() {
        return player.toString();
    }
}
