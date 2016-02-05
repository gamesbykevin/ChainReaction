package com.gamesbykevin.chainreaction.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Font;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.chainreaction.assets.Assets;
import com.gamesbykevin.chainreaction.balls.Balls;
import com.gamesbykevin.chainreaction.panel.GamePanel;
import com.gamesbykevin.chainreaction.player.Player;
import com.gamesbykevin.chainreaction.screen.OptionsScreen;
import com.gamesbykevin.chainreaction.screen.ScreenManager;
import com.gamesbykevin.chainreaction.screen.ScreenManager.State;
import com.gamesbykevin.chainreaction.storage.score.Score;

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
    
    //the paint object we use to render the hint
    private Paint paintHint;
    
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
    
    /**
     * Reaction mode
     */
    public static final int MODE_REACTION = 0;
    
    /**
     * Capture mode
     */
    public static final int MODE_CAPTURE = 1;
    
    //keep track of the current level
    private int levelIndex = 0;
    
    //the player's coordinates when moving
    private float playerX, playerY;
    
    //keep track of the time to fade the hint text away
    private long time;
    
    /**
     * The amount of time it takes to fade the hint text away completely (milliseconds)
     */
    private static final long HINT_FADE_DURATION = 3750L;
    
    //track the best score for each mode index
    private Score scoreboard;
    
    //the duration we want to vibrate the phone for
    private static final long VIBRATION_DURATION = 500L;
    
    /**
     * Create our game object
     * @param screen The main screen
     * @throws Exception
     */
    public Game(final ScreenManager screen) throws Exception
    {
        //our main screen object reference
        this.screen = screen;
        
        //create a new score board
        this.scoreboard = new Score(screen.getScreenOptions(), screen.getPanel().getActivity());
        
        //create a new player
        this.player = new Player();
        
        //create balls container
        this.balls = new Balls(this.player);
    }
    
    private void setHint(final boolean hint)
    {
    	if (!hint && this.hint || hint)
    	{
    		//store time to check duration
    		this.time = System.currentTimeMillis();
    		
    		//reset back to 100% visibility
    		this.getPaintHint().setAlpha(255);
    	}
    	
    	//store the value
    	this.hint = hint;
    }
    
    private boolean hasHint()
    {
    	return this.hint;
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
     * Get the score board
     * @return The object containing the personal best records
     */
    private Score getScoreboard()
    {
    	return this.scoreboard;
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
    		//display hint if the player does not have a score
    		setHint(getPlayer().getScore() == 0);

        	//flag reset false
        	setReset(false);
        	
        	//reset the player
        	getPlayer().reset();
        	
        	//flag game over false
        	setGameover(false);
        	
    		//reset depending on the game mode
    		switch (getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Mode))
    		{
				case MODE_REACTION:
					
					//store the mode
					this.modeIndex = MODE_REACTION;
					
		    		//reset the balls according to the current level
		    		getBalls().reset(
		    			Level.values()[getLevelIndex()].getCount(), 
		    			Level.values()[getLevelIndex()].getGoal(),
		    			this.modeIndex
		    		);
					break;
					
				case MODE_CAPTURE:
					
					//store the mode
					this.modeIndex = MODE_CAPTURE;
					
		    		//reset the balls
		    		getBalls().reset(0, 0, this.modeIndex);
		    		
		    		//place player ball in the middle
		    		getPlayer().getBall().setX(GamePanel.WIDTH / 2);
		    		getPlayer().getBall().setY(GamePanel.HEIGHT / 2);
					break;
	    		
    		}
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
    
    /**
     * Get the paint object
     * @return The paint object we use for the hint
     */
    private Paint getPaintHint()
    {
    	if (this.paintHint == null)
    	{
    		this.paintHint = new Paint();
    		this.paintHint.setAlpha(255);
    	}
    	
    	return this.paintHint;
    }
    
    @Override
    public void update(final int action, final float x, final float y) throws Exception
    {
    	//if reset we can't continue
    	if (hasReset())
    		return;
    	
    	//update motion event based on game mode
    	switch (modeIndex)
    	{
	    	//chain reaction
	    	case MODE_REACTION:
	    		
	        	//if we stopped touching the screen
	        	if (action == MotionEvent.ACTION_UP)
	        	{
	        		//make sure that the player has a turn
	        		if (getPlayer().hasTurn())
	        		{
	        			//turn off hint
	        			setHint(false);
	        			
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
	    	case MODE_CAPTURE:
	    		
        		//make sure the player can play
        		if (getPlayer().hasTurn())
        		{
		    		if (action == MotionEvent.ACTION_DOWN)
		    		{
	    				//store the initial coordinates
	    				this.playerX = x;
	    				this.playerY = y;
		    		}
		    		else if (action == MotionEvent.ACTION_MOVE)
		        	{
		    			//calculate the x,y movement
		    			final float xDiff = x - this.playerX; 
		    			final float yDiff = y - this.playerY;
		    			
		        		//compare the difference to move the player's ball
		    			getPlayer().getBall().setX(getPlayer().getBall().getX() + xDiff);
		    			getPlayer().getBall().setY(getPlayer().getBall().getY() + yDiff);
		    			
		    			//here we will keep the ball on screen
		    			if (getPlayer().getBall().getX() > GamePanel.WIDTH - (getPlayer().getBall().getWidth() / 2))
		    			{
		    				getPlayer().getBall().setX(GamePanel.WIDTH - (getPlayer().getBall().getWidth() / 2));
		    			}
		    			else if (getPlayer().getBall().getX() < (getPlayer().getBall().getWidth() / 2))
		    			{
		    				getPlayer().getBall().setX((getPlayer().getBall().getWidth() / 2));
		    			}
		    			
		    			//here we will keep the ball on screen
		    			if (getPlayer().getBall().getY() > GamePanel.HEIGHT - (getPlayer().getBall().getHeight() / 2))
		    			{
		    				getPlayer().getBall().setY(GamePanel.HEIGHT - (getPlayer().getBall().getHeight() / 2));
		    			}
		    			else if (getPlayer().getBall().getY() < (getPlayer().getBall().getHeight() / 2))
		    			{
		    				getPlayer().getBall().setY((getPlayer().getBall().getHeight() / 2));
		    			}
		    			
		    			//assign the new location
		    			this.playerX = x;
		    			this.playerY = y;
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
        	//store the score
        	final int tmp = getPlayer().getScore();
        	
        	//update the balls
        	getBalls().update();
        	
        	//update the player
        	getPlayer().update();
        	
        	//if we have a score, ensure the hint is turned off
        	if (tmp == 0 && getPlayer().getScore() > 0)
        		setHint(false);
        	
        	//if we are to hide the hint
        	if (!hasHint())
        	{
        		if (getPaintHint().getAlpha() > 0)
        		{
        			//determine the current alpha transparency
        			int alpha = (int)(((float)(HINT_FADE_DURATION - (System.currentTimeMillis() - this.time)) / (float)HINT_FADE_DURATION) * 255); 
        			
        			//make sure we maintain a valid value
        			if (alpha < 0)
        				alpha = 0;
        			
        			//assign the transparency
        			getPaintHint().setAlpha(alpha);
        		}
        	}
        	
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
		        		
		        		switch (this.modeIndex)
		        		{
			        		case MODE_REACTION:
			        			
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
				        				//update the score if it is a personal best
				        				final boolean result = getScoreboard().updateScore(this.modeIndex, getPlayer().getScore());
				        				
				        				//start back at 0
				        				setLevelIndex(0);
				        				
				        				//update message
				        				getScreen().getScreenGameover().setMessage(false, result, "Score: " + getPlayer().getScore(), (result) ? "" : "High: " + getScoreboard().getHighScore(this.modeIndex), "New Game");
				        				
				        				//reset the score
						        		getPlayer().setScore(0);
						        		
										//stop all other sound
										Audio.stop();
										
										//play game over sound
										Audio.play(Assets.AudioGameKey.Lose);
				        			}
				        			else
				        			{
				        				//update message
				        				getScreen().getScreenGameover().setMessage(true, false, "Score: " + getPlayer().getScore(), "", "Next");
				        				
										//stop all other sound
										Audio.stop();
										
										//play game over sound
										Audio.play(Assets.AudioGameKey.Win);
				        			}
				        		}
				        		else
				        		{
			        				//start back at 0
				        			setLevelIndex(0);
			        				
			        				//ensure at this point the hint has been removed
			        				hint = false;
			        				
			        				//update the score if it is a personal best
			        				final boolean result = getScoreboard().updateScore(this.modeIndex, getPlayer().getScore());
			        				
				        			//we did not meet the goal, game over
					        		getScreen().getScreenGameover().setMessage(false, result, "Score: " + getPlayer().getScore(), (result) ? "" : "High: " + getScoreboard().getHighScore(this.modeIndex), "Retry");
					        		
					        		//after we update the message, reset score
					        		getPlayer().setScore(0);
					        		
									//vibrate the phone
									vibrate();
									
									//stop all other sound
									Audio.stop();
																		
									//play game over sound
									Audio.play(Assets.AudioGameKey.Lose);
				        		}
				        		break;
				        		
			        		case MODE_CAPTURE:
			        			
		        				//ensure at this point the hint has been removed
		        				hint = false;
		        				
		        				//update the score if it is a personal best
		        				final boolean result = getScoreboard().updateScore(this.modeIndex, getPlayer().getScore());
		        				
			        			//assign game over message
				        		getScreen().getScreenGameover().setMessage(false, result, "Score: " + getPlayer().getScore(), (result) ? "" : "High: " + getScoreboard().getHighScore(this.modeIndex), "Retry");
				        		
				        		//after we update the message, reset score
				        		getPlayer().setScore(0);
				        		
								//vibrate the phone
								vibrate();
								
								//stop all other sound
								Audio.stop();
								
								//play the explosion
								Audio.play(Assets.AudioGameKey.Explosion);
								
								//play game over sound
								Audio.play(Assets.AudioGameKey.Lose);
			        			break;
		        		}
		        		
        				//reset text position
        				getScreen().getScreenGameover().reset();
        			}
        		}
        	}
        }
    }
    
    /**
     * Vibrate the phone if the setting is enabled
     */
    private void vibrate()
    {
		//make sure vibrate option is enabled
		if (getScreen().getScreenOptions().getIndex(OptionsScreen.Key.Vibrate) == 0)
		{
    		//get our vibrate object
    		Vibrator v = (Vibrator) getScreen().getPanel().getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    		 
			//vibrate for a specified amount of milliseconds
			v.vibrate(VIBRATION_DURATION);
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
    		//only render the hint if we can see it
    		if (getPaintHint().getAlpha() > 0)
    		{
				//render hint text
				canvas.drawBitmap(
					(modeIndex == MODE_REACTION) ? Images.getImage(Assets.ImageGameKey.Hint1): Images.getImage(Assets.ImageGameKey.Hint2), 
					HINT_X, 
					HINT_Y, 
					getPaintHint()
				);
    		}
    		
    		//make sure game isn't over
			if (!hasGameover())
			{
	    		//do we render the goal or score
	    		switch (modeIndex)
	    		{
		    		//reaction
		    		case MODE_REACTION:
		        		//render the current score progress etc....
		        		canvas.drawText("Goal: " + getBalls().getGoal(), 175, 775, getPaint());
		    			break;
		    			
		    		//capture
		    		case MODE_CAPTURE:
		        		//render the current score progress etc....
		        		canvas.drawText("Score: " + getPlayer().getScore(), 145, 775, getPaint());
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
        
        if (this.scoreboard != null)
        {
        	this.scoreboard.dispose();
        	this.scoreboard = null;
        }
    }
}