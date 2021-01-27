/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.fazecast.jSerialComm.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class SerialCommTest {
	SerialPort comPort;

	@BeforeEach
	void beforeEach() {
		comPort = SerialPort.getCommPort("/dev/ttyUSB0");
		comPort.openPort();
	}

	@AfterEach
	void afterEach() {
		comPort.closePort();
	}

	@Test
	void nonBlockingIO() {
		try {
			comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
			while (true) {
				if (comPort.bytesAvailable() > 0) {
					byte[] readBuffer = new byte[comPort.bytesAvailable()];
					int numRead = comPort.readBytes(readBuffer, readBuffer.length);
					System.out.println("Read " + numRead + " bytes.");
				} else {
					try {
						Thread.sleep(20);
					} catch (InterruptedException ignore) {

					}
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			comPort.closePort();

		}
	}

	@Test
	void blockingIO() {
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
		try {
			while (true) {
				byte[] readBuffer = new byte[1024];
				int numRead = comPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			comPort.closePort();
		}

	}

	@Test
	void withIOStream() {
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		try (InputStream in = comPort.getInputStream()) {
			try {
				for (int j = 0; j < 1000; ++j) {
					System.out.print((char) in.read());
				}

			} catch (Throwable e) {
				e.printStackTrace();
			} finally {
				comPort.closePort();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * In this case, your callback will be triggered whenever there is any data available to be read over the serial port.
	 * Once your callback is triggered, you can optionally call bytesAvailable() to determine how much data is available to read,
	 * and you must actually read the data using any of the read() or readBytes() methods.
	 *
	 * In this case, your callback will be triggered whenever all data you have written using any of the write()
	 * or writeBytes() methods has actually been transmitted.
	 */
	@Test
	void eventDataAvailable() {
		comPort.addDataListener(new SerialPortDataListener() {
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
						| SerialPort.LISTENING_EVENT_DATA_WRITTEN;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
					byte[] newData = new byte[comPort.bytesAvailable()];
					int numRead = comPort.readBytes(newData, newData.length);
					System.out.println("Read " + numRead + " bytes.");
				} else if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
					System.out.println("All bytes were successfully transmitted!");
				}

			}
		});
	}

	/**
	 * In this case, your callback will be triggered whenever some amount of data has actually been read from the serial port.
	 * This raw data will be returned to you within your own callback, so there is no further need to read directly from the serial port.
	 */
	@Test
	void eventDataReceived() {
		comPort.addDataListener(new SerialPortDataListener() {
			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED
						| SerialPort.LISTENING_EVENT_DATA_WRITTEN;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
					byte[] newData = event.getReceivedData();
					System.out.println("Received data of size: " + newData.length);
					for (int i = 0; i < newData.length; ++i) {
						System.out.print((char) newData[i]);
					}
					System.out.println("\n");
				} else if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
					System.out.println("All bytes were successfully transmitted!");
				}

			}
		});
	}

	/**
	 * In this case, your callback will be triggered whenever a set fixed amount of data has been read from the serial port.
	 * This raw data will be returned to you within your own callback, so there is no further need to read directly from the serial port.
	 */
	@Test
	void eventPacketReceived() {
		comPort.addDataListener(new SerialPortPacketListener() {
			@Override
			public int getPacketSize() {
				// it's a fixed-length packet
				return 100;
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
					byte[] newData = event.getReceivedData();
					System.out.println("Received data of size: " + newData.length);
					for (int i = 0; i < newData.length; ++i) {
						System.out.print((char) newData[i]);
					}

					System.out.println("\n");
				}

			}
		});
	}

	/**
	 * In this case, your callback will be triggered whenever a message has been received based on a custom delimiter that you specify.
	 * This delimiter can contain one or more consecutive bytes and can indicate either the beginning or the end of a data packet.
	 * The raw data will be returned to you within your own callback, so there is no further need to read directly from the serial port.
	 */
	@Test
	void eventMessageReceived() {
		comPort.addDataListener(new SerialPortMessageListener() {
			@Override
			public byte[] getMessageDelimiter() {
				// event trigger
				return new byte[]{0x7e};
			}

			@Override
			public boolean delimiterIndicatesEndOfMessage() {
				// start or end
				return false;
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
					byte[] delimitedMessage = event.getReceivedData();
					System.out.println("Received the following delimited message: " + delimitedMessage);

				}

			}
		});
	}
}
