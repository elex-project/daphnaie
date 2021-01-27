/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.elex_project.abraxas.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class SerialIOTest {
	@Test //@Ignore
	public void testArduino() throws IOException {
		boolean running = true;
		SerialIO serialIO = SerialIO.open("/dev/ttyUSB0", 512,
				BaudRate._9600, DataBits._8, StopBits._1, Parity.NO);
		//serialIO.setParameters(BaudRate._9600, DataBits._8, StopBits._1, Parity.NO);

		serialIO.setEventListener(new SerialEventListener() {
			@Override
			public void onDataAvailable(final SerialIO serialIO, final int available) {
				Console.writeLine("  " + (serialIO).getBuffer());
				if ((serialIO).getBuffer().free() <= 0) {
					/*try {
						// 버퍼가 가득 찬 경우, 강제로 한 줄을 읽고 버린다.
						serialIO.readLine();
					} catch (NotEnoughAvailableDataException e) {
						e.printStackTrace();
					}*/
					Console.writeLine(available + " -> " + (serialIO).getBuffer().capacity());
				}
			}

			@Override
			public void onByteAvailable(final SerialIO serialIO, final byte b) {
				if ((byte) '\n' == b) {
					List<String> lines = serialIO.readLines();
					for (String line : lines) {
						if (!Stringz.isEmpty(line)) {
							Console.writeLine(": " + (serialIO).getBuffer().toString());

							Console.writeLine(": " + line + "  " + Bytez
									.toHex(":", line.getBytes(StandardCharsets.UTF_8)));

						}
					}
				} else {
					Console.writeLine("triggered but not a new line");
				}
			}

			@Override
			public void onException(final Exception e) {
				e.printStackTrace();
			}

		});
		serialIO.addInterestingByte((byte) '\n');
		serialIO.clearBuffer();
		//Console.writeLine("  " + ((SerialIOImpl)serialIO).getBuffer().toString());
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Console.writeLine("  " + (serialIO).getBuffer().toString());
		int r = serialIO.writeLine("Hi, there"); // 48:69:2c:20:74:68:65:72:65:2e
		//System.out.println(r);
		int i = 0;
		while (running) {
			serialIO.writeLine("hello~ " + i * 10);
			i++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (i > 110) running = false;
		}
		serialIO.close();
	}

	@Test
	@Disabled
	public void test() {
		ByteArrayBuilder resBuilder = new ByteArrayBuilder();
		resBuilder.append((byte) 0x7e).append((byte) 0x01).append((byte) 0x02);
		ByteArrayBuilder dataBuilder = new ByteArrayBuilder();
		dataBuilder
				.append((short) 220)
				.append((short) 150)
				.append((short) 10)
				.append((short) 220)
				.append((short) 150)
				.append((short) 10)
				.append((short) 999)
				.append((short) 600)
				.append(16777215L)
				.append((short) 0x0001);
		byte[] resData = dataBuilder.toByteArray();
		resBuilder.append((short) resData.length).append(resData);
		short crc16 = (short) Bytez.crc16Modbus(resBuilder.toByteArray());
		resBuilder.append(Bytez.swap(Numberz.toBytes(crc16)));
		final byte[] RES = resBuilder.toByteArray();
		log.info(Bytez.toHex(RES));

		assertEquals("7e0102001a00dc0096000a00dc0096000a03e702580000000000ffffff000138a2",
				Bytez.toHex(RES));
	}

	@Test
	public void test2() {
		byte[] b = Bytez.fromHex("3e3e208b09a2a195c995b90a0d0a");
		Console.writeLine(new String(b));

		Console.writeLine(Bytez.toHex(":", "Hi, there.".getBytes()));
	}
}
