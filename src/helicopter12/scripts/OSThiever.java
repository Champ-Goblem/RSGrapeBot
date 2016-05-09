package helicopter12.scripts;

import org.powerbot.script.*;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.*;
import java.util.List;


@Script.Manifest(name="OSThiever", properties = "author=Helicopter12; topic=1300564; client=4; hidden=true;", description="1-99 Thieving; Have food in bank and wear armour!")
public class OSThiever extends PollingScript<ClientContext> implements PaintListener, MessageListener, MouseListener{
    private int optionMode, optionFood;
    private final int npcBounds[] = {-30, 30, -180, -5, -30, 30};
    private final int lobsterID = 379, tunaID = 359, maxTilesAwayToThieve = 7, locationTileDiviation = 25;
    private final int doorIDs[] =  { 34808, 34811, 34807, 34812 };
    private int HP, lvlUps, foodID = lobsterID, startingXP, currentXP, requiredXP, hpToEatAt, energyToRunAt, currentLvl;
    private double successes, failures;
    private boolean hovered = false, thievingStalls = false;
    private int[] thievingIDs;
    private Tile bankTile, locationTile;
    private Npc npcToThieve;
    private GameObject stallToThieve;
    private Image bg;
    private String status = "";
    private state step = state.SETUP;
    private thief optionSelected;
    public static List<MenuOption> thiefMenuOptions = new ArrayList<MenuOption>();
    private final Color color1 = new Color(0, 0, 0, 207);
    private final Color color2 = new Color(255, 255, 255);
    private final Color color4 = new Color(152, 2, 208);
    private final Font font1 = new Font("Arial", 1, 16);
    private final Font font2 = new Font("Arial", 0, 12);

    enum state{
        START, THIEVE, BANK, SETUP, TRAVERSE
    }

