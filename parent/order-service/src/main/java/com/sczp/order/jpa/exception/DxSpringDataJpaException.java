package com.sczp.order.jpa.exception;

public class DxSpringDataJpaException extends Exception {
	
	private static final long serialVersionUID = 6019713371555893826L;

	public DxSpringDataJpaException(String msg){
		super(msg);
	}

	public DxSpringDataJpaException(String msg, Throwable throwable){
		super(msg,throwable);
	}
}
