package reggie.com.compatibility_test;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MyTest extends TestCase{
	public MyTest(String testName){
		super(testName);
	}
	
	public static Test Suite(){
		return new TestSuite(MyTest.class);
	}
	
	public void test01(){
		CompatibilityMethod tc = new CompatibilityMethod();
		tc.getURL();
	}
	
}
