package com.gamesbykevin.chainreaction.balls;

import java.util.ArrayList;
import java.util.List;

import com.gamesbykevin.chainreaction.assets.Assets;
import com.gamesbykevin.chainreaction.common.ICommon;
import com.gamesbykevin.chainreaction.game.Game;
import com.gamesbykevin.chainreaction.panel.GamePanel;
import com.gamesbykevin.chainreaction.player.Player;

import android.graphics.Canvas;

public class Balls implements ICommon 
{
	//list of balls
	private List<Ball> balls;

	/**
	 * Default starting size of the balls
	 */
	public static final int START_DIMENSION = 24;
	
	/**
	 * The speed of the ball movement
	 */
	private static final double BALL_VELOCITY = 3.0;
	
	//reference to the player
	private final Player player;
	
	//the number we have to achieve to win
	private int goal;
	
	//the game mode playing
	private int modeIndex = 0;
	
	//time to track when spawning a new ball
	private long time;
	
	/**
	 * The range of size in balls for capture mode
	 */
	private static final int SPAWN_RANGE = 20;
	
	/**
	 * The pixel amount increase for capture mode
	 */
	private static final int CAPTURE_INCREASE = 2;
	
	/**
	 * The delay until a new ball can be spawned for capture mode (milliseconds)
	 */
	private static final long SPAWN_DELAY = 3500;
	
	/**
	 * Default constructor
	 */
	public Balls(final Player player) 
	{
		//the reference to the player's ball
		this.player = player;
		
		//create list to contain the balls
		this.balls = new ArrayList<Ball>();
	}

	/**
	 * Get the goal
	 * @return The remaining balls needed to reach the goal
	 */
	public int getGoal()
	{
		return this.goal;
	}
	
	/**
	 * Set the goal
	 * @param goal The remaining balls needed to complete the level
	 */
	public void setGoal(final int goal)
	{
		this.goal = goal;
		
		//make sure we are good
		if (getGoal() < 0)
			setGoal(0);
	}
	
	/**
	 * Reset the balls in play<br>
	 * @param count The number of balls we want to add
	 * @param goal The number of balls we have to achieve
	 * @param modeIndex The game mode playing
	 */
	public void reset(final int count, final int goal, final int modeIndex)
	{
		//store the game mode
		this.modeIndex = modeIndex;
		
		//update the time
		this.time = System.currentTimeMillis();
		
		//store the goal
		setGoal(goal);
		
		//remove any existing balls
		get().clear();
		
		//create temporary list of ball types
		List<Ball.Type> types = new ArrayList<Ball.Type>();
		
		while (get().size() < count)
		{
			//make sure we have all types to choose from
			if (types.isEmpty())
			{
				for (Ball.Type type : Ball.Type.values())
				{
					types.add(type);
				}
			}
			
			//pick a random type
			final int randomIndex = GamePanel.RANDOM.nextInt(types.size());
			
			//create a new ball of random type
			Ball ball = new Ball(Ball.Type.values()[randomIndex]);
			
			//remove type from list
			types.remove(randomIndex);
			
			//set ball size
			ball.setDimension(START_DIMENSION);
			
			//temporarily expand for collision detection
			ball.setExpand(true);
			
			//pick a location that isn't occupied by a ball
			while (true)
			{
				//pick random location
				ball.setX(GamePanel.RANDOM.nextInt(GamePanel.WIDTH));
				ball.setY(GamePanel.RANDOM.nextInt(GamePanel.HEIGHT));
				
				//if there is no collision, exit the loop
				if (!hasCollision(ball))
					break;
			}
			
			//remove expansion
			ball.setExpand(false);
			
			//pick random velocity
			ball.setDX(GamePanel.RANDOM.nextBoolean() ? BALL_VELOCITY : -BALL_VELOCITY);
			ball.setDY(GamePanel.RANDOM.nextBoolean() ? BALL_VELOCITY : -BALL_VELOCITY);
			
			//add ball to the list
			get().add(ball);
		}
	}
	
	/**
	 * Get the list of current balls in play
	 * @return The list of current
	 */
	public List<Ball> get()
	{
		return this.balls;
	}
	
