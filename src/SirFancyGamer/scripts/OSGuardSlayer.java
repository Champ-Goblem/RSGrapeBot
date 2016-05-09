package SirFancyGamer.scripts;


import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Script.Manifest(
        name = "Al-Kharid Slayer",
        description = "Kills the Al-Kharid warriors and auto heals/banks for food.",
        properties = "author=SirFancyGamer;topic=1298880;client=4;"
)

public class OSGuardSlayer extends PollingScript<ClientContext> implements PaintListener {

    final int[] eastDoorBounds = {0, 32, -212, 0, 28, 240};
    final int[] westDoorBounds = {112, 144, -224, 0, -104, 108};

    Area castleArea = new Area(new Tile(3296, 3174), new Tile(3288, 3169));

    double maxHealth = 1;

    Font font = new Font("Verdana", Font.BOLD, 12);

    String title = "Al-Kharid Guard Slayer v1.1";
    String expGained = "Exp gained: 0";
    String status = "Status: N/A";

    Color transGrey = new Color(119, 136, 153, 175);


    boolean needBank = true;
    boolean canAttack = false;

    GameObject closedDoor = ctx.objects.select().id(6839).nearest().poll();

    Tile castleTile = new Tile(3293, 3174);
    Tile bankTile = new Tile(3269, 3167);
    boolean foodError = false;

    int foodID;
    Bank bank = new Bank(ctx);

    org.powerbot.script.Tile[] tilesToBank = {new Tile(3293, 3178, 0), new Tile(3290, 3179, 0), new Tile(3287, 3179, 0),
            new Tile(3284, 3179, 0), new Tile(3281, 3179, 0), new Tile(3280, 3176, 0), new Tile(3277, 3173, 0), new Tile(3276, 3170, 0),
            new Tile(3273, 3167, 0), new Tile(3270, 3167, 0)};

    org.powerbot.script.Tile[] tilesToCastle = {new Tile(3271, 3167, 0), new Tile(3274, 3170, 0), new Tile(3275, 3173, 0), new Tile(3277, 3176, 0),
            new Tile(3280, 3179, 0), new Tile(3283, 3179, 0), new Tile(3286, 3179, 0), new Tile(3289, 3179, 0),
            new Tile(3292, 3179, 0), castleTile};

    TilePath pathBank = ctx.movement.newTilePath(tilesToBank);
    TilePath pathCastle = ctx.movement.newTilePath(tilesToCastle);

    Item food = ctx.inventory.select().id(foodID).poll();
    int randChance = 0;
    Random rn = new Random();

    long startingExp = ctx.skills.experience(0) + ctx.skills.experience(1) + ctx.skills.experience(2) + ctx.skills.experience(3)
            + ctx.skills.experience(4) + ctx.skills.experience(6);
    long tempExp = 0;

    Npc guard = ctx.npcs.select().name("Al-Kharid warrior").nearest().poll();

    Tile guardLocation = new Tile(guard.tile().x() + rn.nextInt(3), guard.tile().y() + rn.nextInt(3));


    public void poll() {
        dismissRandomEvent();
        if (((double) ctx.combat.health() / maxHealth) * 100 <= 40) {
            food = ctx.inventory.select().id(foodID).poll();
            food.interact("Eat");
        }
        if (ctx.inventory.select().id(foodID).count() <= 0) {
            if (!ctx.movement.reachable(ctx.players.local().tile(), castleTile)) {
                status = "Status: Opening door!";
                closedDoor = ctx.objects.select().id(6839).nearest().poll();
                if (closedDoor.orientation() == 0 && closedDoor.tile().distanceTo(castleTile) <= 13) {
                    closedDoor.bounds(eastDoorBounds);
                    ctx.camera.turnTo(closedDoor);
                    closedDoor.interact("Open");
                    try {
                        TimeUnit.MILLISECONDS.sleep((long) ((rn.nextDouble() + .55) * 1000));
                    } catch (InterruptedException e) {
                        e.getMessage();
                    }
                } else if (closedDoor.orientation() == 2 && closedDoor.tile().distanceTo(castleTile) <= 13) {
                    closedDoor.bounds(westDoorBounds);
                    ctx.camera.turnTo(closedDoor);
                    closedDoor.interact("Open");
                    try {
                        TimeUnit.MILLISECONDS.sleep((long) ((rn.nextDouble() + .95) * 1000));
                    } catch (InterruptedException e) {
                        e.getMessage();
                    }
                }
            }
            needBank = true;
            canAttack = false;
        }

        if (needBank && !canAttack && ctx.players.local().tile().distanceTo(bankTile) <= 3) {
            bankInteraction();
        } else if (needBank && !canAttack) {
            walkToBank();
        }
        if (!needBank && !canAttack) {
            castleTile = castleArea.getRandomTile();
            walkToCastle();
        }
        if (canAttack) {
            attackGuard();
        }
    }

