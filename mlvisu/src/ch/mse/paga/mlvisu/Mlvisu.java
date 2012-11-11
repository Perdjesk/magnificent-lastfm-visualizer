package ch.mse.paga.mlvisu;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class Mlvisu implements ApplicationListener, GestureListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite sprite;
	ShapeRenderer shapeRender;

	float rotationSpeed = 0.5f;
	float w,h;

	@Override
	public void create() {
		 w = Gdx.graphics.getWidth();
		 h = Gdx.graphics.getHeight();

		Gdx.input.setInputProcessor(new GestureDetector(this));
		shapeRender = new ShapeRenderer();

		camera = new OrthographicCamera(10, 10);
		batch = new SpriteBatch();

		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);

		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
		sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {
		handleInput();
		
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		batch.end();
		shapeRender.setProjectionMatrix(camera.combined);
		shapeRender.begin(ShapeType.Rectangle);
		shapeRender.setColor(new Color(1, 0, 0, 1));
		shapeRender.rect(0, 0, 1, 1);
		shapeRender.end();
	}

	private void handleInput() {
		
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			Gdx.app.debug("blu", "pressA");
			camera.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			camera.zoom -= 0.02;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			camera.translate(-0.1f, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			camera.translate(0.1f, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			camera.translate(0, -0.1f, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			camera.translate(0, 0.1f, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.rotate(-rotationSpeed, 0, 0, 1);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			camera.rotate(rotationSpeed, 0, 0, 1);
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		Gdx.app.log("Mlvisu",deltaX+"/"+deltaY);
		//Gdx.input.vibrate(1);
		//camera.position.add(-deltaX * camera.zoom, deltaY * camera.zoom, 0);
		//camera.lookAt(-x, -y,0);
		camera.translate((deltaX/w), -(deltaY/y), 0);
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (initialDistance > distance)
			camera.zoom += 0.02;
		else
			camera.zoom -= 0.02;
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
}
