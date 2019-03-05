package sample;

import ilog.rules.res.session.config.IlrPluginConfig;

public class Tester {

	public static void main(String[] args) {
		String[] strs = new String[]{ "one", "two", "three" };
		for(String str : strs)
        	if(str != null)
        		System.out.println(str);
		System.out.println("hello");
	}

}