    public void start() {
        System.out.println("Script started!");
        selectFoodItem();
        castleTile = castleArea.getRandomTile();
        food = ctx.inventory.select().id(foodID).poll();
        maxHealth = ctx.skills.realLevel(Constants.SKILLS_HITPOINTS);
        if (ctx.inventory.select().id(foodID).count() <= 0) {
            needBank = true;
        } else {
            needBank = false;
        }
    }

    @Override
    public void stop() {
        System.out.println("Script stopped!");
        if (foodError) {
            JOptionPane.showMessageDialog(null, "Ran out of food!", "Error: No Food!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void attackGuard() {
        status = "Status: Attacking";
        if (!ctx.players.local().inCombat() || guard.animation() == 836) {
            guard = ctx.npcs.select().name("Al-Kharid warrior").nearest().poll();
            for (int i = 8; i > 0; i--) {
                if (ctx.players.local().inCombat()) {
                    break;
                }
                if (!guard.inCombat() && guard.valid()) {
                    openDoor();
                    if (!guard.inViewport()) {
                        walkToGuard();
                        ctx.camera.turnTo(guard);
                    }

                    if (guard.interact("Attack")) {
                        try {
                            TimeUnit.MILLISECONDS.sleep((long) ((org.powerbot.script.Random.nextDouble(.5, .75) + 2.95) * 1000));
                        } catch (InterruptedException e) {
                            e.getMessage();
                        }
                    }

                    randChance = rn.nextInt(100);
                    if (randChance >= 45) {
                        ctx.input.move(rn.nextInt(350), rn.nextInt(300));
                    }
                }

                if (!ctx.players.local().inCombat() && guard.valid() && guard.inCombat()) {
                    guard = ctx.npcs.name("Al-Kharid warrior").nearest().poll();
                    if (!guard.inViewport()) {
                        walkToGuard();
                        ctx.camera.turnTo(guard);
                    }
                }
            }
        } else {
            randChance = rn.nextInt(100);
            if (randChance >= 97) {
                ctx.input.move(ctx.input.getLocation().x + rn.nextInt(35), ctx.input.getLocation().y + rn.nextInt(35));
            }
            if (randChance <= 3) {
                ctx.input.move(ctx.input.getLocation().x - rn.nextInt(35), ctx.input.getLocation().y - rn.nextInt(35));
            }
            if (randChance >= 30 && randChance <= 32) {
                ctx.input.move(ctx.input.getLocation().x - rn.nextInt(35), ctx.input.getLocation().y + rn.nextInt(35));
            }
            if (randChance >= 40 && randChance <= 42) {
                ctx.input.move(ctx.input.getLocation().x + rn.nextInt(35), ctx.input.getLocation().y - rn.nextInt(35));
            }
            ctx.camera.turnTo(randomGuardLocation());
            try {
                TimeUnit.MILLISECONDS.sleep((long)((org.powerbot.script.Random.nextDouble(0, .75)) * 1000));
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    public Tile randomGuardLocation() {
        if (org.powerbot.script.Random.nextInt(0, 100) <= 45) {
            ctx.camera.pitch(org.powerbot.script.Random.nextInt(30, 65));
            guardLocation = new Tile(guard.tile().x() + rn.nextInt(3), guard.tile().y() + rn.nextInt(3));
        }
        return guardLocation;
    }

    public void walkToCastle() {
        status = "Status: Walking to castle";
        castleTile = castleArea.getRandomTile();
        tilesToCastle = new Tile[]{new Tile(3271, 3167, 0), new Tile(3274, 3170, 0), new Tile(3275, 3173, 0), new Tile(3277, 3176, 0),
                new Tile(3280, 3179, 0), new Tile(3283, 3179, 0), new Tile(3286, 3179, 0), new Tile(3289, 3179, 0),
                new Tile(3292, 3179, 0), castleTile};
        pathCastle = ctx.movement.newTilePath(tilesToCastle);
        if (ctx.players.local().tile().distanceTo(castleTile) > 3) {
            pathCastle.traverse();
            try {
                ctx.camera.turnTo(pathCastle.next());
            } catch (NullPointerException e) {
                e.getMessage();
            }
            canAttack = false;
            try {
                TimeUnit.MILLISECONDS.sleep((long) (rn.nextDouble() + .85) * 1000);
            } catch (InterruptedException e) {
                e.getMessage();
            }
        } else if (ctx.inventory.select().id(foodID).count() > 0 && !needBank) {
            canAttack = true;
        }
    }

    public void walkToBank() {
        status = "Status: Walking to bank";
        if (ctx.inventory.select().id(foodID).count() <= 0) {
            needBank = true;
        }
        if (ctx.players.local().tile().distanceTo(new Tile(3269, 3167)) > 3) {
            pathBank.traverse();
            ctx.camera.turnTo(pathBank.next());
            try {
                TimeUnit.MILLISECONDS.sleep((long) (rn.nextDouble() + .85) * 1000);
            } catch (InterruptedException e) {
                e.getMessage();
            }
        } else {
            return;
        }
    }


    public void bankInteraction() {
        status = "Status: Interacting with bank";
        Npc banker = ctx.npcs.select().name("Banker").nearest().poll();
        try {
            TimeUnit.MILLISECONDS.sleep((long) ((rn.nextDouble() + .75) * 1000));
        } catch (InterruptedException e) {
            e.getMessage();
        }
        if (bank.opened()) {
            if (bank.select().id(foodID).count() <= 0) {
                foodError = true;
                bank.close();
                ctx.controller.stop();
            } else if (bank.withdraw(foodID, 28)) {
                needBank = false;
                bank.close();
            }
        } else if (needBank) {
            ctx.camera.turnTo(banker);
            banker.interact("Bank");
        }
    }

    public void selectFoodItem() {
        boolean success = false;
        while (!success) {
            try {
                foodID = Integer.parseInt(JOptionPane.showInputDialog(null, "Type in your desired food ID (Integer value only)", "Select Food",
                        JOptionPane.QUESTION_MESSAGE));
            } catch (NumberFormatException e) {
                foodID = -1;
            }

            if (foodID >= 0) {
                success = true;
            }
        }
    }

    public void walkToGuard() {
        if (!guard.inViewport()) {
            status = "Status: Walking to guard!";
            ctx.movement.findPath(new Tile(guard.tile().x() + org.powerbot.script.Random.nextInt(1, 3),
                    guard.tile().y() + org.powerbot.script.Random.nextInt(1, 3))).traverse();
            if (ctx.players.local().inMotion()) {
                try {
                    TimeUnit.MILLISECONDS.sleep((long) (org.powerbot.script.Random.nextDouble(.35, .75) + .85) * 1000);
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            }
        }
    }

    public void openDoor() {
        if (!guard.tile().matrix(ctx).reachable() && guard.valid()) {
            status = "Status: Opening door!";
            closedDoor = ctx.objects.select().id(6839).nearest().poll();
            if (closedDoor.orientation() == 0 && closedDoor.tile().distanceTo(castleTile) <= 13) {
                closedDoor.bounds(eastDoorBounds);
                ctx.camera.turnTo(closedDoor);
                closedDoor.interact("Open");
                try {
                    TimeUnit.MILLISECONDS.sleep((long) ((org.powerbot.script.Random.nextDouble(.5, .95) + 1) * 1000));
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            } else if (closedDoor.orientation() == 2 && closedDoor.tile().distanceTo(castleTile) <= 13) {
                closedDoor.bounds(westDoorBounds);
                ctx.camera.turnTo(closedDoor);
                closedDoor.interact("Open");
                try {
                    TimeUnit.MILLISECONDS.sleep((long) ((rn.nextDouble() + .95) * 1000));
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            }
        }
    }

    public void dismissRandomEvent() {
        /* Credit to @laputa.  URL: https://www.powerbot.org/community/topic/1292825-random-event-dismisser/  */
        Npc randomNpc = ctx.npcs.select().within(2.0).select(new Filter<Npc>() {

            @Override
            public boolean accept(Npc npc) {
                return npc.overheadMessage().contains(ctx.players.local().name());
            }

        }).poll();

        if (randomNpc.valid()) {
            String action = randomNpc.name().equalsIgnoreCase("genie") ? "Talk-to" : "Dismiss";
            if (randomNpc.interact(action)) {
                try {
                    TimeUnit.MILLISECONDS.sleep((long) (org.powerbot.script.Random.nextDouble(3, 3.5) * 1000));
                } catch (InterruptedException e) {
                    e.getMessage();
                }
            }

        }
    }


    @Override
    public void repaint(Graphics graphics) {
        tempExp = ctx.skills.experience(0) + ctx.skills.experience(1) + ctx.skills.experience(2) + ctx.skills.experience(3)
                + ctx.skills.experience(4) + ctx.skills.experience(6);
        expGained = "Exp gained: " + (tempExp - startingExp);
        Graphics2D g2d = (Graphics2D)graphics;
        guard.draw(g2d, 255);
        graphics.setFont(font);
        graphics.setColor(transGrey);
        graphics.drawRect(0, 0, 230, 75);
        graphics.fillRect(0, 0, 230, 75);
        graphics.setColor(Color.black);
        graphics.getFont();
        graphics.drawString(title, 15, 15);
        graphics.drawString(status, 15, 35);
        graphics.drawString(expGained, 15, 55);

    }
}
