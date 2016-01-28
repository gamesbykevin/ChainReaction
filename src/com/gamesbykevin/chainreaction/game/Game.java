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
import com.gamesbykevin.chainreaction.screen.ScreenManager.State;

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
    
    //is the game over?
    private boolean gameover = false;
    
    //do we display a hint
    private boolean hint = true;
    
    //the mode chosen to play
    private int modeIndex = 0;
    
    //where to render the hint
    private static final int HINT_X = 30;
    
    //where to render the hint
    private static final int HINT_Y = 100;
    
    /**
     * The different level configurations in the game
     */
    public enum Level
    {
    	Level1(5, 1),
    	Level2(10, 2),
    	Level3(15, 3),
    	Level4(20, 5),
    	Level5(25, 7),
    	Level6(30, 10),
    	Level7(35, 15),
    	Level8(40, 21),
    	Level9(45, 27),
    	Level10(50, 33),
    	Level11(55, 44),
    	Level12(60, 55);
    	
    	//the count and goal for the level
    	private final int count, goal;
    	
    	private Level(final int count, final int goal)
    	{
    		//assign our values
    		this.count = count;
    		this.goal = goal;
    	}
    	
    	private int getCount()
    	{
    		return this.count;
    	}
    	
    	private int getGoal()
    	{
    		return this.goal;
    	}
    }
    
    //keep track of the current level
    private int levelIndex = 0;
    
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
     * Is the game over?
     * @return true = yes, false = no
     */
    private boolean hasGameover()
    {
    	return this.gameover;
    }
    
    /**
     * Flag the game over
     * @param gameover true = yes, false = no
     */
    private void setGameover(final boolean gameover)
    {
    	this.gameover = gameover;
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
    
    /**
     * Reset the game
     */
    private void reset() 
    {
    	//make sure we have notified first
    	if (hasNotify())
    	{
    		//store the mode index
    		this.modeIndex = getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Mode); 

    		//display hint if the player does not have a score
    		this.hint = (getPlayer().getScore() == 0);
    		
    		//reset the balls according to the current level
    		getBalls().reset(
    			Level.values()[levelIndex].getCount(), 
    			Level.values()[levelIndex].getGoal()
    		);
    		
        	//flag reset false
        	setReset(false);
        	
        	//reset the player
        	getPlayer().reset();
        	
        	//flag game over false
        	setGameover(false);
    	}
    }
    
    /**
     * Flag reset, we also will flag notify to false if reset is true
     * @param reset true to reset the game, false otherwise
     */
    @Override
    public void setReset(final boolean reset)
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
     * Do we have notify flagged?
     * @return true if we notified the user, false otherwise
     */
    protected boolean hasNotify()
    {
    	return this.notify;
    }
    
    /**
     * Assign the current level index
     * @param levelIndex The desired level index
     */
    public void setLevelIndex(final int levelIndex)
    {
    	this.levelIndex = levelIndex;
    }
    
    /**
     * Get the current level index
     * @return The current level of play
     */
    public int getLevelIndex()
    {
    	return this.levelIndex;
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
            this.paint.setTypeface(Font.getFont(Assets.FontGameKey.Default));
            this.paint.setTextSize(48f);
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
    	
    	switch (modeIndex)
    	{
	    	//chain reaction
	    	case 0:
	        	//if we stopped touching the screen
	        	if (action == MotionEvent.ACTION_UP)
	        	{
	        		//make sure that the player has a turn
	        		if (getPlayer().hasTurn())
	        		{
	        			//turn off hint
	        			this.hint = false;
	        			
	    	    		//update the player's ball location
	    	    		getPlayer().getBall().setX(x);
	    	    		getPlayer().getBall().setY(y);
	    	    		
	    	    		//start expanding the ball
	    	    		getPlayer().getBall().setExpand(true);
	    	    		
	    	    		//take away the player's turn
	    	    		getPlayer().setTurn(false);
	        		}
	        	}
	    		break;
	    		
	    	//capture
	    	case 1:
	        	if (action == MotionEvent.ACTION_MOVE)
	        	{
	        		//make sure the player can play
	        		if (getPlayer().hasTurn())
	        		{
	        			
	        		}
	        	}
	    		break;
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
        	//reset the game
        	reset();
        }
        else
        {
        	//update the balls
        	getBalls().update();
        	
        	//update the player
        	getPlayer().update();
        	
        	//if the player doesn't have a turn, and the ball is dead
        	if (!getPlayer().hasTurn() && getPlayer().getBall().isDead())
        	{
        		//if there are no expanded balls
        		if (getBalls().getExpandedCount() < 1)
        		{
        			//if the game hasn't been flagged over
        			if (!hasGameover())
        			{
        				//flag game over true
        				setGameover(true);
        				
		        		//change the state
		        		getScreen().setState(State.GameOver);
		        		
		        		//find out how many balls were killed, for the score
		        		final int score = Level.values()[getLevelIndex()].getCount() - getBalls().get().size();
		        		
		        		//update the players score
		        		getPlayer().setScore(getPlayer().getScore() + score);
		        		
		        		//make sure we met the goal
		        		if (getBalls().getGoal() < 1)
		        		{
		        			//move to the next level
		        			setLevelIndex(getLevelIndex() + 1);
		        			
		        			//make sure we don't exceed past the last level
		        			if (getLevelIndex() >= Level.values().length)
		        			{
		        				//start back at 0
		        				setLevelIndex(0);
		        				
		        				//update message
		        				getScreen().getScreenGameover().setMessage("You won!!!", "Score: " + getPlayer().getScore(), "New Game");
		        				
		        				//reset the score
				        		getPlayer().setScore(0);
		        			}
		        			else
		        			{
		        				//update message
		        				getScreen().getScreenGameover().setMessage("Success", "", "Next");
		        			}
		        		}
		        		else
		        		{
	        				//start back at 0
		        			setLevelIndex(0);
	        				
	        				//ensure at this point the hint has been removed
	        				hint = false;
	        				
		        			//we did not meet the goal, game over
			        		getScreen().getScreenGameover().setMessage("Game Over", "Score: " + getPlayer().getScore(), "Restart");
			        		
			        		//after we update the message, reset score
			        		getPlayer().setScore(0);
		        		}
		        		
        				//reset text position
        				getScreen().getScreenGameover().reset();
        			}
        		}
        	}
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
    		//if we are to render the hint
    		if (hint)
    		{
    			//render hint text
    			canvas.drawBitmap(
    				(modeIndex == 0) ? Images.getImage(Assets.ImageGameKey.Hint1): Images.getImage(Assets.ImageGameKey.Hint2), 
    				HINT_X, 
    				HINT_Y, 
    				getPaint()
    			);
    		}
    		
    		//make sure game isn't over
			if (!hasGameover())
			{
	    		//do we render the goal or score
	    		switch (modeIndex)
	    		{
		    		//reaction
		    		case 0:
		        		//render the current score progress etc....
		        		canvas.drawText("Goal: " + getBalls().getGoal(), 175, 775, getPaint());
		    			break;
		    			
		    		//capture
		    		case 1:
		        		//render the current score progress etc....
		        		canvas.drawText("Score: " + getPlayer().getScore(), 175, 775, getPaint());
		    			break;
	    		}
			}
    		
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