package org.temp;

public class Tester {
	public static void main(String[] args) throws InterruptedException {
		NearMeServer nms = new NearMeServer();
		for (int i = 0; i < 10; i++) {
			nms.pushEvent("sam", "hellow world", "uknsas");
			//Thread.sleep(100);
			System.out.println(nms.getEvents("sam", "uknsas").length);
		}
	}
}
