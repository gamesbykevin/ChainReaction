package com.gamesbykevin.chainreaction.player;

import com.gamesbykevin.chainreaction.balls.Ball;
import com.gamesbykevin.chainreaction.balls.Balls;
import com.gamesbykevin.chainreaction.common.ICommon;

import android.graphics.Canvas;

public class Player implements ICommon 
{
	//the player's ball
	private Ball ball;
	
	//do we have a turn
	private boolean turn = true;
	
	//the player's total score
	private int score;
	
	//have we moved the ball
	private boolean move = false;
	
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
	 * Flag the player move
	 * @param move Have we moved the player's ball, true = yes, false = no
	 */
	public void setMove(final boolean move)
	{
		this.move = move;
	}
	
	/**
	 * Have we moved the player's ball?
	 * @return true = yes, false = no
	 */
	public boolean hasMove()
	{
		return this.move;
	}
	
	/**
	 * 
	 * @param score
	 */
	public void setScore(final int score)
	{
		this.score = score;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getScore()
	{
		return this.score;
	}
	
	/**
	 * Flag the player a turn.
	 * @param turn true = yes, false = no
	 */
	public void setTurn(final boolean turn)
	{
		this.turn = turn;
	}
	
	/**
	 * Does the player have a turn?
	 * @return true = yes, false = no
	 */
	public boolean hasTurn()
	{
		return this.turn;
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
		//make sure the players ball has the explosion animation
		getBall().addExplosion();
		
		//set the size of the ball
		getBall().setDimension(Balls.START_DIMENSION);
		
		//reset animation
		getBall().getSpritesheet().setKey(Ball.DEFAULT_KEY);
		
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
		
		//give the player a turn
		setTurn(true);
		
		//flag move false
		setMove(false);
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
		}
	}

	@Override
	public void render(Canvas canvas) throws Exception 
	{
		//render the ball
		getBall().render(canvas);
	}
}