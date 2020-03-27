package assets;

public class Card implements Comparable<Card>
{
	private String name;		//Ace, King etc
	private String suit;		//Heart, Spades etc
	private int value;			//Ace = 14 etc
	private boolean isPic;		//Type. number or picture 
	
	public Card()
	{
		name = new String();
		suit = new String();
		value = -1;
	}
	
	public Card(String suitParam, int valParam) 
	{
		name = new String();
		suit = new String();
		this.setValue(suitParam, valParam);
	}
	
	private void setValue(String suitParam, int valParam)
	{
		if (valParam >= 2 && valParam <= 14)
		{
			if (valParam >= 2 && valParam <= 10)
			{
				name = Integer.toString(valParam);
				isPic = false;
			}
			else
			{
				switch(valParam)
				{
				case 11:
					name = "jack";
					break;
				case 12:
					name = "queen";
					break;
				case 13:
					name = "king";
					break;
				case 14:
					name = "ace";
					break;
				default:
					isPic = true;	
				}
			}
			value = valParam;
				
			if (suitParam.equals("hearts"))
			{
				suit = "hearts";
			}
			else if (suitParam.equals("spades"))
			{
				suit = "spades";
			}
			else if (suitParam.equals("diamonds"))
			{
				suit = "diamonds";
			}
			else if (suitParam.equals("clubs"))
			{
				suit = "clubs";	
			}
		}
		else 
		{
			System.out.println("Value not specified.");
		}
	}

	public String getName()
	{
		return name;
	}

	public String getSuit() 
	{
		return suit;
	}
	
	public boolean isPic()
	{
		return isPic;
	}

	public int getValue() 
	{
		return value;
	}
	
	public int compareTo(Card c)
	{
		if(this.suit.equals(c.suit))
			return c.value - this.value;
		else
		{
			switch(this.suit)
			{
			case "hearts":
				return -1;
			case "spades":
				if(c.suit.equals("hearts"))
					return 1;
				else
					return -1;
			case "diamonds":
				if(c.suit.equals("clubs"))
					return -1;
				else
					return 1;
			case "clubs":
				return 1;
			default:
				return 0;	
			}
		}
	}
}