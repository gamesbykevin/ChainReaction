package com.gamesbykevin.chainreaction.balls;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.chainreaction.assets.Assets;
import com.gamesbykevin.chainreaction.common.ICommon;
import com.gamesbykevin.chainreaction.panel.GamePanel;

import android.graphics.Canvas;

public class Ball extends Entity implements ICommon
{
	/**
	 * The size of the animation for each ball
	 */
	public static final int SPRITE_DIMENSION = 128;
	
	/**
	 * The size of the animation for the explosion
	 */
	//public static final int EXPLOSION_DIMENSION = 134;
	public static final int EXPLOSION_DIMENSION = 140;
	
	/**
	 * Default key for the single animation
	 */
	public static final String DEFAULT_KEY = "Default";
	
	/**
	 * Unique key for the explosion animation
	 */
	public static final String EXPLOSION_KEY = "Explosion";
	
	/**
	 * Different types of balls
	 */
	protected enum Type
	{
		Blue1(0,0), Blue2(0,3), Blue3(2,3), Blue4(1,4),
		Yellow1(1,0), Yellow2(3,0), Yellow3(2,4),
		//White1(4,0), White2(1,3),
		Green1(3,1), Green2(4,1), Green3(0,2), Green4(0,4),
		Purple1(1,1), Purple2(4,3), Purple3(3,4),
		Orange1(1,2), Orange2(4,2),
		Red1(2,2), Red2(3,2),
		Other1(3,3);
		
		private final int col, row;
		
		private Type(final int col, final int row)
		{
			this.col = col;
			this.row = row;
		}
	}
	
	//do we expand the ball
	private boolean expand = false;
	
	//after expanding is complete we will pause
	private boolean pause = false;
	
	//is the ball dead
	private boolean dead = false;
	
	//keep track of how long we stay paused
	private long time;
	
	//how long do we stay paused
	public static final long PAUSED_DURATION = 1100L;
	
	/**
	 * The duration of each frame in our explosion (milliseconds)
	 */
	public static final long EXPLOSION_DURATION = 75L;
	
	/**
	 * The speed at which the ball can expand
	 */
	private static final double EXPAND_RATE = 5;
	
	/**
	 * The size at which we stop expanding the ball
	 */
	private static final double EXPAND_LIMIT = 96;
	
	/**
	 * Create ball
	 * @param type The type (a.k.a. color)
	 */
	public Ball(final Type type) 
	{
		this(type.col, type.row);
	}
	
	/**
	 * Create a ball
	 * @param col Column location on sprite sheet
	 * @param row Row location on sprite sheet
	 */
	public Ball(final int col, final int row)
	{
		Animation animation = new Animation(
			Images.getImage(Assets.ImageGameKey.Balls), 
			col * SPRITE_DIMENSION, 
			row * SPRITE_DIMENSION, 
			SPRITE_DIMENSION, 
			SPRITE_DIMENSION
		);
			
		//the animation will not loop
		animation.setLoop(false);
		
		//add single animation to sprite sheet
		super.getSpritesheet().add(DEFAULT_KEY, animation);
		
		//set a default size
		this.setDimension(SPRITE_DIMENSION);
	}
	
	/**
	 * Add and set the explosion animation.<br>
	 */
	public void addExplosion()
	{
		//only add explosion animation if it already doesn't exist
		if (super.getSpritesheet().get(EXPLOSION_KEY) == null)
		{
			Animation animation = new Animation(Images.getImage(Assets.ImageGameKey.Explosion), 0, 0, EXPLOSION_DIMENSION, EXPLOSION_DIMENSION, 9, 1, 9);
			
			//the animation will not loop
			animation.setLoop(false);
			
			//set the frame delay
			animation.setDelay(EXPLOSION_DURATION);
			
			//add animation to sprite sheet
			super.getSpritesheet().add(EXPLOSION_KEY, animation);
		}
		
		//increase the size of the explosion
		this.setDimension(getWidth() * 3);
		
		//set it as current animation
		super.getSpritesheet().setKey(EXPLOSION_KEY);
		
		//make sure animation starts from the beginning
		super.getSpritesheet().get().reset();
	}
	
