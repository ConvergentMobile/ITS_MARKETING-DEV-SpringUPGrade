package category_1;

public enum ItemEnum {
	LUNCH(1), DINNER(2), BREAKFAST(3), 
	BFAST_SIDES(4), LUNCH_SIDES(5), DINNER_SIDES(6),
	LUNCH_APPS(7), DINNER_APPS(8), LUNCH_SOUPS(9), DINNER_SOUPS(10),
	LUNCH_DESSERTS(11), DINNER_DESSERTS(12), 
	NON_ALCOHOL(13), COCKTAILS(14), BEER_WINES(15),
	BREAKFAST_MENU(30), LUNCH_MENU(31), DINNER_MENU(32), DESSERT_MENU(33),
	COCKTAIL_MENU(34),
	GENERAL_SECTION(41),
	OFFER(42),
	EVENT(50);
	
	private int item;
	
	private ItemEnum(int item) {		
		this.item = item;
	}
	
	public int getItem() {
		return item;
	}
	
	public static ItemEnum valueOf(int item) {
		for (ItemEnum m : ItemEnum.values())
			if (m.item == item)
				return m;
		
		throw new AssertionError("Unknown item: " + item);
	}
}
