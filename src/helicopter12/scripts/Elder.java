//Project: Elder Log Collector
//Date: 12/20/15
//Author: Ryan(Helicopter12)
package helicopter12.scripts;

import org.powerbot.script.*;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;

import org.powerbot.script.Random;

import javax.imageio.ImageIO;

@Script.Manifest(name = "Elder Log Collector", properties = "author=Helicopter12; topic=1296549; client=6;", description = "An autonomous Elder tree chopping and banking system used for money making")
public class Elder extends PollingScript<ClientContext> implements PaintListener, MessageListener {
    private String status = "Starting...";
    private final int elderID = 87508, jujuPotionID = 32759;
    private final int deadElderID = 87509, elderLogID = 29556;
    private int logsCollected = 0;
    private int elderPrice;
    private long[] respawn = new long[6];
    private long[] spawnTime = new long[6];
    private final Tile lodeStoneBankTile = new Tile(2899, 3544, 0);
    private final Tile bankTile = new Tile(2887, 3536, 0);
    private final String locationNames[] = { "Varrock:", "Seer's:", "E-Ville:", "E-Peak:", "Draynor1:", "Draynor2:" };
    private final int shiftValues[] = { 11, 9, 5, 16, 4, 4 };
    private final Tile elderLocation[] = { new Tile(3260, 3367, 0), new Tile(2737, 3407, 0), new Tile(3094, 3457, 0),
            new Tile(2324, 3598, 0), new Tile(3059, 3319, 0), new Tile(3098, 3218, 0) };
    private final Tile lodeStoneTile[] = { new Tile(3214, 3376, 0), new Tile(2689, 3482, 0), new Tile(3067, 3505, 0),
            new Tile(2366, 3479, 0), new Tile(3105, 3298, 0), new Tile(3105, 3298, 0) };
    private final int lodeStoneComponentID[] = { 21, 19, 15, 24, 14, 14 };
    private final Tile lFixTile = new Tile(3233, 3221, 0);
    private final int homeWidgetID = 1465, homeComponentID = 56, lodeStoneWidgetID = 1092, bankComponentID = 12;
    private final int hatchetIDs[] = { 1359, 1353, 1355, 1349, 1351, 1361, 1357, 6739 };
    private boolean depositAll = false;
    private int locationCount;
    private int randomizedLocations[] = new int[4];
    private ScriptState step = ScriptState.START;
    private Image cursor;
    private String examineNames = "";
    private int currentElderLocation = 0;
    private int startingXP, currentXP;

    static enum ScriptState {
        START, BEGIN_CHOP_SEQUENCE, CHOP_LODESTONE_TELEPORT, CHOP_LODESTONE_WAIT, WALK_TO_TREE, CHOP_TREE, BEGIN_BANK_SEQUENCE, BANK_LODESTONE_TELEPORT, BANK_LODESTONE_WAIT, WALK_TO_BANK, PERFORM_BANK,
    }

    @Override
    public void start() {
        status = "Configuring...";

        // Load Cursor
        try {
            final URL cursorURL = new URL("http://elder.comlu.com/cursor.png");
            cursor = ImageIO.read(cursorURL);
        } catch (Exception ex) {
            log.info("Error: Couldn't load cursor");
        }

        // Check for empty inventory
        if (shouldBank()) {
            // Inventory full - go deposit everything
            depositAll = true;
            step = ScriptState.BEGIN_BANK_SEQUENCE;
            status = "Inventory Full";
        }

        // Shuffle locations
        int[] locations = new int[6];
        for (int i = 0; i < 6; ++i) {
            locations[i] = i;
        }
        for (int i = 5; i >= 0; --i) {
            int swapIdx = Random.nextInt(0, i + 1);
            int tmp = locations[i];
            locations[i] = locations[swapIdx];
            locations[swapIdx] = tmp;
        }
        // Check if location is unlocked in shuffled order
        locationCount = 0;
        for (int loc : locations) {
            if (lodestoneUnlocked(loc)) {
                randomizedLocations[locationCount++] = loc;
                if (locationCount == 4) {
                    break;
                }
            }
        }

        // Grab the price of elder logs
        elderPrice = getPrice();

        //Get the starting XP
        startingXP = ctx.skills.experience(Constants.SKILLS_WOODCUTTING);

        // Flag ready state
        if (status.equals("Configuring...") && ctx.game.loggedIn()) {
            step = ScriptState.BEGIN_CHOP_SEQUENCE;
        }
    }

