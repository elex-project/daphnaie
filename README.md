## Project Daphnaie: for Serial-IO

powered by jSerialComm

## Send

```java
serialIO.writeLine("hello~~ ");
```

## Read with a Event Listener

```java
SerialIO serialIO = SerialIO.open("/dev/ttyUSB0");
serialIO.setParameters(9600,
        DataBits.DATABITS_8, StopBits.STOPBITS_1, Parity.PARITY_NO);
// 이벤트 리스너 등록
serialIO.setEventListener(new SerialEventListener() {
        // 버퍼에 데이터가 있으며 호출된다.
        @Override
        public void onDataAvailable(final SerialIO serialIO, final int available) {
            if (available>=bufferSize) {
                try {
                    // 버퍼가 가득 찬 경우, 강제로 한 줄을 읽고 버린다.
                    serialIO.readLine();
                } catch (NotEnoughAvailableDataException e) {
                    e.printStackTrace();
                }
                Console.writeLine(available + " -> " + serialIO.available());
            }
        }

        // 버퍼에 해당 바이트가 입력되면 호출된다.
        @Override
        public void onByteAvailable(final SerialIO serialIO, final byte b) {
            if ((byte)'\n'==b) {
                List<String> lines = serialIO.readLines();
                for (String line : lines) {
                    Console.writeLine(": " + line);
                }
            } else {
                Console.writeLine("triggered but not a new line");
            }
        }

        @Override
        public void onException(final Exception e) {
            e.printStackTrace();
        }

        @Override
        public void onCTS(final SerialIO serialIO, final boolean on) { }

        @Override
        public void onDSR(final SerialIO serialIO, final boolean on) { }
    });
// 관심 바이트 추가
serialIO.addInterestingByte((byte)'\n');
```

------
Copyright &copt 2018 Elex. All Rights Reserved.
https://www.elex-project.com/
