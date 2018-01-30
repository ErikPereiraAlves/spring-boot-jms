package com.erikalves.jmsapp.ecommerce;


public class TransactionsDemo {

	public static void main(String[] args) {
		String url = "tcp://localhost:61616";  //netstat -o -n -a | findstr 61616
		String user = "admin";
		String password = "admin";

		
		Order  order= new Order(url, user, password);
		Inventory inventory = new Inventory(url, user, password);
		Payment payment = new Payment("PaymentQueue", url, user, password);

		new Thread(order, "Order").start();
		new Thread(inventory, "Inventory").start();
		new Thread(payment, "Payment").start();

	}

}