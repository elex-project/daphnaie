/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.fazecast.jSerialComm.SerialPort;
/**
 * @author Elex
 */
public enum StopBits {
	_1(SerialPort.ONE_STOP_BIT),
	_1_5(SerialPort.ONE_POINT_FIVE_STOP_BITS),
	_2(SerialPort.TWO_STOP_BITS);

	private final int value;

	StopBits(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
