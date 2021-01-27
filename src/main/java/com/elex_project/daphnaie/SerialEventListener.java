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
public interface SerialEventListener {
	/**
	 * 입력 버퍼에 데이터가 있는 경우에 호출됨
	 *
	 * @param serialIO
	 * @param available
	 */
	public void onDataAvailable(SerialIO serialIO, int available);

	/**
	 * 입력 버퍼에 어떤 바이트가 들어온 경우에 호출됨
	 *
	 * @param serialIO
	 * @param b
	 */
	public void onByteAvailable(SerialIO serialIO, byte b);

	public void onException(Exception e);

}
