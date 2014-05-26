package se.DV1456.mathable;

public class Player {
	private String name;
	private int[] theTiles;
	private int points;
	
	public Player(String name)
	{
		this.name = name;
		this.points = 0;
		this.theTiles = new int[7];
	}
	public String getName() 
	{
		return name;
	}
	public int[] getTheTiles() 
	{
		return theTiles;
	}
	public void setTheTiles(int[] theTiles) 
	{
		this.theTiles = theTiles;
	}
	public int getPoints() 
	{
		return points;
	}
	public void increasePoints(int points)
	{
		this.points += points;
	}
}