    @Override
    public void stop() {
        log.info("Stopped - Uploading data");
        String url = "http://elder.comlu.com/records.php?time=" + (getTotalRuntime() / 1000) + "&collected="
                + logsCollected + "&profit=" + ((logsCollected * elderPrice) / 1000) + "K";
        downloadString(url);
        log.info("Stopped - Data sent");
    }

    @Override
    public void poll() {
        // Begin step sequence for easy debugging
        switch (step) {
            case BEGIN_CHOP_SEQUENCE:
                // Click the Home Lodestone button on minimap
                final Component homeButton = ctx.widgets.component(homeWidgetID, homeComponentID);
                if (homeButton.valid() && homeButton.visible()) {
                    status = "Opening map";
                    if (homeButton.click()) {
                        Condition.sleep(Random.nextInt(700, 1000));
                        step = ScriptState.CHOP_LODESTONE_TELEPORT;
                    }
                }
                break;
            case CHOP_LODESTONE_TELEPORT:
                // Teleport using the lodestone interface
                final Component lodestoneInt = ctx.widgets.component(lodeStoneWidgetID,
                        lodeStoneComponentID[randomizedLocations[currentElderLocation]]);
                if (lodestoneInt.valid() && lodestoneInt.visible()) {
                    status = "Teleporting...";
                    if (lodestoneInt.click()) {
                        Condition.sleep(Random.nextInt(700, 1000));
                        step = ScriptState.CHOP_LODESTONE_WAIT;
                    }
                }
                break;
            case CHOP_LODESTONE_WAIT:
                // If bot accidently misclicked lumbridge and not draynor, try again
                if (lFixTile.tile().equals(ctx.players.local().tile())) {
                    if (ctx.players.local().animation() == -1) {
                        step = ScriptState.BEGIN_CHOP_SEQUENCE;
                        break;
                    }
                }
                // While we are teleporting wait
                if (!ctx.players.local().tile().equals(lodeStoneTile[randomizedLocations[currentElderLocation]])) {
                    Condition.sleep(200);
                } else {
                    step = ScriptState.WALK_TO_TREE;
                }
                break;
            case WALK_TO_TREE:
                // If we are not at the desired tile then walk to it
                if (elderLocation[randomizedLocations[currentElderLocation]].distanceTo(ctx.players.local().tile()) < 10) {
                    step = ScriptState.CHOP_TREE;
                } else {
                    status = "Walking to Elder";
                    ctx.movement.step(elderLocation[randomizedLocations[currentElderLocation]]);
                    Condition.sleep(Random.nextInt(700, 1000));
                }
                break;
            case CHOP_TREE:
                // Handle the chopping now
                final GameObject elder = ctx.objects.select(10).id(elderID).poll();
                if (elder.valid()) {
                    status = "Chopping";
                    if (!elder.inViewport()) {
                        ctx.camera.turnTo(elder.tile());
                    }
                    if (ctx.players.local().animation() == -1) {
                        Condition.sleep(1000);
                        if (ctx.players.local().animation() == -1) {
                            elder.interact("Chop down", elder.name());
                        }
                    }
                    antiBan();
                } else {
                    status = "Elder Dead";
                    // Tree is dead so start the timer and decide to bank or move
                    // the to next location
                    triggerTimer();

                    if (shouldBank()) {
                        step = ScriptState.BEGIN_BANK_SEQUENCE;
                    } else {
                        currentElderLocation = nextLocation(currentElderLocation);
                        step = ScriptState.BEGIN_CHOP_SEQUENCE;
                    }
                }
                break;
            case BEGIN_BANK_SEQUENCE:
                // Begin bank sequence by opening the lodestone map
                final Component homeButton2 = ctx.widgets.component(homeWidgetID, homeComponentID);
                if (homeButton2.valid() && homeButton2.visible()) {
                    status = "Opening map";
                    if (homeButton2.click()) {
                        Condition.sleep(Random.nextInt(700, 1000));
                        step = ScriptState.BANK_LODESTONE_TELEPORT;
                    }
                }
                break;
            case BANK_LODESTONE_TELEPORT:
                // Teleport to the bank using the lodestone interface
                final Component lodestoneInterface = ctx.widgets.component(lodeStoneWidgetID, bankComponentID);
                if (lodestoneInterface.valid() && lodestoneInterface.visible()) {
                    status = "Teleporting(Bank)";
                    if (lodestoneInterface.click()) {
                        Condition.sleep(Random.nextInt(700, 1000));
                        step = ScriptState.BANK_LODESTONE_WAIT;
                    }
                }
                break;
            case BANK_LODESTONE_WAIT:
                // While we are teleporting to the bank wait
                if (!ctx.players.local().tile().equals(lodeStoneBankTile)) {
                    Condition.sleep(200);
                } else {
                    step = ScriptState.WALK_TO_BANK;
                }
                break;
            case WALK_TO_BANK:
                // If we are not at the bank tile then walk to it
                if (ctx.bank.inViewport()) {
                    ctx.camera.pitch(Random.nextInt(35, 65));
                    step = ScriptState.PERFORM_BANK;
                } else if (bankTile.distanceTo(ctx.players.local().tile()) > 4) {
                    status = "Walking to Bank";
                    ctx.movement.step(bankTile);
                    Condition.sleep(Random.nextInt(700, 1200));
                } else {
                    step = ScriptState.PERFORM_BANK;
                }
                break;
            case PERFORM_BANK:
                // Begin Banking
                if (!ctx.bank.opened()) {
                    status = "Opening Bank";
                    ctx.bank.open();
                } else if (!ctx.backpack.select().id(elderLogID).isEmpty()) {
                    status = "Depositing Logs";
                    final Item logs = ctx.backpack.id(elderLogID).poll();
                    logs.interact("Deposit-All", logs.name());

                } else if (depositAll) {
                    status = "Depositing...";
                    ctx.bank.depositInventory();
                    Condition.sleep(200);
                    depositAll = false;
                } else {
                    status = "Closing Bank";
                    ctx.bank.close();
                    Condition.sleep(Random.nextInt(500, 1000));
                    currentElderLocation = nextLocation(currentElderLocation);
                    step = ScriptState.BEGIN_CHOP_SEQUENCE;
                }
                break;
            default:
                break;
        }

    }

