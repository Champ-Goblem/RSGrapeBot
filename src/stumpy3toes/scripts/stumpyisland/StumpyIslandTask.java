package stumpy3toes.scripts.stumpyisland;

import org.powerbot.script.Tile;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.task.Task;

public abstract class StumpyIslandTask extends Task {
    private static final int PROGRESS_VARPBIT_ID = 281;

    private final int progressVarpbitMin;
    private final int progressVarpbitMax;

    private final Tile[] reachableTiles;

    public StumpyIslandTask(ClientContext ctx, String name, int progressVarpbitMin, int progressVarpbitMax,
                            Tile... reachableTiles) {
        super(ctx, name);
        this.progressVarpbitMin = progressVarpbitMin;
        this.progressVarpbitMax = progressVarpbitMax;
        this.reachableTiles = reachableTiles;
    }

    private boolean inArea() {
        for (Tile tile : reachableTiles) {
            if (ctx.movement.reachable(tile)) {
                return true;
            }
        }
        return false;
    }

    protected final int progress() {
        return ctx.varpbits.varpbit(PROGRESS_VARPBIT_ID);
    }

    @Override
    public boolean checks() {
        int progress = progress();
        return (progress >= progressVarpbitMin && progress <= progressVarpbitMax) || inArea();
    }

    @Override
    protected final void poll() {
        int progress = progress();
        if (progress >= progressVarpbitMax) {
            leaveArea();
        } else {
            execute(progress);
        }
    }

    protected abstract void execute(int progress);

    protected abstract void leaveArea();
}
