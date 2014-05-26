package se.DV1456.mathable.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Tile {
	//Elements
	TILE_ZERO(Value.ZERO, Row.ROW1, 0),

	//Seven instances of 1-10, all the others do only have one instance.
	TILE_ONE1(Value.ONE, Row.ROW1, 1),
	TILE_ONE2(Value.ONE, Row.ROW1, 1),
	TILE_ONE3(Value.ONE, Row.ROW1, 1),
	TILE_ONE4(Value.ONE, Row.ROW1, 1),
	TILE_ONE5(Value.ONE, Row.ROW1, 1),
	TILE_ONE6(Value.ONE, Row.ROW1, 1),
	TILE_ONE7(Value.ONE, Row.ROW1, 1),

	TILE_TWO1(Value.TWO, Row.ROW1, 2),
	TILE_TWO2(Value.TWO, Row.ROW1, 2),
	TILE_TWO3(Value.TWO, Row.ROW1, 2),
	TILE_TWO4(Value.TWO, Row.ROW1, 2),
	TILE_TWO5(Value.TWO, Row.ROW1, 2),
	TILE_TWO6(Value.TWO, Row.ROW1, 2),
	TILE_TWO7(Value.TWO, Row.ROW1, 2),

	TILE_THREE1(Value.THREE, Row.ROW1, 3),
	TILE_THREE2(Value.THREE, Row.ROW1, 3),
	TILE_THREE3(Value.THREE, Row.ROW1, 3),
	TILE_THREE4(Value.THREE, Row.ROW1, 3),
	TILE_THREE5(Value.THREE, Row.ROW1, 3),
	TILE_THREE6(Value.THREE, Row.ROW1, 3),
	TILE_THREE7(Value.THREE, Row.ROW1, 3),

	TILE_FOUR1(Value.FOUR, Row.ROW1, 4),
	TILE_FOUR2(Value.FOUR, Row.ROW1, 4),
	TILE_FOUR3(Value.FOUR, Row.ROW1, 4),
	TILE_FOUR4(Value.FOUR, Row.ROW1, 4),
	TILE_FOUR5(Value.FOUR, Row.ROW1, 4),
	TILE_FOUR6(Value.FOUR, Row.ROW1, 4),
	TILE_FOUR7(Value.FOUR, Row.ROW1, 4),

	TILE_FIVE1(Value.FIVE, Row.ROW1, 5),
	TILE_FIVE2(Value.FIVE, Row.ROW1, 5),
	TILE_FIVE3(Value.FIVE, Row.ROW1, 5),
	TILE_FIVE4(Value.FIVE, Row.ROW1, 5),
	TILE_FIVE5(Value.FIVE, Row.ROW1, 5),
	TILE_FIVE6(Value.FIVE, Row.ROW1, 5),
	TILE_FIVE7(Value.FIVE, Row.ROW1, 5),

	TILE_SIX1(Value.SIX, Row.ROW1, 6),
	TILE_SIX2(Value.SIX, Row.ROW1, 6),
	TILE_SIX3(Value.SIX, Row.ROW1, 6),
	TILE_SIX4(Value.SIX, Row.ROW1, 6),
	TILE_SIX5(Value.SIX, Row.ROW1, 6),
	TILE_SIX6(Value.SIX, Row.ROW1, 6),
	TILE_SIX7(Value.SIX, Row.ROW1, 6),

	TILE_SEVEN1(Value.SEVEN, Row.ROW1, 7),
	TILE_SEVEN2(Value.SEVEN, Row.ROW1, 7),
	TILE_SEVEN3(Value.SEVEN, Row.ROW1, 7),
	TILE_SEVEN4(Value.SEVEN, Row.ROW1, 7),
	TILE_SEVEN5(Value.SEVEN, Row.ROW1, 7),
	TILE_SEVEN6(Value.SEVEN, Row.ROW1, 7),
	TILE_SEVEN7(Value.SEVEN, Row.ROW1, 7),

	TILE_EIGHT1(Value.EIGHT, Row.ROW1, 8),
	TILE_EIGHT2(Value.EIGHT, Row.ROW1, 8),
	TILE_EIGHT3(Value.EIGHT, Row.ROW1, 8),
	TILE_EIGHT4(Value.EIGHT, Row.ROW1, 8),
	TILE_EIGHT5(Value.EIGHT, Row.ROW1, 8),
	TILE_EIGHT6(Value.EIGHT, Row.ROW1, 8),
	TILE_EIGHT7(Value.EIGHT, Row.ROW1, 8),

	TILE_NINE1(Value.NINE, Row.ROW1, 9),
	TILE_NINE2(Value.NINE, Row.ROW1, 9),
	TILE_NINE3(Value.NINE, Row.ROW1, 9),
	TILE_NINE4(Value.NINE, Row.ROW1, 9),
	TILE_NINE5(Value.NINE, Row.ROW1, 9),
	TILE_NINE6(Value.NINE, Row.ROW1, 9),
	TILE_NINE7(Value.NINE, Row.ROW1, 9),

	TILE_TEN1(Value.TEN, Row.ROW1, 10),
	TILE_TEN2(Value.TEN, Row.ROW1, 10),
	TILE_TEN3(Value.TEN, Row.ROW1, 10),
	TILE_TEN4(Value.TEN, Row.ROW1, 10),
	TILE_TEN5(Value.TEN, Row.ROW1, 10),
	TILE_TEN6(Value.TEN, Row.ROW1, 10),
	TILE_TEN7(Value.TEN, Row.ROW1, 10),

	TILE_ELEVEN(Value.ELEVEN, Row.ROW1, 11),
	TILE_TWELVE(Value.TWELVE, Row.ROW1, 12),
	TILE_THIRTEEN(Value.THIRTEEN, Row.ROW1, 13),
	TILE_FOURTEEN(Value.FOURTEEN, Row.ROW1, 14),
	TILE_FIFTEEN(Value.FIFTEEN, Row.ROW2, 15),
	TILE_SIXTEEN(Value.SIXTEEN, Row.ROW2, 16),
	TILE_SEVENTEEN(Value.SEVENTEEN, Row.ROW2, 17),
	TILE_EIGHTEEN(Value.EIGHTEEN, Row.ROW2, 18),
	TILE_NINETEEN(Value.NINETEEN, Row.ROW2, 19),
	TILE_TWENTY(Value.TWENTY, Row.ROW2, 20),
	TILE_TWENTYONE(Value.TWENTYONE, Row.ROW2, 21),
	TILE_TWENTYFOUR(Value.TWENTYFOUR, Row.ROW2, 24),
	TILE_TWENTYFIVE(Value.TWENTYFIVE, Row.ROW2, 25),
	TILE_TWENTYSEVEN(Value.TWENTYSEVEN, Row.ROW2, 27),
	TILE_TWENTYEIGHT(Value.TWENTYEIGHT, Row.ROW2, 28),
	TILE_THIRTY(Value.THIRTY, Row.ROW2, 30),
	TILE_THIRTYTWO(Value.THIRTYTWO, Row.ROW2, 32),
	TILE_THIRTYFIVE(Value.THIRTYFIVE, Row.ROW2, 35),
	TILE_THIRTYSIX(Value.THIRTYSIX, Row.ROW2, 36),
	TILE_FOURTY(Value.FOURTY, Row.ROW3, 40),
	TILE_FOURTYTWO(Value.FOURTYTWO, Row.ROW3, 42),
	TILE_FOURTYFIVE(Value.FOURTYFIVE, Row.ROW3, 45),
	TILE_FOURTYEIGHT(Value.FOURTYEIGHT, Row.ROW3, 48),
	TILE_FOURTYNINE(Value.FOURTYNINE, Row.ROW3, 49),
	TILE_FIFTY(Value.FIFTY, Row.ROW3, 50),
	TILE_FIFTYFOUR(Value.FIFTYFOUR, Row.ROW3, 54),
	TILE_FIFTYSIX(Value.FIFTYSIX, Row.ROW3, 56),
	TILE_SIXTY(Value.SIXTY, Row.ROW3, 60),
	TILE_SIXTYTHREE(Value.SIXTYTHREE, Row.ROW3, 63),
	TILE_SIXTYFOUR(Value.SIXTYFOUR, Row.ROW3, 64),
	TILE_SEVENTY(Value.SEVENTY, Row.ROW3, 70),
	TILE_SEVENTYTWO(Value.SEVENTYTWO, Row.ROW3, 72),
	TILE_EIGHTY(Value.EIGHTY, Row.ROW3, 80),
	TILE_EIGHTYONE(Value.EIGHTYONE, Row.ROW3, 81),
	TILE_NINETY(Value.NINETY, Row.ROW4, 90);

	//The size of the tile is 100x100
	private final int TILE_SIZE = 100;

	//Creates a list with all the tiles in a random order
	private static List<Tile> tList;

	//Variables
	private Value value;
	private Row row;
	private int number;
	private boolean picked;

	//constructor
	Tile(final Value value, final Row row, final int number) 
	{
		this.value = value;
		this.row = row;
		this.number = number;
		this.picked = false;
	}

	//Functions
	public int getmValue() 
	{
		return this.value.ordinal();
	}

	public boolean getPicked()
	{
		return this.picked;
	}

	public int getNumber() {
		return number;
	}
	
	public void setPicked(boolean picked)
	{
		this.picked = picked;
	}

	public int getTILE_SIZE() 
	{
		return TILE_SIZE;
	}

	public int getTexturePositionX() 
	{
		return this.value.ordinal() * this.TILE_SIZE;
	}
	
	public int getTexturePositionY() 
	{
		return this.row.ordinal() * this.TILE_SIZE;
	}

	private static void createList() {
		//Initiallize the list
		tList = new ArrayList<Tile>(Arrays.asList(Tile.values()));
		//shuffle around the elements
		Collections.shuffle(tList);
	}

	public static Tile getRandomTile() throws Exception{
		//If the list is not yet created, lets create it.
		if(tList == null)
		{
			createList();
		}
		//If all tiles are already picked
		if(tList.size() == 0)
		{
			throw new Exception("No tiles left!");
		}
		//returns the first tile in the shuffled ordered list.
		return tList.remove(0);
	}
}
