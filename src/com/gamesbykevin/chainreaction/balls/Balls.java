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
	public static final int START_DIMENSION = 16;
	
	/**
	 * The speed of the ball movement
	 */
	private static final double BALL_VELOCITY = 4;
	
	//reference to the player's ball
	private final Ball player;
	
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
	 * Reset the balls in play<br>
	 * @param count The number of balls we want to add
	 */
	public void reset(final int count)
	{
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
			
			//set ball location
			ball.setX(GamePanel.RANDOM.nextInt(GamePanel.WIDTH));
			ball.setY(GamePanel.RANDOM.nextInt(GamePanel.HEIGHT));
			
			//set ball size
			ball.setDimension(START_DIMENSION);
			
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
				
				//also check for ball collision with other balls
				for (int x = 0; x < get().size(); x++)
				{
					//get the current ball
					Ball tmp = get().get(x);
					
					//skip because we don't want to check the same ball
					if (ball.hasId(tmp.getId()))
						continue;
					
					//if the balls don't intersect, we won't bother checking
					if (!ball.hasCollision(tmp))
						continue;
					
					//make sure the balls aren't dead
					if (!tmp.isDead() && !ball.isDead())
					{
						//determine which ball is expanding
						if (tmp.hasPause() || tmp.hasExpand())
						{
							//flag the other ball expand true
							ball.setExpand(true);
						}
						else if (ball.hasPause() || ball.hasExpand())
						{
							//flag the other ball expand true
							tmp.setExpand(true);
						}
					}
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
					//if there is collision and the ball is not dead
					if (!player.isDead() && ball.hasCollision(player))
					{
						//if the ball is not dead let's also check the player's ball
						if (player.hasPause() || player.hasExpand())
						{
							ball.setExpand(true);
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
			//render each ball
			for (Ball ball : get())
			{
				ball.render(canvas);
			}
		}
	}
}