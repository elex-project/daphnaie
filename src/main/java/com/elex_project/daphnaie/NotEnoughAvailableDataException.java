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
public class NotEnoughAvailableDataException extends Exception {
	NotEnoughAvailableDataException() {
		super();
	}

	NotEnoughAvailableDataException(Throwable e) {
		super(e);
	}

	NotEnoughAvailableDataException(String message) {
		super(message);
	}
}
