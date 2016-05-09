package stumpy3toes.api.task.tasks;

import org.powerbot.script.Random;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.task.Task;

public class CameraTask extends Task {
    public CameraTask(ClientContext ctx) {
        super(ctx, "Camera");
    }

    public CameraTask(ClientContext ctx, int priority) {
        this(ctx);
        setPriority(priority);
    }

    @Override
    public boolean checks() {
        setStatus("Adjusting camera pitch");
        return ctx.camera.pitch() < 70;
    }

    @Override
    public void poll() {
        ctx.camera.pitch(Random.nextInt(70, 99));
    }
}
