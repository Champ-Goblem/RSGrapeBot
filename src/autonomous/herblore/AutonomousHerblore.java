package autonomous.herblore;
//**********************************************************
// Script: Autonomous Herblore
//
// User: Autonomous
//
// Author: AutonomousCoding
//
// Date: January 14, 2015
//
// Description: Makes potions or cleans herbs.
//
// Credits to CakeMix for getPrice method
//
//*********************************************************

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.powerbot.script.*;
import org.powerbot.script.rt6.ClientContext;


@Script.Manifest(name = "Autonomous Herblore", description = "Makes potions or cleans herbs.", properties =
        "author=Autonomous; topic=1301191; client=6;")

public class AutonomousHerblore extends PollingScript<ClientContext> implements MessageListener, PaintListener {

    private boolean cleaned = false;
    private boolean made = false;
    private boolean enoughSupplies = true;

    private long startTime = System.currentTimeMillis();
    private long milliseconds;

    private int potsMade, extraPots, totalPots, savedIngredient, cleanedHerbs, potionID, ingredientID, cleanID, grimyID,
            firstHalf, secondHalf, firstHalfID, secondHalfID, seconds, minutes, hours, firstItemCount, lastItemCount,
            cleanedHr, moneyMade, potionPrice, ingredientsCost, ingredientSavings, vialCount;
    private int startXp = ctx.skills.experience(15);

    private double profitHr = 0;

    org.powerbot.script.Random rand = new org.powerbot.script.Random();

    private String stateString;

    private Font font = new Font("Courier New", 1, 10);

    private BufferedImage img = null;

    @Override
    public void poll() {

        final State state = state();
        stateString = state().toString();

        switch (state) {

            case ANTIBAN: {
                //Working on this
                break;
            }

            case BANKING: {

                if (!ctx.bank.opened()){
                    if (rand.nextInt(1, 5) > 3){
                        ctx.camera.turnTo(ctx.bank.nearest());
                    }
                    ctx.bank.open();
                    Condition.sleep(1000);
                }
                vialCount = ctx.bank.select().id(227).count();

                if (ctx.widgets.component(762, 43).click()) {
                    cleaned = false;
                    made = false;
                }

                Condition.sleep(1500);
                break;
            }
            case MAKING: {

                firstHalfID = ctx.backpack.itemAt(0).id();
                secondHalfID = ctx.backpack.itemAt(27).id();

                if (!ctx.backpack.itemAt(0).name().toLowerCase().contains("unf") && !ctx.backpack.itemAt(0).name()
                        .toLowerCase().contains("vial")){
                    ingredientID = ctx.backpack.itemAt(0).id();
                }
                else{
                    ingredientID = ctx.backpack.itemAt(27).id();
                }

                if (ctx.objects.select(10).id(89770).poll().inViewport()){
                    if (rand.nextInt(1, 5) > 3){
                        ctx.camera.turnTo(ctx.objects.select(10).id(89770).poll());
                        ctx.objects.select().id(89770).peek().click("Mix Potions");
                    }else{
                        ctx.objects.select().id(89770).peek().interact("Mix Potions");
                    }
                }
                else {
                    firstHalf = rand.nextInt(0, 13);
                    secondHalf = rand.nextInt(14, 27);

                    if (ctx.backpack.itemAt(firstHalf).interact("Use")) {
                        Condition.sleep(1000);
                        ctx.backpack.itemAt(secondHalf).interact("Use");
                    }

                }
                Condition.sleep(1500);
                if (ctx.widgets.component(1370, 20).valid()) {
                    if (ctx.widgets.component(1370, 20).interact("Make")){
                        made = true;
                    }
                }
                Condition.sleep(1000);
                break;

            }

            case CLEANING: {
                grimyID = ctx.backpack.itemAt(0).id();

                if (ctx.backpack.itemAt(rand.nextInt(0, 27)).click()) {
                    Condition.sleep(2000);
                }

                if (ctx.widgets.component(1370, 20).valid()) {
                    if (ctx.widgets.component(1370, 20).interact("Clean")){
                        cleaned = true;
                    }
                }
                Condition.sleep(1500);
                break;
            }

            case WAITING: {
                Condition.sleep(3000);
                break;
            }

            case GRABBING: {
                /* Working on this.
                if (ctx.objects.select(10).id(89770).poll().inViewport()){
                    if (ctx.objects.select().id(89770).peek().interact("Take Vials")){
                        ctx.input.sendln("27");
                    }
                }
                break;
                */
                break;
            }

            case STOP: {
                ctx.controller.stop();
            }

        }
    }

