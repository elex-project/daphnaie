/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.elex_project.abraxas.ByteArrayBuilder;
import com.elex_project.abraxas.Bytez;
import com.elex_project.abraxas.Numberz;
import com.elex_project.abraxas.Stringz;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

@Slf4j
public class Daphnaie {

	public static void main(String... args) throws IOException {
		String port = "/dev/ttyS0";
		int baudRate = BaudRate._115200.getValue();
		int dataBits = DataBits._8.getValue();
		int stopBits = StopBits._1.getValue();
		int parity = Parity.NO.getValue();

		// request packet
		ByteArrayBuilder reqBuilder = new ByteArrayBuilder();
		reqBuilder.append((byte) 0x7e).append((byte) 0x01).append((byte) 0x01);
		short crc16 = (short) Bytez.crc16Modbus(reqBuilder.toByteArray());
		reqBuilder.append(Bytez.swap(Numberz.toBytes(crc16)));
		final byte[] REQ = reqBuilder.toByteArray();

		// response packet
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
		crc16 = Bytez.crc16Modbus(resBuilder.toByteArray());
		resBuilder.append(Bytez.swap(Numberz.toBytes(crc16)));
		final byte[] RES = resBuilder.toByteArray();

		// open a connection
		SerialIO serialIO = SerialIO.open(port);
		serialIO.setParameters(baudRate, dataBits, stopBits, parity);
		serialIO.setEventListener(new SerialEventListener() {
			@Override
			public void onDataAvailable(SerialIO serialIO, int available) {
				// to synchronize a start byte
				while (serialIO.peek() != (byte) 0x7e && serialIO.available() > 0) {
					serialIO.skip(1);
					log.info("skipped");
				}

				if (available >= 5) {
					byte[] b = new byte[5];
					serialIO.read(b);
					log.info("read : {}" + Bytez.toHex(b));
					// if the input matches to a predefined REQ, then write a RES to the output
					if (Arrays.equals(REQ, b)) {
						serialIO.write(RES);
						log.info("eq");
					} else {
						log.info("!eq : " + Bytez.toHex(REQ));
					}
				}
			}

			@Override
			public void onByteAvailable(final SerialIO serialIO, final byte b) {

			}

			@Override
			public void onException(Exception e) {
				log.error("Something's wrong..", e);
			}

		});

		Scanner scanner = new Scanner(System.in);
		String line = null;
		while ((line = scanner.nextLine()) != null) {
			if (line.equals(Stringz.EMPTY_STRING)) {
				break;
			} else {
				serialIO.write(line);
			}
		}

		serialIO.close();
	}

}
