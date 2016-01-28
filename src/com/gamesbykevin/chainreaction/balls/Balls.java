package com.gamesbykevin.chainreaction.balls;

import java.util.ArrayList;
import java.util.List;

import com.gamesbykevin.chainreaction.common.ICommon;
import com.gamesbykevin.chainreaction.panel.GamePanel;

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
	
	//reference to the player's ball
	private final Ball player;
	
	//the number we have to achieve to win
	private int goal;
	
	/**
	 * Default constructor
	 */
	public Balls(final Ball player) 
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
	 */
	public void reset(final int count, final int goal)
	{
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
			//update each ball
			for (int i = 0; i < get().size(); i++)
			{
				//get the current ball
				Ball ball = get().get(i);
				
				//update ball
				ball.update();
				
				/**
				 * If this ball collides with any others that aren't dead and expanding.<br>
				 * We will also expand this ball
				 */
				if (hasCollision(ball))
				{
					//if this ball has not expanded yet, take away one from our goal
					if (!ball.hasExpand())
						setGoal(getGoal() - 1);
					
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
					if (!player.isDead())
					{
						//check if the ball collides with the player's ball
						if (ball.hasCollision(player))
						{
							//if the player's ball is expanding, we will expand this ball
							if (player.hasExpand())
							{
								//if this ball has not expanded yet, take away one from our goal
								if (!ball.hasExpand())
									setGoal(getGoal() - 1);
								
								ball.setExpand(true);
							}
						}
					}
				}
			}
		}
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