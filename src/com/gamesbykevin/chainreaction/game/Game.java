package com.gamesbykevin.chainreaction.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Font;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.chainreaction.assets.Assets;
import com.gamesbykevin.chainreaction.balls.Balls;
import com.gamesbykevin.chainreaction.player.Player;
import com.gamesbykevin.chainreaction.screen.OptionsScreen;
import com.gamesbykevin.chainreaction.screen.ScreenManager;

/**
 * The main game logic will happen here
 * @author ABRAHAM
 */
public final class Game implements IGame
{
    //our main screen object reference
    private final ScreenManager screen;
    
    //paint object to draw text
    private Paint paint;
    
    //is the game being reset
    private boolean reset = false;
    
    //has the player been notified (has the user seen the loading screen)
    private boolean notify = false;
    
    //the balls in the game
    private Balls balls;
    
    //the player
    private Player player;
    
    /**
     * Create our game object
     * @param screen The main screen
     * @throws Exception
     */
    public Game(final ScreenManager screen) throws Exception
    {
        //our main screen object reference
        this.screen = screen;
        
        //create a new player
        this.player = new Player();
        
        //create balls container
        this.balls = new Balls(this.player.getBall());
    }
    
    /**
     * Get the main screen object reference
     * @return The main screen object reference
     */
    public ScreenManager getScreen()
    {
        return this.screen;
    }
    
    /**
     * Get the balls in play
     * @return The balls container
     */
    public Balls getBalls()
    {
    	return this.balls;
    }
    
    /**
     * Get the player
     * @return The human controlled player
     */
    public Player getPlayer()
    {
    	return this.player;
    }
    
    @Override
    public void reset() throws Exception
    {
        //flag reset
    	setReset(true);
    }
    
    /**
     * Flag reset, we also will flag notify to false if reset is true
     * @param reset true to reset the game, false otherwise
     */
    private void setReset(final boolean reset)
    {
    	this.reset = reset;
    	
    	//flag that the user has not been notified, since we are resetting
    	if (hasReset())
    		setNotify(false);
    }
    
    /**
     * Do we have reset flagged?
     * @return true = yes, false = no
     */
    public boolean hasReset()
    {
    	return this.reset;
    }
    
    /**
     * Flag notify
     * @param notify True if we notified the user, false otherwise
     */
    private void setNotify(final boolean notify)
    {
    	this.notify = notify;
    }
    
    /**
     * Do we have notify?
     * @return true if we notified the user, false otherwise
     */
    protected boolean hasNotify()
    {
    	return this.notify;
    }
    
    /**
     * Get the paint object
     * @return The paint object used to draw text in the game
     */
    public Paint getPaint()
    {
    	//if the object has not been created yet
    	if (this.paint == null)
    	{
            //create new paint object
            this.paint = new Paint();
            //this.paint.setTypeface(Font.getFont(Assets.FontGameKey.Default));
            this.paint.setTextSize(24f);
            this.paint.setColor(Color.WHITE);
            this.paint.setLinearText(false);
    	}
    	
        return this.paint;
    }
    
    @Override
    public void update(final int action, final float x, final float y) throws Exception
    {
    	//if reset we can't continue
    	if (hasReset())
    		return;
    	
    	//if we stopped touching the screen
    	if (action == MotionEvent.ACTION_UP)
    	{
    		//make sure that the player has lives
    		if (getPlayer().getLives() > 0)
    		{
	    		//update the player's ball location
	    		getPlayer().getBall().setX(x);
	    		getPlayer().getBall().setY(y);
	    		
	    		//start expanding the ball
	    		getPlayer().getBall().setExpand(true);
    		}
    	}
    }
    
    /**
     * Update game
     * @throws Exception 
     */
    public void update() throws Exception
    {
        //if we are to reset the game
        if (hasReset())
        {
        	//make sure we have notified first
        	if (hasNotify())
        	{
        		//create ships based on the game mode
        		switch (getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Mode))
        		{
        		
        		}
        		
        		//reset the balls
        		getBalls().reset(55);
        		
	        	//flag reset false
	        	setReset(false);
	        	
	        	//set the player's lives
	        	getPlayer().setLives(1);
        	}
        }
        else
        {
        	//update the balls
        	getBalls().update();
        	
        	//update the player
        	getPlayer().update();
        }
    }
    
    /**
     * Render game elements
     * @param canvas Where to write the pixel data
     * @throws Exception 
     */
    @Override
    public void render(final Canvas canvas) throws Exception
    {
    	if (hasReset())
    	{
			//render loading screen
			canvas.drawBitmap(Images.getImage(Assets.ImageMenuKey.Splash), 0, 0, null);
			
			//flag that the user has been notified
			setNotify(true);
    	}
    	else
    	{
    		//render the player
    		getPlayer().render(canvas);
    		
    		//render the balls
    		getBalls().render(canvas);
    	}
    }
    
    @Override
    public void dispose()
    {
        this.paint = null;
        
        if (this.balls != null)
        {
        	this.balls.dispose();
        	this.balls = null;
        }
        
        if (this.player != null)
        {
        	this.player.dispose();
        	this.player = null;
        }
    }
}