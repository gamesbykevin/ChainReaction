package com.gamesbykevin.chainreaction.game;

import com.gamesbykevin.androidframework.resources.Disposable;

import android.graphics.Canvas;

/**
 * Game interface methods
 * @author GOD
 */
public interface IGame extends Disposable
{
    /**
     * Logic to restart the game with the same settings
     * @throws Exception
     */
    public void reset() throws Exception;
    
    /**
     * Logic to update element
     */
    public void update() throws Exception;
    
    /**
     * Logic to render the game
     * @param canvas Canvas where we draw
     * @throws Exception
     */
    public void render(final Canvas canvas) throws Exception;
    
    /**
     * Update the game based on a motion event
     * @param action The action of the MotionEvent
     * @param x (x-coordinate)
     * @param y (y-coordinate)
     * @throws Exception
     */
    public void update(final int action, final float x, final float y) throws Exception;
}
