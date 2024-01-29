package application;

public enum EnumDirection {
	HORIZONTAL("H"),
	VERTICAL("V");
	private String description = null;

	EnumDirection(String desc) {
		this.description = desc;
	}

	public String getDescription() {
		return this.description;
	}

	public static EnumDirection getEnum(String s) {
//    	System.out.println("input: " + s);
		for (EnumDirection e : EnumDirection.values()) {
//    	   	System.out.println("e: " + e + "des: " + e.description);
			if (e.description.equals(s)) {
				return e;
			}
		}
		return null;
	}
}
