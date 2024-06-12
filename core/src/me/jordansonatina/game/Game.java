package me.jordansonatina.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class Game extends ApplicationAdapter {

	public final int WORLD_WIDTH = 720;
	public final int WORLD_HEIGHT = 1280;
	public final int GRID_SIZE = 110;
	public final int GRID_MARGIN = 10;

	public char[][] grid;

	public int guesses;

	public ShapeRenderer renderer;
	public SpriteBatch batch;

	public Texture wordleLogo;

	
	@Override
	public void create () {
		grid = new char[6][5];
		guesses = 0;

		renderer = new ShapeRenderer();
		batch = new SpriteBatch();

		wordleLogo = new Texture(Gdx.files.internal("wordle.png"));

	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		Gdx.gl.glLineWidth(3);
		// RENDER THE GRID
		renderer.begin(ShapeRenderer.ShapeType.Line);
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				int rowLength = (GRID_SIZE*grid[row].length)+(GRID_MARGIN*grid[row].length);
				int colLength = (GRID_SIZE*grid.length)+(GRID_MARGIN*grid.length);
				int distanceFromLeftToCentralize = (WORLD_WIDTH - rowLength) / 2;
				int distanceFromBottomToCentralize = (WORLD_HEIGHT - colLength) / 2;
				renderer.setColor(Color.LIGHT_GRAY);
				renderer.rect(distanceFromLeftToCentralize + col * (GRID_SIZE+GRID_MARGIN), distanceFromBottomToCentralize + row * (GRID_SIZE+GRID_MARGIN), GRID_SIZE, GRID_SIZE);
			}
		}
		renderer.end();

		batch.begin();
		int distanceFromLeftToCentralize = (WORLD_WIDTH-576)/2;
		batch.draw(wordleLogo, distanceFromLeftToCentralize, WORLD_HEIGHT-(152 + GRID_MARGIN), 576, 152);
		batch.end();
	}
	
	@Override
	public void dispose () {
		renderer.dispose();
		batch.dispose();
	}
}
