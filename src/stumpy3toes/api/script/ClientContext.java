package stumpy3toes.api.script;

public class ClientContext extends org.powerbot.script.rt4.ClientContext {
    public final Checks checks;
    public final GroundItems groundItems;
    public final Inventory inventory;
    public final Movement movement;
    public final Npcs npcs;
    public final Objects objects;
    public final Players players;

    public final org.powerbot.script.rt4.GroundItems pbGroundItems;
    public final org.powerbot.script.rt4.Inventory pbInventory;
    public final org.powerbot.script.rt4.Npcs pbNpcs;
    public final org.powerbot.script.rt4.Objects pbObjects;
    public final org.powerbot.script.rt4.Players pbPlayers;

    public ClientContext(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
        this.checks = new Checks(this);
        this.groundItems = new GroundItems(this);
        this.inventory = new Inventory(this);
        this.movement = new Movement(this);
        this.npcs = new Npcs(this);
        this.objects = new Objects(this);
        this.players = new Players(this);

        this.pbGroundItems = super.groundItems;
        this.pbInventory = super.inventory;
        this.pbNpcs = super.npcs;
        this.pbObjects = super.objects;
        this.pbPlayers = super.players;
    }
}
