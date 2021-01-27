/*
 * Project Daphnaie
 * for serial I/O
 *
 * Copyright (c) 2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.daphnaie;

import com.fazecast.jSerialComm.SerialPort;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
/**
 * @author Elex
 */
public enum FlowControl {
	DISABLED(SerialPort.FLOW_CONTROL_DISABLED),
	RTS(SerialPort.FLOW_CONTROL_RTS_ENABLED),
	CTS(SerialPort.FLOW_CONTROL_CTS_ENABLED),
	DSR(SerialPort.FLOW_CONTROL_DSR_ENABLED),
	DTR(SerialPort.FLOW_CONTROL_DTR_ENABLED),
	XON_XOFF_IN(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED),
	XON_XOFF_OUT(SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);

	private final int value;

	FlowControl(int val) {
		this.value = val;
	}

	@Contract(pure = true)
	public static int getValue(FlowControl @NotNull ... flowControls) {
		int mask = 0;
		for (FlowControl item : flowControls) {
			mask |= item.getValue();
		}
		return mask;
	}

	public int getValue() {
		return value;
	}
}
