/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.fazecast.jSerialComm.SerialPort;
import org.jetbrains.annotations.NotNull;

abstract class SerialIOBase {
	protected SerialPort serialPort;

	public String getPortName() {
		return serialPort.getDescriptivePortName();
	}

	public void setTimeout(final int mode, final int readTimeout, final int writeTimeout) {
		serialPort.setComPortTimeouts(mode, readTimeout, writeTimeout);
	}

	public void setBlockingMode() {
		serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
	}

	/**
	 * 통신 설정
	 *
	 * @param baudRate
	 * @param dataBits
	 * @param stopBits
	 * @param parity
	 */
	public final void setParameters(final @NotNull BaudRate baudRate, final @NotNull DataBits dataBits, final @NotNull StopBits stopBits, final @NotNull Parity parity) {
		setParameters(baudRate.getValue(), dataBits.getValue(), stopBits.getValue(), parity.getValue());
	}

	/**
	 * 통신 설정
	 *
	 * @param baudRate
	 * @param dataBits
	 * @param stopBits
	 * @param parity
	 */
	public void setParameters(final int baudRate, final int dataBits, final int stopBits, final int parity) {
		serialPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
	}

	/**
	 * 통신 설정
	 *
	 * @param baudRate
	 * @param dataBits
	 * @param stopBits
	 * @param parity
	 */
	public final void setParameters(final int baudRate, @NotNull final DataBits dataBits, @NotNull final StopBits stopBits, @NotNull final Parity parity) {
		setParameters(baudRate, dataBits.getValue(), stopBits.getValue(), parity.getValue());
	}

	public int getFlowControlMode() {
		return serialPort.getFlowControlSettings();
	}

	public void setFlowControlMode(final int mask) {
		serialPort.setFlowControl(mask);
	}

	public final void setFlowControlMode(final FlowControl @NotNull ... flowControls) {
		setFlowControlMode(FlowControl.getValue(flowControls));
	}

	public boolean setBreak() {
		return serialPort.setBreak();
	}

	public boolean clearBreak() {
		return serialPort.clearBreak();
	}

	public boolean getCTS() {
		return serialPort.getCTS();
	}

	public boolean getDSR() {
		return serialPort.getDSR();
	}

	public boolean getDCD() {
		return serialPort.getDCD();
	}

	/**
	 * Data Terminal Ready, 데이터 터미널 준비
	 *
	 * @return
	 */
	public boolean setDTR() {
		return serialPort.setDTR();
	}

	public boolean clearDTR() {
		return serialPort.clearDTR();
	}

	public boolean getRI() {
		return serialPort.getRI();
	}

	/**
	 * Request To Send, 송신 요구
	 *
	 * @return
	 */
	public boolean setRTS() {
		return serialPort.setRTS();
	}

	public boolean clearRTS() {
		return serialPort.clearRTS();
	}

	public synchronized boolean sendBreak(int duration) {
		if (serialPort.setBreak()) {
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(duration);
					} catch (InterruptedException ignore) {
						//L.e(TAG, e);
					}
					serialPort.clearBreak();
				}
			}.start();
			return true;
		} else {
			return false;
		}
	}
}
