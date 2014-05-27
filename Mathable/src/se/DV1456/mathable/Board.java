package se.DV1456.mathable;

public class Board 
{
	boolean usedTiles[][];
	int tileValue[][];
	
	public Board() 
	{
		super();
		this.usedTiles = new boolean[14][14];
		this.usedTiles[7][7] = true;
		this.usedTiles[8][7] = true;
		this.usedTiles[7][8] = true;
		this.usedTiles[8][8] = true;
		this.tileValue = new int[14][14];
		this.tileValue[7][7] = 1;
		this.tileValue[8][7] = 2;
		this.tileValue[7][8] = 3;
		this.tileValue[8][8] = 4;
	}
	
	public boolean getUsedTiles(int x, int y) 
	{
		return usedTiles[x][y];
	}
	public void setUsedTiles(int x, int y, boolean state) 
	{
		this.usedTiles[x][y] = state;
	}
	public int getTileValue(int x, int y) 
	{
		return tileValue[x][y];
	}
	public void setTileValue(int x, int y, int value) 
	{
		this.tileValue[x][y] = value;
	}
}