    private State state() {

        lastItemCount = ctx.backpack.select().id(ctx.backpack.itemAt(27).id()).count();
        firstItemCount = ctx.backpack.select().id(ctx.backpack.itemAt(0).id()).count();

        System.out.println("Getting State");

        if (made && ctx.backpack.select().id(secondHalfID).count() > 0 || cleaned && ctx.backpack.select().id
                (grimyID).count() > 0) {
            System.out.println("Waiting.");
            return State.WAITING;
        }
        else if(rand.nextInt(0, 1000) > 990){
            System.out.println("Antiban");
            return State.ANTIBAN;
        }
        else if (!made && ctx.players.local().animation() == -1 && lastItemCount == 14 && firstItemCount == 14){
            System.out.println("Making");
            return State.MAKING;
        }
        else if (!cleaned && ctx.players.local().animation() == -1 && lastItemCount == 28 && ctx.backpack.select().id
                (ctx.backpack.itemAt(0)).poll().name().toLowerCase().contains("grimy")){
            System.out.println("Cleaning");
            return State.CLEANING;
        }
        else if (enoughSupplies == true && ctx.backpack.select().id(secondHalfID).count() == 0 || enoughSupplies == true && ctx.backpack
                .select().id
                (grimyID).count() == 0 || ctx.backpack.select().id(227).count() == 27){
            System.out.println("Banking");
            return State.BANKING;
        }
        else{
            System.out.println("Stopping.");
            if (ctx.game.logout(false)) {
                ctx.controller.stop();
            }
            return State.STOP;
        }
    }

    private enum State {
        BANKING, MAKING, STOP, WAITING, CLEANING, GRABBING, ANTIBAN
    }

    @Override
    public void messaged(MessageEvent e) {
        final String msg = e.text().toLowerCase();
        if (e.source().isEmpty() && msg.contains("you mix the")) {
            potsMade++;
            potionID = ctx.backpack.itemAt(0).id();
            if (potsMade < 3){
                img = downloadImage("http://i67.tinypic.com/o0ufbq.png");
                try {
                    potionPrice = getPrice(potionID);
                    ingredientsCost = (getPrice(firstHalfID)+ getPrice(secondHalfID));
                    ingredientSavings = getPrice(ingredientID);

                } catch (Exception g){
                    g.printStackTrace();
                }
            }
        }
        else if (e.source().isEmpty() && msg.contains("you mix such a ")){
            extraPots++;
        }
        else if (e.source().isEmpty() && msg.contains("save an ingredient")){
            savedIngredient++;
        }
        else if (e.source().isEmpty() && msg.contains("you clean")){
            cleanedHerbs++;
            cleanID = ctx.backpack.itemAt(0).id();
            if (cleanedHerbs < 3){
                img = downloadImage("http://i67.tinypic.com/vywrjl.png");

                try {
                    ingredientsCost = (getPrice(cleanID) - getPrice(grimyID));

                } catch (Exception f){
                    f.printStackTrace();
                }
            }
        }
        else if (e.source().isEmpty() && msg.contains("item could not be found")){
            enoughSupplies = false;
        }
        totalPots = potsMade + extraPots;
    }

    @Override
    public void repaint(Graphics g) {
        milliseconds = System.currentTimeMillis();
        seconds = ((int)milliseconds - (int)startTime)/1000%60;
        minutes = ((int)milliseconds - (int)startTime)/1000/60%60;
        hours = ((int)milliseconds - (int)startTime)/1000/60/60;

        try {
            if (img != null){
                double hoursPercent = ((double)seconds/3600) + ((double)minutes/60) + (double)hours;
                int xpHr = (int)((ctx.skills.experience(15) - startXp)/hoursPercent)/1000;

                String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                profitHr = (int)(moneyMade/hoursPercent)/1000;
                cleanedHr = (int)(cleanedHerbs/hoursPercent);

                g.setFont(font);
                g.drawImage(img, 0, 314, 280, 75, null);
                g.drawString(time, 43, 346);
                g.drawString(stateString, 50, 377);
                g.drawString(xpHr + "k", 50, 361);

                if (potsMade > 0){
                    moneyMade = (totalPots*potionPrice) - (potsMade*ingredientsCost)
                            + (savedIngredient*ingredientSavings);

                    g.drawString(Integer.toString(totalPots), 211, 346);
                    g.drawString(Integer.toString(savedIngredient), 227, 362);
                }
                else if (cleanedHerbs > 0){
                    moneyMade = cleanedHerbs*ingredientsCost;

                    g.drawString(Integer.toString(cleanedHerbs), 214, 345);
                    g.drawString(Integer.toString(cleanedHr), 203, 362);
                }
                if (profitHr > 999){
                    profitHr = profitHr/1000;
                    g.drawString(String.format("%.2fm", profitHr), 180, 377);
                }
                else {
                    g.drawString((int)profitHr + "k", 180, 377);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getPrice(int id) throws IOException {
        URL url = new URL("http://open.tip.it/json/ge_single_item?item=" + id);
        URLConnection con = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));

        String line = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            line += inputLine;
        }

        in.close();

        if (!line.contains("mark_price"))
            return -1;

        line = line.substring(line.indexOf("mark_price\":\"")
                + "mark_price\":\"".length());
        line = line.substring(0, line.indexOf("\""));

        line = line.replace(",", "");
        return Integer.parseInt(line);
    }

}
