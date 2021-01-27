/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.elex_project.abraxas.Bytez;
import com.elex_project.abraxas.CircularByteBuffer;
import com.elex_project.abraxas.Numberz;
import com.elex_project.abraxas.Stringz;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListenerWithExceptions;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Elex
 */
@Slf4j
public final class SerialIO extends SerialIOBase implements Closeable {
	private static final byte NL = (byte) '\n';
	private static final String NEWLINE = "\n";

	private final CircularByteBuffer inBuffer;
	private SerialEventListener listener;

	public static SerialPort[] getSerialPorts() {
		return SerialPort.getCommPorts();
	}

	private SerialIO(final @NotNull String port, final int inBuffSize) {
		this(port, inBuffSize,
				BaudRate._9600.getValue(),
				DataBits._8.getValue(),
				StopBits._1.getValue(),
				Parity.NO.getValue());
	}

	private SerialIO(final @NotNull String port, final int inBuffSize,
	                 final int baudRate, final int dataBits, final int stopBits, final int parity) {
		serialPort = SerialPort.getCommPort(port);
		serialPort.openPort();

		setParameters(baudRate, dataBits, stopBits, parity);
		//serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 500);
		inBuffer = new CircularByteBuffer(inBuffSize);

		serialPort.addDataListener(new SerialPortDataListenerWithExceptions() {
			@Override
			public void catchException(Exception e) {
				if (null != listener) listener.onException(e);
			}

			@Override
			public int getListeningEvents() {
				return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
			}

			@Override
			public void serialEvent(SerialPortEvent event) {
				byte[] newData = event.getReceivedData();
				inBuffer.put(newData);
			}
		});
	}

	/**
	 * 시리얼 연결
	 *
	 * @param port 시리얼포트, 버퍼 1024
	 * @return
	 */
	public static @NotNull SerialIO open(final @NotNull String port) {
		return open(port, BaudRate._9600);
	}

	/**
	 * 시리얼 연결
	 *
	 * @param port
	 * @param inputBufferSize 버퍼 크기
	 * @return
	 */
	public static @NotNull SerialIO open(final @NotNull String port, final @NotNull BaudRate baudRate) {
		return open(port, 1024,
				baudRate, DataBits._8, StopBits._1, Parity.NO);
	}

	public static @NotNull SerialIO open(final @NotNull String port, final int inputBufferSize,
	                                     final @NotNull BaudRate baudRate, final @NotNull DataBits dataBits, final @NotNull StopBits stopBits, final @NotNull Parity parity) {
		return new SerialIO(port, inputBufferSize,
				baudRate.getValue(),
				dataBits.getValue(),
				stopBits.getValue(),
				parity.getValue());
	}


	/**
	 * 이벤트 리스너
	 *
	 * @param listener
	 */
	public final void setEventListener(@Nullable SerialEventListener listener) {
		this.listener = listener;
	}


	/**
	 * 관심 바이트 추가
	 * 사용 방법) 줄 바꿈 문자를 추가해 둔 뒤, 이벤트가 발생하면 readLines()로 읽어 온다.
	 *
	 * @param v 입력 버퍼에 해당 바이트가 들어오면, 리스너가 호출된다.
	 */
	public void addInterestingByte(final byte v) {
		inBuffer.addListener(new CircularByteBuffer.InputListener() {
			@Override
			public byte getTrigger() {
				return v;
			}

			@Override
			public void onDataAvailable(final byte trigger, final CircularByteBuffer buffer) {
				if (null != listener) {
					listener.onByteAvailable(SerialIO.this, trigger);
				}
			}
		});
	}

	;

	public void removeInterestingByte(final byte v) {
		inBuffer.removeListener(v);
	}

	public void removeAllInterestingBytes() {
		inBuffer.removeAllListeners();
	}

	public void clearBuffer() {
		inBuffer.clear();
	}

	@TestOnly
	CircularByteBuffer getBuffer() {
		return inBuffer;
	}

	void addBufferMonitor(@Nullable CircularByteBuffer.InputListener listener) {
		inBuffer.addListener(listener);
	}

	void removeBufferMonitor(@NotNull CircularByteBuffer.InputListener listener) {
		inBuffer.removeListener(listener);
	}

	void removeBufferMonitor(final byte trigger) {
		inBuffer.removeListener(trigger);
	}

	void removeAllBufferMonitors() {
		inBuffer.removeAllListeners();
	}


	public int read() {
		return inBuffer.get();
	}

	public int read(final byte[] buf) {
		return inBuffer.get(buf);
	}

	public int read(final byte[] buf, final int offset, final int length) {
		return inBuffer.get(buf, offset, length);
	}


	public byte readByte() throws NotEnoughAvailableDataException {
		try {
			return inBuffer.getByte();
		} catch (IndexOutOfBoundsException e) {
			throw new NotEnoughAvailableDataException();
		}
	}

	public short readShort() throws NotEnoughAvailableDataException {
		try {
			return inBuffer.getShort();
		} catch (IndexOutOfBoundsException e) {
			throw new NotEnoughAvailableDataException();
		}
	}

