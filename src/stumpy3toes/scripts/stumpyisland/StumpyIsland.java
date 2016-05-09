package stumpy3toes.scripts.stumpyisland;

import org.powerbot.script.PaintListener;
import stumpy3toes.api.script.Script;
import stumpy3toes.api.task.TaskSet;
import stumpy3toes.api.task.tasks.CameraTask;
import stumpy3toes.scripts.stumpyisland.gui.GUI;
import stumpy3toes.scripts.stumpyisland.gui.GUIListener;
import stumpy3toes.scripts.stumpyisland.tasks.*;
import stumpy3toes.scripts.stumpyisland.tasks.charactercreation.CharacterCreation;
import stumpy3toes.scripts.stumpyisland.tasks.charactercreation.Gender;

import javax.swing.*;
import java.awt.*;

@org.powerbot.script.Script.Manifest(
        name = "Stumpy Island",
        properties = "author=stumpy3toes; topic=1309819; client=4;",
        description = "Completes tutorial island with options to randomise character and choose ironman status"
)
public class StumpyIsland extends Script implements PaintListener {
    private final Chat chat = new Chat(ctx);
    private final CharacterCreation characterCreation = new CharacterCreation(ctx);
    public final RoofSetting roofSetting = new RoofSetting(ctx);
    private final CameraTask cameraTask = new CameraTask(ctx);
    private final RunescapeGuide runescapeGuide = new RunescapeGuide(ctx);
    private final SurvivalExpert survivalExpert = new SurvivalExpert(ctx);
    private final MasterChef masterChef = new MasterChef(ctx);
    private final Emotes emotes = new Emotes(ctx);
    private final QuestGuide questGuide = new QuestGuide(ctx);
    private final MiningInstructor miningInstructor = new MiningInstructor(ctx);
    private final CombatInstructor combatInstructor = new CombatInstructor(ctx);
    private final FinancialAdvisor financialAdvisor = new FinancialAdvisor(ctx);
    private final BrotherBrace brotherBrace = new BrotherBrace(ctx);
    private final MagicInstructor magicInstructor = new MagicInstructor(ctx);

    @Override
    protected void initTaskSet(TaskSet tasks) {
        tasks.add(chat);
        tasks.add(characterCreation);
        tasks.add(cameraTask);
        tasks.add(runescapeGuide);
        tasks.add(roofSetting);
        tasks.add(survivalExpert);
        tasks.add(masterChef);
        tasks.add(emotes);
        tasks.add(questGuide);
        tasks.add(miningInstructor);
        tasks.add(combatInstructor);
        tasks.add(financialAdvisor);
        tasks.add(brotherBrace);
        tasks.add(magicInstructor);
    }

    @Override
    public void start() {
        super.start();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI(new GUIListener() {
                    @Override
                    public void closed() {
                        ctx.controller.stop();
                    }

                    @Override
                    public void started(AccountType accountType, Gender gender, boolean randomiseCharacter,
                                        boolean enterMainland, boolean userEnterPin) {
                        magicInstructor.accountType = accountType;
                        magicInstructor.enterMainland = enterMainland;
                        magicInstructor.userEnterPin = userEnterPin;
                        magicInstructor.startTime = System.currentTimeMillis();
                        characterCreation.genderChoice = gender;
                        characterCreation.randomiseCreation = randomiseCharacter;
                        ctx.controller.resume();
                    }
                });
            }
        });
        ctx.controller.suspend();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void repaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 518, 20);

        g.setColor(Color.WHITE);
        g.drawString("Task: " + (activeTask() == null ? "[NULL]" : activeTask().name), 0, 15);
        g.drawString("Status: " + (activeTask() == null ? "[NULL]" : activeTask().status()), 160, 15);
    }
}