    public void start() {

        //Set up our menu
        defineMenuOptions();

        //Set parameters
        startingXP = ctx.skills.experience(Constants.SKILLS_THIEVING);
        requiredXP = ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_THIEVING) + 1);
        currentLvl = ctx.skills.realLevel(Constants.SKILLS_THIEVING);
        hpToEatAt =  Random.nextInt(55,71);
        energyToRunAt = Random.nextInt(40, 70);

        //Load paint background
        try {
            final URL cursorURL = new URL("http://elder.comlu.com/testPaint.png");
            bg = ImageIO.read(cursorURL);
        }catch (Exception ex) {
            log.info("Error: Couldn't load image (" + ex.toString() + ")");
        }

        //Set default state to thieve men:
        setVariablesForNPC(thief.Man);

        status = "Configuring...";
    }

    public void stop() {
        uploadLog();
    }

    public void poll() {

        HP = (int)(((double)ctx.combat.health() / ctx.skills.realLevel(Constants.SKILLS_HITPOINTS)) * 100.0);

        switch(step) {
            case TRAVERSE:
                status = "Traversing...";
                if(ctx.players.local().tile().distanceTo(locationTile) < 12){
                        step = state.THIEVE;
                }else{
                    ctx.movement.step(locationTile);
                    Condition.sleep(Random.nextInt(0,5000));
                }

                //Check if the player is running and if not if they should be running
                if(!ctx.movement.running() && ctx.movement.energyLevel() >= energyToRunAt) {
                    ctx.movement.running(true);
                    Condition.sleep(Random.nextInt(300,1800));
                }
                break;

            case THIEVE:
                status = "Thieving...";
                if(!thievingStalls) {
                    eat(foodID);
                    pickpocketNPC(thievingIDs);
                }else{
                    if (ctx.inventory.select().count() == 28) {
                        step = state.BANK;
                    } else {
                        stealFromStall(thievingIDs, locationTile);
                    }
                }
                antiBan();
                break;

            case BANK:
                status = "Banking...";
                doBanking();
                break;

            case START:
                if (locationTile.distanceTo(ctx.players.local().tile()) < locationTileDiviation) {
                    if (((ctx.inventory.select().isEmpty() && !thievingStalls) || (ctx.inventory.select().count() > 25 && thievingStalls)) && (optionMode != 0)) {
                        step = state.BANK;
                    }else{
                        step = state.THIEVE;
                    }
                }else{
                    step = state.TRAVERSE;
                }
                break;
        }
    }

    public void pickpocketNPC(final int npcIDs[]){
        if(npcToThieve == null || npcToThieve.tile().distanceTo(ctx.players.local().tile()) > maxTilesAwayToThieve || !npcToThieve.valid()){
            npcToThieve = ctx.npcs.select().id(npcIDs).nearest().poll();
            npcToThieve.bounds(npcBounds[0], npcBounds[1], npcBounds[2], npcBounds[3], npcBounds[4], npcBounds[5]);
        }

        if(npcToThieve.valid()){
            if (!npcToThieve.inViewport()) {
                if(npcToThieve.tile().distanceTo(ctx.players.local().tile()) > 9) {
                    ctx.movement.step(npcToThieve.tile());
                    Condition.sleep(Random.nextInt(1200,3000));
                }else {
                    ctx.camera.turnTo(npcToThieve.tile());
                }
            } else {
                if((ctx.menu.opened() && ctx.menu.items().length > 4) || (rightClick(npcToThieve) && ctx.menu.opened() && ctx.menu.items().length > 4)){
                    Condition.sleep(Random.nextInt(200,600));
                    if (ctx.players.local().animation() == -1 && !ctx.players.local().inMotion() && !(npcToThieve.interacting() == ctx.players.local())) {
                        if(hovered){
                            if(!ctx.players.local().inCombat()) {
                                ctx.input.click(true);
                                Condition.sleep(Random.nextInt(200,600));
                            }
                        }else{
                            if(!ctx.players.local().inCombat()) {
                                clickMenuItem("Pickpocket");
                                Condition.sleep(Random.nextInt(200,600));
                            }
                        }
                        hovered = false;
                    }else{
                        if(!hovered) {
                            hoverMenuItem("Pickpocket");
                            hovered = true;
                            Condition.sleep(Random.nextInt(200,600));
                        }
                    }
                }
            }

        }

    }

    public void stealFromStall(final int stallID[], final Tile safeTiles){

        status = "Stealing";
        if (stallToThieve == null || safeTiles.distanceTo(ctx.players.local().tile()) > maxTilesAwayToThieve || !stallToThieve.valid()) {
            stallToThieve = ctx.objects.select().id(stallID).nearest().poll();
        }

        if (safeTiles.equals(ctx.players.local().tile())) {
            if((ctx.menu.opened() && ctx.menu.items().length >= 4) || (stallToThieve.click(false) && ctx.menu.opened() && ctx.menu.items().length >= 4)){
                if (ctx.players.local().animation() == -1 && !ctx.players.local().inMotion() && stallToThieve.valid() && !ctx.players.local().inCombat()) {
                    if(hovered){
                        ctx.input.click(true);
                    }else{
                        clickMenuItem("Steal-from");
                    }
                    hovered = false;
                }else{
                    if(!hovered) {
                        hoverMenuItem("Steal-from");
                        hovered = true;
                    }
                }
            }else{
                if(ctx.menu.opened()) {
                    ctx.menu.close();
                }
            }
        } else {
            if(locationTile.matrix(ctx).inViewport() && !ctx.players.local().inMotion() && locationTile.matrix(ctx).click()) {
                Condition.sleep(Random.nextInt(3000, 4000));
            }else{
                ctx.camera.turnTo(locationTile.tile());
            }
        }
    }

    public void eat(final int mFoodID){
        //Check if inventory is open, if not open it
        if(HP != 0 && HP <= hpToEatAt){
            final Item food = ctx.inventory.select().id(mFoodID).poll();
            if(food.valid()){
                status = "Eating";
                food.click();
            }else{
                if(optionMode != 0) {
                    step = state.BANK;
                }
            }
        }
    }

    public void doBanking(){
        if(bankTile.distanceTo(ctx.players.local().tile()) < 10 && ctx.bank.present() ){
            if(ctx.bank.opened() || (ctx.bank.open() && ctx.bank.opened())){
                if(!thievingStalls) { //Get food we are doing NPCs
                    if (ctx.inventory.select().isEmpty() || ctx.inventory.select().id(foodID).count() == 0) {
                        if (ctx.bank.withdraw(foodID, Bank.Amount.ALL) && ctx.bank.close()) {
                            step = state.THIEVE;
                        } else {
                            if(logout()) {
                                ctx.controller.stop();
                            }
                        }
                    }
                }else{
                    if(ctx.inventory.select().count() > 0){
                        ctx.bank.depositInventory();
                        Condition.sleep(Random.nextInt(400,700));
                    }else{
                        if(ctx.bank.close()) {
                            step = state.THIEVE;
                        }
                    }
                }
            }
        }else{
            if(bankTile.matrix(ctx).reachable()) {
                status = "Walking to Bank";
                ctx.movement.step(bankTile);
                Condition.sleep(Random.nextInt(1050, 5900));
            }else{
                openDoor();
            }
        }
    }

    public boolean rightClick(final Npc npcToClick) {
        Point rndPoint = npcToClick.nextPoint();
        if(ctx.input.move(rndPoint) && npcToClick.contains(rndPoint) && npcToClick.click(false)) {
            return true;
        }
        return false;
    }


    public boolean canThieve(final int lvlRequired){
        if(ctx.skills.realLevel(Constants.SKILLS_THIEVING) >= lvlRequired){
            return true;
        }
        return false;
    }

    public void openDoor(){
        final GameObject door = ctx.objects.select().id(doorIDs).nearest().poll();
        if(door.valid()){
            ctx.camera.turnTo(door.tile());
            if(door.interact("Open",door.name())){
                Condition.sleep(Random.nextInt(1000,2700));
            }
        }
    }

    public void uploadLog() {
        final String url = "http://elder.comlu.com/thief/records.php?time=" + (getTotalRuntime() / 1000) + "&user=" +
                ctx.properties.getProperty("user.id") + "&mode=" + optionSelected + "&lvl=" +
                currentLvl + "&xp=" + (currentXP - startingXP);
        downloadString(url);
    }

    public boolean logout(){
        final Component doorIcon = ctx.widgets.component(161, 31);
        if(doorIcon.valid() && doorIcon.visible() && doorIcon.click(true)) {
            Condition.sleep(Random.nextInt(500,1300));
            final Component logoutButton = ctx.widgets.component(182, 10);
            if(logoutButton.valid() && logoutButton.visible() && logoutButton.click(true)) {
                return  true;
            }
        }
        return false;
    }

    public void hoverMenuItem(final String action){
        ctx.menu.hover(new Filter<MenuCommand>() {
            @Override
            public boolean accept(final MenuCommand menuCommand) {
                return menuCommand.action.equals(action);
            }
        });
    }

    public void clickMenuItem(final String action){
        ctx.menu.click(new Filter<MenuCommand>() {
            @Override
            public boolean accept(final MenuCommand menuCommand) {
                return menuCommand.action.equals(action);
            }
        });
    }

    private void antiBan() {
        int rnd = Random.nextInt(0, 200);
        switch (rnd) {
            case 0:
                ctx.camera.angle(Random.nextInt(0, 300));
                break;
            case 1:
                ctx.camera.pitch(Random.nextInt(0, 71));
                break;
            case 2:
                ctx.camera.angle(Random.nextInt(0, 300));
                ctx.camera.pitch(Random.nextInt(0, 71));
                break;
            case 3:
                ctx.input.move(ctx.input.getLocation().x + Random.nextInt(-200, 200),
                        ctx.input.getLocation().y + Random.nextInt(-300, 250));
                break;
            case 4:
                status = "Breaking...";
                ctx.input.move(Random.nextInt(-10,1), Random.nextInt(-10,1));
                Condition.sleep(Random.nextInt(2000,45000));
                break;
        }
    }

    public void defineMenuOptions() {
        thiefMenuOptions.add(new MenuOption(0, "Man",           1,  10, thief.Man,         true));
        thiefMenuOptions.add(new MenuOption(1, "Farmer",        10, 20, thief.Farmer,      false));
        thiefMenuOptions.add(new MenuOption(2, "Silk Stall",    20, 38, thief.SilkStall,   false));
        thiefMenuOptions.add(new MenuOption(3, "Master Farmer", 38, 40, thief.mFarmer,     false));
        thiefMenuOptions.add(new MenuOption(4, "Guards",        40, 55, thief.Guards,      false));
        thiefMenuOptions.add(new MenuOption(5, "Knights",       55, 70, thief.Knights,     false));
        thiefMenuOptions.add(new MenuOption(6, "Paladins",      70, 80, thief.Paladin,     false));
        thiefMenuOptions.add(new MenuOption(7, "Heroes",        80, 99, thief.Hero,        false));
    }

    @Override
    public void mouseClicked(final MouseEvent m) {
        //Only check mouse clicks if we are in set-up
        if(step == state.SETUP) {
            int index = 0;
            for ( MenuOption opt : thiefMenuOptions ) {
                if(opt.wasClicked(m.getPoint()) && canThieve(opt.getStyle().lvlReq())){
                    opt.selected = true;
                    setVariablesForNPC(opt.getStyle());
                    for(int i = 0; i < thiefMenuOptions.size(); i++) {
                        if(i != index) {
                            thiefMenuOptions.get(i).selected = false;
                        }
                    }
                }
                index++;
            }

            if (m.getX() <= 97 && m.getX() >= 80 && m.getY() <= 243 && m.getY() >= 226) {
                optionFood = 0;
                foodID = lobsterID;
            } else if (m.getX() <= 181 && m.getX() >= 164 && m.getY() <= 243 && m.getY() >= 226) {
                optionFood = 1;
                foodID = tunaID;
            } else if (m.getX() <= 338 && m.getX() >= 255 && m.getY() <= 246 && m.getY() >= 220) {
                //Start thieving
                step = state.START;
            }
        }
    }

    private void setVariablesForNPC(final thief npc) {
        thievingIDs = npc.ID();
        bankTile = npc.bankTile();
        locationTile = npc.location();
        thievingStalls = npc.stall();
        optionMode = npc.option();
        optionSelected = npc;
    }

    @Override
    public void repaint(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        if (step != state.SETUP) {
            drawPaint(g);
        }else{
            drawSettings(g);
        }
        drawMouse(g);

    }

    public void drawPaint(final Graphics g){
        currentXP = ctx.skills.experience(Constants.SKILLS_THIEVING);

        if(bg != null) {
            g.drawImage(bg, 5, 35, null);
        }

        g.setColor(Color.white);
        g.drawString("HP: " + HP + "% (Eat @: " + hpToEatAt + "%)", 20, 100);
        g.drawString("Status: " + status, 20, 120);
        g.drawString("Distance: " + (int)ctx.players.local().tile().distanceTo(locationTile), 20, 140);
        g.drawString("Levels Gained: " + lvlUps, 20, 160);
        final long hr = getTotalRuntime() / 3600000;
        final long min = getTotalRuntime() / 60000;
        final long sec = getTotalRuntime() / 1000;
        g.drawString("Run Time: " + hr + ":" + (min - (hr * 60)) + ":" + (sec - (60 * min)), 20, 180);

        final int xpGained = (currentXP - startingXP);
        final int xphr = (int)(xpGained / ( getTotalRuntime() / 3600000.0));
        g.drawString("XP Gained: " + (xpGained > 1000 ? xpGained/1000 + "k" : xpGained) + " (" + xphr/1000 + "k/hr)", 20, 200);

        int ttl =  (xphr == 0 ? ttl = 0 : (int)(((requiredXP - currentXP) *3600000.0) / xphr));
        final long ttlhr = ttl/3600000;
        final long ttlmin = ttl/60000;
        g.drawString("TTL: "+ ttlhr + "hr : " + (ttlmin - (ttlhr * 60)) + "min" , 20, 220);

        g.drawString("Success Rate: " + ((successes + failures) == 0 ? 0 : (int) (successes / (failures + successes) * 100)) + "%", 20, 240);

        //DEBUGGING ONLY
        drawDebugBoundingBoxes(g);
    }

    public void drawDebugBoundingBoxes(final Graphics g){
        if(npcToThieve != null && npcToThieve.valid() && npcToThieve.inViewport()) {
            npcToThieve.bounds(npcBounds[0], npcBounds[1], npcBounds[2], npcBounds[3], npcBounds[4], npcBounds[5]);
            for (Polygon poly : npcToThieve.triangles()) {
                g.drawPolygon(poly);
            }
        }
    }

    public void drawMouse(final Graphics g){

        final Point pt =  ctx.input.getLocation();
        g.setColor(Color.RED);
        g.fillOval(pt.x - 5, pt.y -5, 10, 10);
    }

    public void drawSettings(final Graphics g){
        g.setColor(color1);
        g.fillRect(9, 10, 335, 242);
        g.setColor(color2);
        g.drawRect(9, 10, 335, 242);
        g.drawLine(336, 210, 18, 210);
        g.setColor(color4);
        g.drawRect(255, 220, 83, 26);
        g.setFont(font1);
        g.drawString("Start", 278, 239);

        //Checkboxes
        g.setColor(color2);
        g.drawRect(80,  226, 17, 17);
        g.drawRect(164, 226, 17, 17);

        g.drawString("Food:", 20, 240);
        g.setFont(font2);
        g.drawString("Lobster", 102, 240);
        g.drawString("Tuna",    188, 240);

        for( MenuOption opt : thiefMenuOptions ) {
            opt.drawOption(g);
        }

        g.setColor(color4);
        switch(optionFood){
            case 0:
                g.fillRect(81, 227, 16, 16);
                break;
            case 1:
                g.fillRect(165, 227, 16, 16);
                break;
        }
    }

    @Override
    public void messaged(final MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (msg.contains("just advanced a thieving level")) {
            lvlUps++;
            requiredXP = ctx.skills.experienceAt(ctx.skills.level(Constants.SKILLS_THIEVING) + 1);
            currentLvl = ctx.skills.realLevel(Constants.SKILLS_THIEVING);
        }else if(msg.contains("coins have been added") || msg.contains("you steal") || msg.contains("you pick")) {
            successes++;
        }else if(msg.contains("you fail to pick")){
            failures++;
        }
    }

    public enum thief{
        Knights  (new Tile(2662,3305,0), new Tile(2653,3283,0), new int[] {23},         55, false, 5),
        Guards   (new Tile(2662,3305,0), new Tile(2653,3283,0), new int[] {32},         40, false, 4),
        Hero     (new Tile(2662,3305,0), new Tile(2653,3283,0), new int[] {21},         80, false, 7),
        Paladin  (new Tile(2662,3305,0), new Tile(2653,3283,0), new int[] {2256},       70, false, 6),
        mFarmer  (new Tile(3080,3250,0), new Tile(3093,3243,0), new int[] {2234},       38, false, 3),
        Man      (new Tile(3223,3218,0), new Tile(0000,0000,0), new int[] {3080, 3083}, 1,  false, 0),
        Farmer   (new Tile(0000,0000,0), new Tile(0000,0000,0), new int[] {000},        10, false, 1),
        SilkStall(new Tile(0000,0000,0), new Tile(2653,3283,0), new int[] {000},        20, true,  2);
        //Need silkstall location tile and ID

        private final int lvlReq, ID[], option;
        private final Tile location, bankTile;
        private final boolean stall;

        thief(final Tile location, final Tile bankTile, final int ID[], final int lvlReq, final boolean stall, final int option) {
            this.ID = ID;
            this.lvlReq = lvlReq;
            this.location = location;
            this.bankTile = bankTile;
            this.stall = stall;
            this.option = option;
        }

        private int[] ID(){ return ID; }

        public int lvlReq() {
            return lvlReq;
        }

        public Tile location() {
            return location;
        }

        public Tile bankTile(){
            return bankTile;
        }

        public boolean stall() { return stall; }

        public int option() { return option; }
    }

    @Override
    public void mouseReleased(final MouseEvent m){

    }

    @Override
    public void mousePressed(final MouseEvent m){

    }

    @Override
    public void mouseEntered(final MouseEvent m){

    }

    @Override
    public void mouseExited(final MouseEvent m){

    }
}

