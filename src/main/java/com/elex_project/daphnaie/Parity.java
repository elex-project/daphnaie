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
public enum Parity {
	NO(SerialPort.NO_PARITY),
	EVEN(SerialPort.EVEN_PARITY),
	ODD(SerialPort.ODD_PARITY),
	MARK(SerialPort.MARK_PARITY),
	SPACE(SerialPort.SPACE_PARITY);

	private final int value;

	Parity(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
