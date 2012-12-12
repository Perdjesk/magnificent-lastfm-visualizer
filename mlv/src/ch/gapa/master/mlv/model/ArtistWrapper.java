package ch.gapa.master.mlv.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;



public class ArtistWrapper {
	static final int RADIUS = 20;
	static final int SELECTED_COLOR = Color.GREEN;
	static final int DEFAULT_COLOR = Color.BLUE;
	static final String DEFAULT_TEXT ="Artist Wrapper";
	
	Point position = new Point();
	Paint paint = new Paint();
	
	boolean isSelected=true;
	
	public ArtistWrapper(){
		paint.setAntiAlias(false);
		paint.setColor(DEFAULT_COLOR);
		paint.setTextAlign(Paint.Align.CENTER);
	}
	public ArtistWrapper(Point position){
		this();
		this.position=position;
	}
	
	public void setPosition(Point position) {
		this.position=position;
	}
	
	public void draw(Canvas canvas) {
		canvas.drawCircle(position.x, position.y, RADIUS , paint);
		canvas.drawText(DEFAULT_TEXT, position.x, position.y+RADIUS+paint.getTextSize(), paint);
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected=isSelected;
		if (isSelected) paint.setColor(SELECTED_COLOR);
		else paint.setColor(DEFAULT_COLOR);
	}
	
	public boolean contains(Point location){
		return (location.x > (position.x-RADIUS) && location.x < (position.x+RADIUS)
				&& location.y > (position.y-RADIUS) && location.y < (position.y+RADIUS));
	}
}