	public int readInt() throws NotEnoughAvailableDataException {
		try {
			return inBuffer.getInt();
		} catch (IndexOutOfBoundsException e) {
			throw new NotEnoughAvailableDataException();
		}
	}

	public long readLong() throws NotEnoughAvailableDataException {
		try {
			return inBuffer.getLong();
		} catch (IndexOutOfBoundsException e) {
			throw new NotEnoughAvailableDataException();
		}
	}

	public String readString(final byte terminalByte, @NotNull final String encoding)
			throws NotEnoughAvailableDataException, UnsupportedEncodingException {
		try {
			return inBuffer.getString(terminalByte, encoding);
		} catch (IndexOutOfBoundsException e) {
			throw new NotEnoughAvailableDataException();
		}
	}

	public String readString(final byte terminalByte, final @NotNull Charset encoding)
			throws NotEnoughAvailableDataException {
		try {
			return inBuffer.getString(terminalByte, encoding);
		} catch (IndexOutOfBoundsException e) {
			throw new NotEnoughAvailableDataException();
		}
	}

	/**
	 * '\n'까지 읽는다.
	 *
	 * @param encoding
	 * @return
	 * @throws NotEnoughAvailableDataException
	 * @throws UnsupportedEncodingException
	 */
	public final String readLine(@NotNull final String encoding)
			throws NotEnoughAvailableDataException, UnsupportedEncodingException {
		return readString(NL, encoding);
	}

	public final String readLine(@NotNull final Charset encoding)
			throws NotEnoughAvailableDataException {
		return readString(NL, encoding);
	}

	/**
	 * '\n'까지 ASCII 형식으로 읽는다.
	 *
	 * @return
	 * @throws NotEnoughAvailableDataException
	 */
	public final String readLine()
			throws NotEnoughAvailableDataException {
		try {
			return readLine(StandardCharsets.US_ASCII);
		} catch (NoClassDefFoundError e) {
			try {
				return readLine(Stringz.ASCII);
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	public @NotNull List<String> readLines() {
		List<String> lines = new ArrayList<>();
		while (inBuffer.seek(NL) > 0) {
			try {
				lines.add(readLine());
			} catch (NotEnoughAvailableDataException e) {
				if (null != listener) listener.onException(e);
			}
		}
		return lines;
	}


	/**
	 * 수신 버퍼에 대기 중인 데이터 길이
	 *
	 * @return
	 */
	public int available() {
		return inBuffer.available();
	}

	/**
	 * 특정 바이트의 위치. 없으면 -1.
	 *
	 * @param b
	 * @return
	 */
	public int seek(final byte b) {
		return inBuffer.seek(b);
	}

	/**
	 * 수신 버퍼의 데이터 일부를 폐기
	 *
	 * @param length 시작 위치부터의 데이터 길이
	 * @return
	 */
	public int skip(final int length) {
		return inBuffer.skip(length);
	}

	/**
	 * 수신 버퍼의 맨 앞 데이터 확인
	 *
	 * @return
	 */
	public byte peek() {
		return (byte) inBuffer.peek();
	}

	public int write(final byte val) {
		return serialPort.writeBytes(new byte[]{val}, 1);
	}

	public int write(final byte[] data) {
		return serialPort.writeBytes(data, data.length, 0);
	}

	public int write(final byte[] val, final int offset, final int length) {
		return serialPort.writeBytes(val, length, offset);
	}

	public int write(final short val) {
		return write(Numberz.toBytes(val));
	}

	public int write(final int val) {
		return write(Numberz.toBytes(val));
	}

	public int write(final long val) {
		return write(Numberz.toBytes(val));
	}

	public int write(@NotNull final String val) {
		try {
			return write(val.getBytes(Stringz.ASCII));
		} catch (UnsupportedEncodingException e) {
			if (null != listener) listener.onException(e);
			return write(val.getBytes());
		}
	}

	public int write(final @NotNull String val, final @NotNull String charset) throws UnsupportedEncodingException {
		return write(val.getBytes(charset));
	}

	public int write(final @NotNull String val, final @NotNull Charset charset) {
		return write(val.getBytes(charset));
	}

	public int writeHex(final @NotNull String hexStr) throws IllegalArgumentException {
		return write(Bytez.fromHex(hexStr));
	}

	/**
	 * 데이터 끝에 '\n'을 추가해서 전송한다.
	 *
	 * @param val
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public final int writeLine(@NotNull final String val, @NotNull final String charset)
			throws UnsupportedEncodingException {
		return write(val + NEWLINE, charset);
	}

	public final int writeLine(@NotNull final String val, @NotNull final Charset charset) {
		return write(val + NEWLINE, charset);
	}

	/**
	 * 데이터 끝에 '\n'을 추가해서 ASCII 형식으로 전송한다.
	 *
	 * @param val
	 * @return
	 */
	public final int writeLine(@NotNull final String val) {
		try {
			return writeLine(val, StandardCharsets.US_ASCII);
		} catch (NoClassDefFoundError e) {
			try {
				return writeLine(val, Stringz.ASCII);
			} catch (UnsupportedEncodingException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	public void close() throws IOException {
		inBuffer.clear();
		//eventThread.isRunning = false;
		serialPort.closePort();
	}


}
