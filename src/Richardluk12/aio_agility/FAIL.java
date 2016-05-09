package Richardluk12.aio_agility;

import org.powerbot.script.Tile;

/**
 * Created by Rich on 4/19/2016.
 */
public class FAIL{
    public boolean failable;
    public String failMessage;
    public String successMessage;
    public String startMessage;
    public Obstacle failObstacle;

    public FAIL(boolean _fail){
        this.failable = _fail;
        this.startMessage = null;
        this.failMessage = null;
        this.successMessage = null;
        this.failObstacle = null;
    }

    public FAIL(boolean _fail, String _startMessage, String _failMessage, String _successMessage, Obstacle _failObstacle){
        this.failable = _fail;
        this.startMessage = _startMessage;
        this.failMessage = _failMessage;
        this.successMessage = _successMessage;
        this.failObstacle = _failObstacle;
    }
}

