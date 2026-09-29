package com.wuji.common.utils;


import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;


public final class ObjectId implements Comparable<ObjectId>, Serializable {
    private static final long serialVersionUID = 3670079982654483072L;
    private static final int LOW_ORDER_THREE_BYTES = 16777215;
    private static final int MACHINE_IDENTIFIER;
    private static final short PROCESS_IDENTIFIER;
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger((new SecureRandom()).nextInt());
    private static final char[] HEX_CHARS =
            new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private final int timestamp;
    private final int machineIdentifier;
    private final short processIdentifier;
    private final int counter;

    ObjectId(Date date, int counter) {
        this(date, MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, counter);
    }

    ObjectId(Date date, int machineIdentifier, short processIdentifier, int counter) {
        this(dateToTimestampSeconds(date), machineIdentifier, processIdentifier, counter);
    }

    ObjectId(int timestamp, int machineIdentifier, short processIdentifier, int counter) {
        this(timestamp, machineIdentifier, processIdentifier, counter, true);
    }

    ObjectId(int timestamp, int machineIdentifier, short processIdentifier, int counter, boolean checkCounter) {
        if ((machineIdentifier & -16777216) != 0) {
            throw new IllegalArgumentException(
                    "The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
        } else if (checkCounter && (counter & -16777216) != 0) {
            throw new IllegalArgumentException(
                    "The counter must be between 0 and 16777215 (it must fit in three bytes).");
        } else {
            this.timestamp = timestamp;
            this.machineIdentifier = machineIdentifier;
            this.processIdentifier = processIdentifier;
            this.counter = counter & 16777215;
        }
    }

    ObjectId(String hexString) {
        this(parseHexString(hexString));
    }

    ObjectId(byte[] bytes) {
        this(ByteBuffer.wrap(bytes));
    }

    ObjectId(int timestamp, int machineAndProcessIdentifier, int counter) {
        this(legacyToBytes(timestamp, machineAndProcessIdentifier, counter));
    }

    ObjectId(ByteBuffer buffer) {
        this.timestamp = makeInt(buffer.get(), buffer.get(), buffer.get(), buffer.get());
        this.machineIdentifier = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
        this.processIdentifier = (short) makeInt((byte) 0, (byte) 0, buffer.get(), buffer.get());
        this.counter = makeInt((byte) 0, buffer.get(), buffer.get(), buffer.get());
    }

    ObjectId() {
        this(new Date());
    }

    ObjectId(Date date) {
        this(dateToTimestampSeconds(date), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, NEXT_COUNTER.getAndIncrement(),
                false);
    }

    public static String getGuid() {
        return ObjectId.get().toString();
    }

    public static ObjectId get() {
        return new ObjectId();
    }

    public static boolean isValid(String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException();
        } else {
            int len = hexString.length();
            if (len != 24) {
                return false;
            } else {
                for (int i = 0; i < len; ++i) {
                    char c = hexString.charAt(i);
                    if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static int getGeneratedMachineIdentifier() {
        return MACHINE_IDENTIFIER;
    }

    public static int getGeneratedProcessIdentifier() {
        return PROCESS_IDENTIFIER;
    }

    public static int getCurrentCounter() {
        return NEXT_COUNTER.get();
    }

    public static ObjectId createFromLegacyFormat(int time, int machine, int inc) {
        return new ObjectId(time, machine, inc);
    }

    private static byte[] legacyToBytes(int timestamp, int machineAndProcessIdentifier, int counter) {
        return new byte[]{int3(timestamp), int2(timestamp), int1(timestamp), int0(timestamp),
                int3(machineAndProcessIdentifier), int2(machineAndProcessIdentifier), int1(machineAndProcessIdentifier),
                int0(machineAndProcessIdentifier), int3(counter), int2(counter), int1(counter), int0(counter)};
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(12);
        this.putToByteBuffer(buffer);
        return buffer.array();
    }

    public void putToByteBuffer(ByteBuffer buffer) {

        buffer.put(int3(this.timestamp));
        buffer.put(int2(this.timestamp));
        buffer.put(int1(this.timestamp));
        buffer.put(int0(this.timestamp));
        buffer.put(int2(this.machineIdentifier));
        buffer.put(int1(this.machineIdentifier));
        buffer.put(int0(this.machineIdentifier));
        buffer.put(short1(this.processIdentifier));
        buffer.put(short0(this.processIdentifier));
        buffer.put(int2(this.counter));
        buffer.put(int1(this.counter));
        buffer.put(int0(this.counter));
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public int getMachineIdentifier() {
        return this.machineIdentifier;
    }

    public short getProcessIdentifier() {
        return this.processIdentifier;
    }

    public int getCounter() {
        return this.counter;
    }

    public Date getDate() {
        return new Date((long) this.timestamp * 1000L);
    }

    public String toHexString() {
        char[] chars = new char[24];
        int i = 0;
        byte[] var3 = this.toByteArray();
        int var4 = var3.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            chars[i++] = HEX_CHARS[(b >> 4) & 15];
            chars[i++] = HEX_CHARS[b & 15];
        }

        return new String(chars);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ObjectId objectId = (ObjectId) o;
            if (this.counter != objectId.counter) {
                return false;
            } else if (this.machineIdentifier != objectId.machineIdentifier) {
                return false;
            } else if (this.processIdentifier != objectId.processIdentifier) {
                return false;
            } else {
                return this.timestamp == objectId.timestamp;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.timestamp;
        result = 31 * result + this.machineIdentifier;
        result = 31 * result + this.processIdentifier;
        result = 31 * result + this.counter;
        return result;
    }

    public int compareTo(ObjectId other) {
        if (other == null) {
            throw new NullPointerException();
        } else {
            byte[] byteArray = this.toByteArray();
            byte[] otherByteArray = other.toByteArray();

            for (int i = 0; i < 12; ++i) {
                if (byteArray[i] != otherByteArray[i]) {
                    return (byteArray[i] & 255) < (otherByteArray[i] & 255) ? -1 : 1;
                }
            }

            return 0;
        }
    }

    public String toString() {
        return this.toHexString();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public int getTimeSecond() {
        return this.timestamp;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public long getTime() {
        return (long) this.timestamp * 1000L;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String toStringMongod() {
        return this.toHexString();
    }

    private static int createMachineIdentifier() {
        int machinePiece;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            if (e == null) {
                return 0;
            }
            while (e.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);

                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException var7) {
                    }
                }
            }

            machinePiece = sb.toString().hashCode();
        } catch (Throwable var8) {
            machinePiece = (new SecureRandom()).nextInt();
        }

        machinePiece &= 16777215;
        return machinePiece;
    }

    private static short createProcessIdentifier() {
        short processId;
        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf(64)));
            } else {
                processId = (short) ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }
        } catch (Throwable var2) {
            processId = (short) (new SecureRandom()).nextInt();
        }

        return processId;
    }

    private static byte[] parseHexString(String s) {
        if (!isValid(s)) {
            throw new IllegalArgumentException("invalid hexadecimal representation of an ObjectId: [" + s + "]");
        } else {
            byte[] b = new byte[12];

            for (int i = 0; i < b.length; ++i) {
                b[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            }

            return b;
        }
    }

    private static int dateToTimestampSeconds(Date time) {
        return (int) (time.getTime() / 1000L);
    }

    private static int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (b3 << 24) | ((b2 & 255) << 16) | ((b1 & 255) << 8) | (b0 & 255);
    }

    private static byte int3(int x) {
        return (byte) (x >> 24);
    }

    private static byte int2(int x) {
        return (byte) (x >> 16);
    }

    private static byte int1(int x) {
        return (byte) (x >> 8);
    }

    private static byte int0(int x) {
        return (byte) x;
    }

    private static byte short1(short x) {
        return (byte) (x >> 8);
    }

    private static byte short0(short x) {
        return (byte) x;
    }

    static {
        try {
            MACHINE_IDENTIFIER = createMachineIdentifier();
            PROCESS_IDENTIFIER = createProcessIdentifier();
        } catch (Exception var1) {
            throw new RuntimeException(var1);
        }
    }
}








