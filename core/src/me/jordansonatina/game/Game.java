package me.jordansonatina.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sun.java.swing.plaf.windows.WindowsDesktopPaneUI;

public class Game extends ApplicationAdapter {

	public final int WORLD_WIDTH = 720;
	public final int WORLD_HEIGHT = 1280;
	public final int GRID_SIZE = 110;
	public final int GRID_MARGIN = 10;

	public static char[][] grid;

	public static String[] possibleAnswers;
	public static String answer;

	public FileHandle answersFile;

	public static int guesses;
	public static int letter; // 1 - 5

	public ShapeRenderer renderer;
	public SpriteBatch batch;
	public BitmapFont font;

	public Texture wordleLogo;

	
	@Override
	public void create () {
		grid = new char[6][5];

		answersFile = Gdx.files.internal("answers.txt");

		// PLACE ALL ANSWERS FROM TXT INTO ARRAY
		String text = answersFile.readString();
		possibleAnswers = text.split("\\r?\\n");

		answer = possibleAnswers[(int)(Math.random()*possibleAnswers.length)];
		answer = answer.toUpperCase();
		System.out.println(answer);

		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				grid[row][col] = '.';
			}
		}


		guesses = 0;

		renderer = new ShapeRenderer();
		batch = new SpriteBatch();

		wordleLogo = new Texture(Gdx.files.internal("wordle.png"));

		font = new BitmapFont();

		InputMultiplexer multiplexer = new InputMultiplexer();
		MyInputProcessor inputProcessor = new MyInputProcessor();
		multiplexer.addProcessor(inputProcessor);
		Gdx.input.setInputProcessor(multiplexer);

	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		Gdx.gl.glLineWidth(3);

		// DEAL WITH HIGHLIGHTING THE SQUARES TO EITHER GREY, GREEN, OR GOLD
		renderer.begin(ShapeRenderer.ShapeType.Filled);
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				int rowLength = (GRID_SIZE*grid[row].length)+(GRID_MARGIN*grid[row].length);
				int colLength = (GRID_SIZE*grid.length)+(GRID_MARGIN*grid.length);
				int distanceFromLeftToCentralize = (WORLD_WIDTH - rowLength) / 2;
				int distanceFromBottomToCentralize = (WORLD_HEIGHT - colLength) / 2;

				// ONLY ADJUST THE SQUARES THAT HAVE BEEN GUESSED
				if ((5-row) < guesses)
				{
					if (charInAnswer(grid[5-row][col]))
					{
						// IT IS IN THE ANSWER BUT IS IT CORRECTLY PLACED? IF SO MAKE GREEN
						if (charInCorrectIndex(grid[5-row][col], 5-row))
						{
							renderer.setColor(Color.LIME);
						} else {
							renderer.setColor(Color.GOLD);
						}
					} else {
						renderer.setColor(Color.GRAY);
					}

					renderer.rect(distanceFromLeftToCentralize + col * (GRID_SIZE+GRID_MARGIN), distanceFromBottomToCentralize + row * (GRID_SIZE+GRID_MARGIN), GRID_SIZE, GRID_SIZE);
				}

			}
		}
		renderer.end();

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

				// RENDER OUT LETTERS FOR EACH BOX
				batch.begin();
				font.setColor(Color.BLACK);
				font.draw(batch, "" + grid[row][col], distanceFromLeftToCentralize + col * (GRID_SIZE+GRID_MARGIN), distanceFromBottomToCentralize + (6-row) * (GRID_SIZE+GRID_MARGIN));
				batch.end();

			}
		}
		renderer.end();

		batch.begin();
		int distanceFromLeftToCentralize = (WORLD_WIDTH-576)/2;
		batch.draw(wordleLogo, distanceFromLeftToCentralize, WORLD_HEIGHT-(152 + GRID_MARGIN), 576, 152);
		batch.end();
	}

	public boolean charInAnswer(char c)
	{
        return answer.contains("" + c);
	}
	public boolean charInCorrectIndex(char c, int row)
	{
		String guess = "";
		for (int i = 0; i < 5; i++)
		{
			guess += Game.grid[row][i];
		}
		int indexFromGuess = guess.indexOf("" + c);
		int indexFromAnswer = answer.indexOf("" + c);

		return indexFromGuess == indexFromAnswer;
	}

	public static void displayGrid()
	{
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				System.out.print(grid[row][col] + "  ");
			}
			System.out.println();
		}
	}
	@Override
	public void dispose () {
		renderer.dispose();
		batch.dispose();
	}

	private static class MyInputProcessor extends InputAdapter {
		@Override
		public boolean keyDown(int keycode) {
			// DELETE LETTERS
			if (keycode == Input.Keys.DEL && letter >= 1)
			{
				Game.grid[guesses][letter-1] = '.';
				letter--;
				System.out.println(letter);
			}
			// PUT IN LETTERS
			if (keycode != Input.Keys.ENTER && keycode != Input.Keys.DEL && letter < 5)
			{
				letter++;
				Game.grid[guesses][letter-1] = Input.Keys.toString(keycode).charAt(0);

				System.out.println(letter);
			}

			if (keycode == Input.Keys.ENTER && letter == 5)
			{
				guesses++;
				letter = 0;
			}

			displayGrid();

			return true; // return true to indicate that the input was handled
		}
	}
}