    private final Color color1 = new Color(0, 0, 0, 220);
    private final Color color2 = new Color(255, 215, 0);
    private final Color color3 = new Color(255, 215, 0, 229);
    private final Color color4 = new Color(255, 153, 0, 226);
    private final Color color5 = new Color(255, 255, 255);
    private final BasicStroke stroke1 = new BasicStroke(2);
    private final BasicStroke stroke2 = new BasicStroke(1);
    private final Font font1 = new Font("Mistral", 1, 25);
    private final Font font2 = new Font("Arial", 1, 10);
    private final Font font3 = new Font("Arial", 1, 15);
    private final Font font4 = new Font("Arial", 0, 15);
    private final Font font5 = new Font("Arial", 0, 9);

    @Override
    public void repaint(Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;

        currentXP = ctx.skills.experience(Constants.SKILLS_WOODCUTTING);
        final int xpGained = (currentXP - startingXP);
        final int xphr = (int)(xpGained / ( getTotalRuntime() / 3600000.0));
        final int profit = (logsCollected * elderPrice);

        for (int i = 0; i < 6; ++i) {
            long spawn = (respawn[i] - getTotalRuntime()) / 1000;
            spawnTime[i] = Math.max(spawn, 0);
        }
        g.setColor(color1);
        g.fillRoundRect(7, 6, 185, 208, 16, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(7, 6, 185, 208, 16, 16);
        g.setColor(color3);
        g.setStroke(stroke2);
        g.drawLine(13, 33, 181, 33);
        g.setFont(font1);
        g.setColor(color4);
        g.drawString("Elder", 72, 29);
        g.setFont(font2);
        g.setColor(color5);
        g.drawString("v2.03", 129, 27);
        g.setFont(font3);
        g.drawString("Status:", 16, 54);
        g.drawString("Logs Collected:", 16, 72);
        g.drawString("Profit:", 16, 93);
        g.drawString("Exp: ", 16, 113);

        int spawnBaseCoord = 132;
        int spawnIncrement = 20;
        for (int i = 0; i < locationCount; ++i) {
            int coord = spawnBaseCoord + spawnIncrement * i;
            int tree = randomizedLocations[i];
            g.setFont(font3);
            g.drawString(locationNames[tree], 16, coord);
            long minutes = spawnTime[tree] / 60;
            long seconds = spawnTime[tree] % 60;
            g.setFont(font4);
            g.drawString(minutes + ":" + seconds, 90, coord);
        }

        g.setFont(font4);
        g.drawString(status, 69, 54);
        g.drawString("" + logsCollected, 132, 72);
        g.drawString((profit < 1000 ? profit : profit/1000 + "K") + " ("
                        + ((int) (((logsCollected * elderPrice) / 1000) * 3600000.0) / getTotalRuntime()) + "k/hr)",
                66, 93);
        g.drawString((xpGained > 1000 ? xpGained/1000 + "k" : xpGained) + " (" + xphr/1000 + "k/hr)", 66, 113);


        g.setFont(font5);
        final long hr = getTotalRuntime() / 3600000;
        final long min = getTotalRuntime() / 60000;
        final long sec = getTotalRuntime() / 1000;
        g.drawString(hr + ":" + (min - (hr * 60)) + ":" + (sec - (60 * min)), 82, 208);
        Point p = ctx.input.getLocation();
        if (cursor != null) {
            g.drawImage(cursor, p.x - 4, p.y - 2, null);
        }
    }

    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (msg.equals("you get some elder logs.")) {
            logsCollected++;
        } else if (msg.contains("too full to")) {
            // Start banking routine
            step = ScriptState.BEGIN_BANK_SEQUENCE;
            triggerTimer();
        }
    }