	/**
	 * Count the total number of balls that have been expanded.
	 * @return the total number of balls that have expanded true
	 */
	public int getExpandedCount()
	{
		//keep track of count
		int count = 0;
		
		//check each ball
		for (Ball ball : get())
		{
			//if the ball hasn't been flagged dead and has expanded
			if (!ball.isDead() && ball.hasExpand())
				count++;
		}
		
		//return our result
		return count;
	}
	
	@Override
	public void dispose() 
	{
		if (this.balls != null)
		{
			for (int i = 0; i < this.balls.size(); i++)
			{
				this.balls.get(i).dispose();
				this.balls.set(i, null);
			}
			
			this.balls.clear();
			this.balls = null;
		}
	}

	/**
	 * Do we have collision?<br>
	 * Here we are checking to see if 2 balls have collided that are not dead, and at least 1 expanding
	 * @param ball The ball we want to check against the balls list
	 * @return true if the specified ball collides with any ball and both are not dead, with at least 1 ball expanding
	 */
	private boolean hasCollision(final Ball ball)
	{
		//if this ball is dead, we can't check for collision
		if (ball.isDead())
			return false;
		
		//check each ball in our list
		for (Ball tmp : get())
		{
			//if this is the same ball, skip it
			if (tmp.hasId(ball))
				continue;
			
			//if this ball is dead, we can't check for collision
			if (tmp.isDead())
				continue;
			
			//check if we have collision
			if (tmp.hasCollision(ball))
			{
				//if either is expanding, return true
				if (tmp.hasExpand() || ball.hasExpand())
					return true;
			}
		}
		
		//we did not find any collisions
		return false;
	}
	
	@Override
	public void update() throws Exception 
	{
		if (get() != null)
		{
			//do we play a collision sound
			boolean sound = false;
			
			//update each ball
			for (int i = 0; i < get().size(); i++)
			{
				//get the current ball
				Ball ball = get().get(i);
				
				//update ball
				ball.update();
				
				switch (this.modeIndex)
				{
					//reaction mode
					case Game.MODE_REACTION:
						/**
						 * If this ball collides with any others that aren't dead and expanding.<br>
						 * We will also expand this ball
						 */
						if (hasCollision(ball))
						{
							//if this ball has not expanded yet, take away one from our goal
							if (!ball.hasExpand())
							{
								//decrease the goal
								setGoal(getGoal() - 1);
								
								//flag true to play random sound effect
								sound = true;
							}
							
							ball.setExpand(true);
						}
						
						//if the ball is dead, remove it
						if (ball.isDead())
						{
							//remove from list
							get().remove(i);
							
							//adjust index
							i--;
						}
						else
						{
							//make sure the player ball isn't dead
							if (!player.getBall().isDead())
							{
								//check if the ball collides with the player's ball
								if (ball.hasCollision(player.getBall()))
								{
									//if the player's ball is expanding, we will expand this ball
									if (player.getBall().hasExpand())
									{
										//if this ball has not expanded yet, take away one from our goal
										if (!ball.hasExpand())
										{
											//decrease the goal
											setGoal(getGoal() - 1);
											
											//flag true to play random sound effect
											sound = true;
										}
										
										ball.setExpand(true);
									}
								}
							}
						}
						break;
						
					//capture mode
					case Game.MODE_CAPTURE:
						
						//if ball is off screen we want to remove it
						if (ball.getDX() < 0 && ball.getX() < -ball.getWidth() || 
							ball.getDX() > 0 && ball.getX() > GamePanel.WIDTH + ball.getWidth() ||
							ball.getDY() < 0 && ball.getY() < -ball.getHeight() ||
							ball.getDY() > 0 && ball.getY() > GamePanel.HEIGHT + ball.getHeight())
						{
							//remove from list
							get().remove(i);
							
							//adjust index
							i--;
						}
						
						//check if the ball has collided with the player's ball
						if (ball.hasCollision(player.getBall()))
						{
							//make sure the player's ball is not dead
							if (!player.getBall().isDead())
							{
								//check if the player's ball is bigger
								if (player.getBall().getWidth() > ball.getWidth())
								{
									//remove from list
									get().remove(i);
									
									//adjust index
									i--;
									
									//increase the player's ball size
									player.getBall().setDimension(player.getBall().getWidth() + CAPTURE_INCREASE);
									
									//increase the player's score
									player.setScore(player.getScore() + 1);
									
									//to keep it challenging immediately spawn another ball
									spawnBall();
									
									//play collision sound
									sound = true;
								}
								else
								{
									//the player's turn is over
									player.setTurn(false);
									
									//ball is dead
									player.getBall().setDead(true);
									
									//add the explosion
									player.getBall().addExplosion();
								}
							}
						}
						break;
				}
			}
			
			//if we are to play a collision sound effect
			if (sound)
				Assets.playCollisionSound();
			
			//check if we need to spawn any new balls for capture mode
			if (this.modeIndex == Game.MODE_CAPTURE)
			{
				//check if time to add a ball
				if (System.currentTimeMillis() - this.time >= SPAWN_DELAY)
				{
					//also make sure the player has a turn
					if (this.player.hasTurn())
					{
						//update last time update
						this.time = System.currentTimeMillis();
					
						//add a new ball
						spawnBall();
					}
				}
			}
		}
	}