	/**
	 * Flag the ball expanding
	 * @param expand true = yes, false = no
	 */
	public void setExpand(final boolean expand)
	{
		this.expand = expand;
	}
	
	/**
	 * Is the ball expanding?
	 * @return true = yes, false = no
	 */
	public boolean hasExpand()
	{
		return this.expand;
	}
	
	/**
	 * Flag the ball paused
	 * @param pause true if the ball has finished expanding, false otherwise
	 */
	public void setPause(final boolean pause)
	{
		this.pause = pause;
	}
	
	/**
	 * Is the ball paused?
	 * @return true if the ball has finished expanding, false otherwise
	 */
	public boolean hasPause()
	{
		return this.pause;
	}
	
	/**
	 * Flag the ball dead.
	 * @param dead true = yes, false = no
	 */
	public void setDead(final boolean dead)
	{
		this.dead = dead;
	}
	
	/**
	 * Is the ball dead?
	 * @return true = yes, false = no
	 */
	public boolean isDead()
	{
		return this.dead;
	}
	
	/**
	 * Does the ball have collision?
	 * @param entity The entity we want to check
	 * @return true if the ball collides with the entity, false otherwise
	 */
	public boolean hasCollision(final Entity entity)
	{
		//get the distance between entities
		final double distance = getDistance(entity);
		
		//get the radius of both entities
		final double radius1 = (getWidth() / 2);
		final double radius2 = (entity.getWidth() / 2);
		
		//if the distance is less that the total radius
		return (distance < (radius1 + radius2));
	}

	@Override
	public void dispose() 
	{
		super.dispose();
	}
	
	@Override
	public void update() throws Exception 
	{
		//update the current assigned animation
		super.getSpritesheet().update();
		
		//if we are expanding
		if (hasExpand())
		{
			if (hasPause())
			{
				//if the ball has been paused long enough
				if (System.currentTimeMillis() - time >= PAUSED_DURATION)
				{
					//shrink the ball
					setDimension(getWidth() - (EXPAND_RATE * 2));
					
					//if the ball is too small, flag dead
					if (getWidth() < 2 || getHeight() < 2)
						setDead(true);
				}
			}
			else
			{
				//expand the ball
				setDimension(getWidth() + EXPAND_RATE);
				
				//make sure we don't get to big
				if (getWidth() > EXPAND_LIMIT || getHeight() > EXPAND_LIMIT)
				{
					//limit size
					setDimension(EXPAND_LIMIT);
					
					//flag true
					setPause(true);
					
					//store the time
					this.time = System.currentTimeMillis();
				}
			}
		}
		else
		{
			//update location
			setX(getX() + getDX());
			setY(getY() + getDY());
			
			//make sure we stay within the width of the screen
			if (getX() < (getWidth() / 2) && getDX() < 0 || getX() > GamePanel.WIDTH - (getWidth() / 2) && getDX() > 0)
				setDX(-getDX());
			
			//make sure we stay within the height of the screen
			if (getY() < (getHeight() / 2) && getDY() < 0 || getY() > GamePanel.HEIGHT - (getHeight() / 2) && getDY() > 0)
				setDY(-getDY());
		}
	}
	
	@Override
	public void render(final Canvas canvas) throws Exception
	{
		//skip if dead and the animation has finished
		if (isDead() && super.getSpritesheet().get().hasFinished() || getWidth() < 1 || getHeight() < 1)
			return;
		
		//store original location
		final double x = getX();
		final double y = getY();
		
		//offset location
		setX(x- (getWidth() / 2));
		setY(y - (getHeight() / 2));
		
		//render the ball
		super.render(canvas);
		
		//restore original location
		setX(x);
		setY(y);
	}
	
	/**
	 * Assign the dimension.<br>
	 * The ball will have the same width/height
	 * @param d Pixel size of the ball
	 */
	public void setDimension(final double d)
	{
		setHeight(d);
		setWidth(d);
	}
	
	@Override
	public void setHeight(final double h)
	{
		super.setHeight(h);
		super.setWidth(h);
	}
	
	@Override
	public void setWidth(final double w)
	{
		super.setWidth(w);
		super.setHeight(w);
	}
}