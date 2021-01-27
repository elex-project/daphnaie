/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.elex_project.abraxas.Console;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoTest {
	public static void main(String... args) {
		SerialIO serialIO = SerialIO.open("/dev/ttyUSB0");
		serialIO.addInterestingByte("\n".getBytes()[0]);
		serialIO.setEventListener(new SerialEventListener() {
			@Override
			public void onDataAvailable(final SerialIO serialIO, final int available) {

			}

			@Override
			public void onByteAvailable(final SerialIO serialIO, final byte b) {
				//byte[] buffer = new byte[available];
				try {
					String s = serialIO.readLine();//.read(buffer);
					Console.writeLine(s);
				} catch (NotEnoughAvailableDataException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onException(final Exception e) {
				e.printStackTrace();
			}

		});

		while (true){
			serialIO.writeLine("Hello");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
