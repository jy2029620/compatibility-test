package reggie.com.compatibility_test;

/**
 * @author: Reggie
 * @data: 2016年9月28日 下午4:32:49
 * @version: V1.0
 */
public enum CompatibilityEnum {
	http_url("http://android.myapp.com/myapp/union.htm?orgame=1&page=1");
	private final String value;

	private CompatibilityEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
