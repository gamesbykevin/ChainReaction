package com.gamesbykevin.chainreaction.player;

import com.gamesbykevin.chainreaction.balls.Ball;
import com.gamesbykevin.chainreaction.balls.Balls;
import com.gamesbykevin.chainreaction.common.ICommon;

import android.graphics.Canvas;

public class Player implements ICommon 
{
	//number of lives
	private int lives;

	//the player's ball
	private Ball ball;
	
	/**
	 * Default constructor
	 */
	public Player() 
	{
		//the player will always have the white ball
		this.ball = new Ball(4, 0);
	
		//reset the ball
		reset();
	}
	
	/**
	 * Reset the player's ball.<br>
	 * 1. Hide off the screen
	 * 2. Reset the starting dimension
	 * 3. Allow for expansion again
	 * 4. Stop the ball's velocity
	 */
	public final void reset()
	{
		//set the size of the ball
		getBall().setDimension(Balls.START_DIMENSION);
		
		//place the ball off the screen for now
		getBall().setX(-getBall().getWidth());
		getBall().setY(-getBall().getHeight());
		
		//this ball will not move
		getBall().setDX(0);
		getBall().setDY(0);
		
		//flag false, so we can play it again
		getBall().setExpand(false);
		getBall().setPause(false);
		getBall().setDead(false);
	}
	
	/**
	 * 
	 * @param lives
	 */
	public void setLives(final int lives)
	{
		this.lives = lives;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLives()
	{
		return this.lives;
	}

	/**
	 * Get the players ball
	 * @return The ball the player controls
	 */
	public Ball getBall()
	{
		return this.ball;
	}
	
	@Override
	public void dispose() 
	{
		if (ball != null)
		{
			ball.dispose();
			ball = null;
		}
	}

	@Override
	public void update() throws Exception 
	{
		if (getBall() != null)
		{
			//update the ball
			getBall().update();
			
			//reset the ball
			if (getBall().isDead())
			{
				//reset ball
				reset();
				
				//deduct a life
				setLives(getLives() - 1);
			}
		}
	}

	@Override
	public void render(Canvas canvas) throws Exception 
	{
		//render the ball
		getBall().render(canvas);
	}
}