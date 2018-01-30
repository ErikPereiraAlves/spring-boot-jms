package com.erikalves.jmsapp.ecommerce;


import com.erikalves.jmsapp.models.Product;
import com.erikalves.jmsapp.utils.DateUtils;

public class ProductOrderRequester {

	public static void main(String[] args) {
		String url = "tcp://localhost:61616";  //netstat -o -n -a | findstr 61616
		String user = "admin";
		String password = "admin";

		Product product =  new Product("1", "Mentos", "Mentos powermings", "Mentos powermings", "google.com/mentos", 1.5, DateUtils.getCurrentTimestamp(), DateUtils.getCurrentTimestamp() );


		Order  order= new Order(url, user, password,product);
		Inventory inventory = new Inventory(url, user, password);
		Payment payment = new Payment("PaymentQueue", url, user, password);

		new Thread(order, "Order").start();
		new Thread(inventory, "Inventory").start();
		new Thread(payment, "Payment").start();

	}

}