package se.DV1456.mathable;

import org.andengine.entity.primitive.Rectangle;


public class Board extends Rectangle
{
	
	private boolean drawBorders;
	private int cols = 14;
	private int rows = 14;

	public Board(float pX, float pY, float pWidth, float pHeight,
			IRectangleVertexBufferObject pRectangleVertexBufferObject) 
	{
		super(pX, pY, pWidth, pHeight, pRectangleVertexBufferObject);
		draw();
	}
	
	private void draw()
	{
		if(this.drawBorders)
		{
			
		}
	}
}