	/**
	 * Spawn a new ball, used in Capture mode
	 */
	private void spawnBall()
	{
		//pick a random width difference within 10 pixels of the player's ball
		int width = GamePanel.RANDOM.nextInt(SPAWN_RANGE * 2) - SPAWN_RANGE;
		
		//create a new ball of random type
		Ball ball = new Ball(Ball.Type.values()[GamePanel.RANDOM.nextInt(Ball.Type.values().length)]);
		
		//set ball size
		ball.setDimension(player.getBall().getWidth() + width);
		
		//make sure width is large enough
		if (ball.getWidth() < (player.getBall().getWidth() / 2))
			ball.setDimension(player.getBall().getWidth() / 2);
		
		//where to spawn the ball
		if (GamePanel.RANDOM.nextBoolean())
		{
			if (GamePanel.RANDOM.nextBoolean())
			{
				//place ball on west side
				ball.setX(-ball.getWidth());
				
				//velocity will be east
				ball.setDX((GamePanel.RANDOM.nextDouble() * BALL_VELOCITY) + BALL_VELOCITY);
			}
			else
			{
				//place ball on east side
				ball.setX(GamePanel.WIDTH + ball.getWidth());
				
				//velocity will be west
				ball.setDX((GamePanel.RANDOM.nextDouble() * -BALL_VELOCITY) - BALL_VELOCITY);
			}
			
			//pick random location
			ball.setY(GamePanel.RANDOM.nextInt(GamePanel.HEIGHT));
			
			//pick random y velocity
			if (GamePanel.RANDOM.nextBoolean())
			{
				ball.setDY((GamePanel.RANDOM.nextDouble() * -BALL_VELOCITY) - BALL_VELOCITY);
			}
			else
			{
				ball.setDY((GamePanel.RANDOM.nextDouble() * BALL_VELOCITY) + BALL_VELOCITY);
			}
		}
		else
		{
			if (GamePanel.RANDOM.nextBoolean())
			{
				//place ball on north side
				ball.setY(-ball.getHeight());
				
				//velocity will be south
				ball.setDY((GamePanel.RANDOM.nextDouble() * BALL_VELOCITY) + BALL_VELOCITY);
			}
			else
			{
				//place ball on south side
				ball.setY(GamePanel.HEIGHT + ball.getHeight());
				
				//velocity will be north
				ball.setDY((GamePanel.RANDOM.nextDouble() * -BALL_VELOCITY) - BALL_VELOCITY);
			}
			
			//pick random location
			ball.setX(GamePanel.RANDOM.nextInt(GamePanel.WIDTH));
			
			//pick random x velocity
			if (GamePanel.RANDOM.nextBoolean())
			{
				ball.setDX((GamePanel.RANDOM.nextDouble() * -BALL_VELOCITY) - BALL_VELOCITY);
			}
			else
			{
				ball.setDX((GamePanel.RANDOM.nextDouble() * BALL_VELOCITY) + BALL_VELOCITY);
			}
		}
			
		//add ball to the list
		get().add(ball);
	}
	
	@Override
	public void render(Canvas canvas) throws Exception 
	{
		if (get() != null)
		{
			//render non-expanding balls first
			for (Ball ball : get())
			{
				if (!ball.hasExpand())
					ball.render(canvas);
			}
			
			//now render the expanding
			for (Ball ball : get())
			{
				if (ball.hasExpand())
					ball.render(canvas);
			}
		}
	}
}