    private int getPrice() {
        final String content = downloadString("http://itemdb-rs.runescape.com/viewitem.ws?obj=29556");
        if (content == null) {
            return 0;
        }

        final String[] lines = content.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("Current Guide Price")) {
                String rString = lines[i].replace("<h3>Current Guide Price <span title='", "");
                rString = rString.substring(rString.indexOf("'>") + 2, 20);
                rString = rString.replace(",", "");
                return Integer.parseInt(rString.trim());
            }
        }
        return 0;
    }

    private boolean shouldBank() {
        boolean ret = true;
        if (ctx.backpack.select().count() < 22) {
            ret = false;
        }
        return ret;
    }

    private int nextLocation(int c) {
        antiPattern();

        int next;

        for (next = (c + 1) % locationCount; next != c; next = (next + 1) % locationCount) {
            if (spawnTime[randomizedLocations[next]] <= 1) {
                break;
            }
        }

        if (spawnTime[randomizedLocations[next]] > 1) {
            for (int i = 0; i < locationCount; ++i) {
                if (spawnTime[randomizedLocations[i]] < spawnTime[randomizedLocations[next]]) {
                    next = i;
                }
            }
        }

        return next;
    }

    private void antiPattern() {
        final int rnd = Random.nextInt(0, 10);
        if (rnd == 2) {
            status = "Shifting Pattern";
            int[] nums = new int[6];
            outer: for (int i = 0; i < 6; ++i) {
                nums[i] = -1;
                for (int j = 0; j < locationCount; ++j) {
                    if (randomizedLocations[j] == i) {
                        continue outer;
                    }
                }
                if (lodestoneUnlocked(i)) {
                    nums[i] = i;
                }
            }
            Arrays.sort(nums);
            int start = 0;
            while (start < 6 && nums[start] == -1) {
                start++;
            }
            if (start < 6) {
                int chosen = nums[Random.nextInt(start, 6)];
                log.info("Replacing tree " + randomizedLocations[currentElderLocation] + " with " + chosen);
                randomizedLocations[currentElderLocation] = chosen;
            }
        }
    }

    private void antiBan() {
        int rnd = Random.nextInt(0, 200);
        switch (rnd) {
            case 0:
                status = "Anti-ban(1)";
                ctx.camera.angle(Random.nextInt(0, 300));
                break;
            case 1:
                status = "Anti-ban(1)";
                ctx.camera.pitch(Random.nextInt(0, 71));
                break;
            case 10:
                status = "Anti-ban(1)";
                ctx.camera.angle(Random.nextInt(0, 300));
                break;
            case 20:
                status = "Anti-ban(2)";
                final GameObject obj = ctx.objects.select().within(10).shuffle().poll();
                if (obj.valid() && obj.inViewport()) {
                    if (!examineNames.contains(obj.name())) {
                        obj.interact("Examine", obj.name());
                        examineNames += " " + obj.name();
                    }
                } else {
                    ctx.input.move(ctx.input.getLocation().x + Random.nextInt(-200, 200),
                            ctx.input.getLocation().y + Random.nextInt(-300, 250));
                }
                break;
            case 30:
                status = "Anti-ban(3)";
                final Npc npc = ctx.npcs.select().within(10).shuffle().poll();
                if (npc.valid() && npc.inViewport()) {
                    if (!examineNames.contains(npc.name())) {
                        npc.interact("Examine", npc.name());
                        examineNames += " " + npc.name();
                    }
                } else {
                    ctx.input.move(ctx.input.getLocation().x + Random.nextInt(-200, 200),
                            ctx.input.getLocation().y + Random.nextInt(-300, 250));
                }
                break;
            case 40:
                status = "Anti-ban(4)";
                final Player plyr = ctx.players.select().within(10).shuffle().poll();
                final int rndTab = Random.nextInt(0, 4);
                if (plyr.valid() && plyr.inViewport() && !ctx.players.local().name().equals(plyr.name())) {
                    plyr.interact("Examine", plyr.name());
                    Condition.sleep(Random.nextInt(100, 1500));
                    if (rndTab != 0) {
                        final Component cmp = ctx.widgets.component(1560, rndTab);
                        if (cmp.valid() && cmp.visible()) {
                            cmp.click();
                        }
                    }
                    Condition.sleep(Random.nextInt(1000, 10000));

                    final Component widg = ctx.widgets.component(1560, 22);
                    if (widg.valid() && widg.visible()) {
                        widg.click();
                    }
                } else {
                    ctx.input.move(ctx.input.getLocation().x + Random.nextInt(-200, 200),
                            ctx.input.getLocation().y + Random.nextInt(-300, 250));
                }
                break;
        }
    }

    private void triggerTimer() {
        int tree = randomizedLocations[currentElderLocation];
        long currentTime = getTotalRuntime();
        if (respawn[tree] < currentTime) {
            respawn[tree] = currentTime + 600000;
        } else {
            // Seeing tree we already knew was dead
        }
    }

    // Credit to LodeStone class
    private boolean lodestoneUnlocked(int check) {
        boolean ret = true;
        if (ctx.varpbits.varpbit(3, shiftValues[check], 1) != 1) {
            ret = false;
        }
        return ret;
    }
}