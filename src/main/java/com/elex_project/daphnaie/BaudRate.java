/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

/**
 * @author Elex
 */
public enum BaudRate {
	_110(110), _300(300), _600(600),
	_1200(1200), _4800(4800), _9600(9600),
	_14400(14400), _19200(19200),
	_38400(38400), _57600(57600),
	_115200(115200), _128000(128000),
	_256000(256000);

	private final int value;

	BaudRate(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
