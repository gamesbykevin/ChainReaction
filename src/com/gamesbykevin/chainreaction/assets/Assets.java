package com.gamesbykevin.chainreaction.assets;

import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Files;
import com.gamesbykevin.androidframework.resources.Font;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.chainreaction.panel.GamePanel;

import android.app.Activity;

/**
 * This class will contain all of our assets
 * @author GOD
 */
public class Assets 
{
    /**
     * The directory where audio sound effect resources are kept
     */
    private static final String DIRECTORY_MENU_AUDIO = "audio/menu";
    
    /**
     * The directory where audio sound effect resources are kept
     */
    private static final String DIRECTORY_GAME_AUDIO = "audio/game";
    
    /**
     * The directory where image resources are kept for the menu
     */
    private static final String DIRECTORY_MENU_IMAGE = "image/menu";
    
    /**
     * The directory where image resources are kept for the game
     */
    private static final String DIRECTORY_GAME_IMAGE = "image/game";
    
    /**
     * The directory where font resources are kept
     */
    private static final String DIRECTORY_MENU_FONT = "font/menu";
    
    /**
     * The directory where font resources are kept
     */
    private static final String DIRECTORY_GAME_FONT = "font/game";
    
    /**
     * The directory where our text files are kept
     */
    private static final String DIRECTORY_TEXT = "text";
    
    /**
     * The different fonts used in our game.<br>
     * Order these according to the file name in the "font" assets folder.
     */
    public enum FontMenuKey
    {
        //Default
    }
    
    /**
     * The different fonts used in our game.<br>
     * Order these according to the file name in the "font" assets folder.
     */
    public enum FontGameKey
    {
    	Default
    }
    
    /**
     * The different images for our menu items
     */
    public enum ImageMenuKey
    {
    	Background, 
    	Button, 
    	Cancel, 
    	Confirm,
    	Facebook, 
    	Instructions, 
    	Twitter, 
    	GameOver,
    	Logo, 
    	NewRecord, 
    	Splash, 
    	Winner
    }
    
    /**
     * The different images in our game.<br>
     * Order these according to the file name in the "image" assets folder.
     */
    public enum ImageGameKey
    {
    	Balls, Explosion, Hint1, Hint2
    }
    
    /**
     * The key of each text file.<br>
     * Order these according to the file name in the "text" assets folder.
     */
    public enum TextKey
    {
        
    }
    
    /**
     * The key of each sound in our game.<br>
     * Order these according to the file name in the "audio" assets folder.
     */
    public enum AudioMenuKey
    {
    	Selection, Theme 
    }
    
    /**
     * The key of each sound in our game.<br>
     * Order these according to the file name in the "audio" assets folder.
     */
    public enum AudioGameKey
    {
    	Explosion, Lose, Music, Tone1, Tone2, Tone3, Tone4, Tone5, Win
    }
    
    /**
     * Load all assets.<br>
     * If an asset already exists, it won't be loaded again
     * @param activity Object containing AssetManager needed to load assets
     * @throws Exception 
     */
    public static final void load(final Activity activity) throws Exception
    {
        //load all images for the menu
        Images.load(activity, ImageMenuKey.values(), DIRECTORY_MENU_IMAGE, true);
        
        //load all fonts for the menu
        Font.load(activity, FontMenuKey.values(), DIRECTORY_MENU_FONT, true);
        
        //load all audio for the menu
        Audio.load(activity, AudioMenuKey.values(), DIRECTORY_MENU_AUDIO, true);
        
        //load images for the game
        Images.load(activity, ImageGameKey.values(), DIRECTORY_GAME_IMAGE, true);
        
        //load all audio for the game
        Audio.load(activity, AudioGameKey.values(), DIRECTORY_GAME_AUDIO, true);
        
        //load all fonts for the game
        Font.load(activity, FontGameKey.values(), DIRECTORY_GAME_FONT, true);
        
        //load all text files
        Files.load(activity, TextKey.values(), DIRECTORY_TEXT, true);
    }
    
    public static void playCollisionSound()
    {
    	switch (GamePanel.RANDOM.nextInt(5))
    	{
	    	case 0:
	    		Audio.play(Assets.AudioGameKey.Tone1);
	    		break;
	    		
	    	case 1:
	    		Audio.play(Assets.AudioGameKey.Tone2);
	    		break;
	    		
	    	case 2:
	    		Audio.play(Assets.AudioGameKey.Tone3);
	    		break;
	    		
	    	case 3:
	    		Audio.play(Assets.AudioGameKey.Tone4);
	    		break;
	    		
	    	case 4:
	    		Audio.play(Assets.AudioGameKey.Tone5);
	    		break;
    	}
    }
    
    /**
     * Recycle all assets
     */
    public static void recycle()
    {
        try
        {
            Images.dispose();
            Font.dispose();
            Audio.dispose();
            Files.dispose();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}