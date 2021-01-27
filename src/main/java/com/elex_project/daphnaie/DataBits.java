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
public enum DataBits {

	_8(8),
	_7(7),
	_6(6),
	_5(5);

	private final int value;

	DataBits(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
