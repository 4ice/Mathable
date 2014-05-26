package se.DV1456.mathable;

import se.DV1456.mathable.tile.Tile;

public class Player {
	private String name;
	private Tile[] theTiles;
	private int nrOfTiles;
	private int points;
	
	public Player(String name)
	{
		this.name = name;
		this.points = 0;
		this.theTiles = new Tile[7];
		this.nrOfTiles = 0;
	}
	public String getName() 
	{
		return name;
	}
	public Tile[] getTheTiles() 
	{
		return theTiles;
	}
	public void setTheTiles(Tile[] theTiles) 
	{
		this.theTiles = theTiles;
	}
	public int getPoints() 
	{
		return points;
	}
	
	public void addTile(Tile tile) throws Exception
	{
		if(this.nrOfTiles == 7)
		{
			throw new Exception("The user already has seven tiles.");
		}
		this.theTiles[this.nrOfTiles++] = tile;
	}
	public void playTile()
	{
		this.nrOfTiles--;
	}
	
	public void increasePoints(int points)
	{
		this.points += points;
	}
}
