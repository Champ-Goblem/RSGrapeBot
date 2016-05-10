package com.example;


import org.powerbot.script.rt6.ClientContext;


import org.powerbot.script.PollingScript;

import org.powerbot.script.Script;
import org.powerbot.script.Tile;

import org.powerbot.script.rt6.TilePath;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.Npc;

@Script.Manifest(name="Grape Hoarder", description="Kills Edgeville Guards or Varrock Gaurds for grapes")
public class RSBot extends PollingScript<ClientContext> {
    public static final int GRAPE = 1987;
    public static final int[] EGUARD = {3407, 298, 299, 3408};
    public static final int[] VGUARD = {5919, 5920};
    private final Tile[] PATHBANK = {

            new Tile(3094, 3493, 0),
            new Tile(3097, 3497, 0),
            new Tile(3103, 3501, 0),
            new Tile(3105, 3507, 0),
            new Tile(3114, 3507, 0),
            new Tile(3114, 3514, 0)
    };

    private final Tile[] PATHVBANK = {
            new Tile(3213, 3465, 0),
            new Tile(3213, 3455, 0),
            new Tile(3213, 3439, 0),
            new Tile(3211, 3433, 0),
            new Tile(3209, 3429, 0),
            new Tile(3197, 3429, 0),
            new Tile(3189, 3429, 0),
            new Tile(3186, 3438, 0),
            new Tile(3183, 3438, 0)
    };

    private TilePath VGtoVB, EGtoEB, VBtoVG, EBtoEG;
    private int GrapesCollected = 0;
    @Override
        public void start() {

            VGtoVB = new TilePath(ctx, PATHVBANK);
            VBtoVG = new TilePath(ctx, PATHBANK).reverse();
            EGtoEB = new TilePath(ctx, PATHBANK).reverse();
            EBtoEG = new TilePath(ctx, PATHBANK);
        }
    @Override
        public void poll() {

        switch (state()) {
            case BANK:
                if (!ctx.bank.opened()) {
                    ctx.bank.open();
                } else if (!ctx.backpack.select().id(GRAPE).isEmpty()) {
                    ctx.bank.depositInventory();
                } else {
                    ctx.bank.close();
                }
                break;


            case Walk_To_VBank:
                VGtoVB.traverse();
                break;

            case Walk_To_VGuards:
                VBtoVG.traverse();
                break;

            case Walk_To_EBank:
                EGtoEB.traverse();
                break;

            case Walk_To_EGuards:

                EBtoEG.traverse();
                break;

            case Attack_EGUARDS:

                    Npc guard = ctx.npcs.select().id(EGUARD).nearest().poll();

                    guard.interact("Attack");
                    ctx.camera.turnTo(guard);

                try {
                    Thread.sleep(12000);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                break;
            case Grape_Collect:
                final GroundItem item = ctx.groundItems.select().id(GRAPE).within(10).poll();
                item.interact("Take", "Grapes");
                break;
        }
    }
    public State state() {
        if (ctx.players.local().healthPercent() == 10) {
            ctx.controller.stop();
        }
        if (ctx.bank.opened()) {
        return State.BANK;
        }

        if (ctx.backpack.select().count() < 28 ) {

            if (!ctx.npcs.select().id(EGUARD).within(10).isEmpty()) {
                if (!ctx.groundItems.select().id(GRAPE).isEmpty()) {
                    return State.Grape_Collect;
                }
                return State.Attack_EGUARDS;
            } else {
                return State.Walk_To_EGuards;
            }
        }


     else if (!ctx.bank.inViewport()) {
            if (ctx.backpack.select().count() == 28) {
                return State.Walk_To_EBank;
            }
        } else if (ctx.bank.nearest().tile().distanceTo(ctx.players.local()) < 10) {
            return State.BANK;
        }
        return null;
    }

   private enum State {
        BANK, Walk_To_VBank, Walk_To_VGuards, Walk_To_EBank, Walk_To_EGuards, Attack_EGUARDS, Grape_Collect
    }
}