class MenuOption {
    private final Color color2 = new Color(255, 255, 255);
    private final Color color4 = new Color(152, 2, 208);
    private final Font font1 = new Font("Arial", 1, 16);
    public Point checkboxLocation = new Point (0,0);
    public String name, levelRange;
    public boolean selected;
    public int option;
    public OSThiever.thief style;

    public MenuOption(final int option, final String name, final int lvlRangeMin, final int lvlRangeMax, final OSThiever.thief style, boolean selected) {
        this.name = name;
        this.levelRange = "Level "  + lvlRangeMin + " - " + lvlRangeMax;
        this.selected = selected;
        this.option = option;
        this.style = style;
        this.checkboxLocation = new Point(20, 20 + (24 * option));
    }

    public void drawOption(Graphics g) {
        g.setColor(color2);
        g.setFont(font1);
        g.drawString(this.name,       46,  35 +(this.option * 24));
        g.drawString(this.levelRange, 242, 35 +(this.option * 24));
        g.drawRect(this.checkboxLocation.x, this.checkboxLocation.y,  17, 17);
        if(this.selected) {
            g.setColor(color4);
            g.fillRect(21, 21 + (this.option * 24), 16, 16);
        }
    }

    public OSThiever.thief getStyle() { return this.style; }

    public boolean wasClicked(final Point click) {
        if (click.getX() <= 37 && click.getX() >= 20 && click.getY() <= (this.checkboxLocation.y + 17) && click.getY() >= this.checkboxLocation.y) {
            return true;
        }
        return false;
    }
}
