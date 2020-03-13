package com.github.xiaoyao9184.eproject.filetable.util;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by xy on 2020/3/13.
 */
public class UUIDUtil {

    /**
     * UUID to MS FileTable hierarchyid tree node string
     *
     * https://www.tek-tips.com/viewthread.cfm?qid=1785183
     *
     * Microsoft GUID use a mixed-endian format,
     * whereby the first three components of the UUID are little-endian, and the last two are big-endian.
     * https://stackoverflow.com/questions/48659919/convert-a-uuid-into-a-guid-and-vice-versa
     *
     *
     * @param uuid
     * @return
     */
    public static String uuidToHierarchyIdNodeString(UUID uuid){
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);

        //uuid to guid
        long msbBigEndian = uuid.getMostSignificantBits();
        long msbLittleEndian =
                ((msbBigEndian & 0x0000_00FF_0000_0000L) << 24) |
                        ((msbBigEndian & 0x0000_FF00_0000_0000L) << 8) |
                        ((msbBigEndian & 0x00FF_0000_0000_0000L) >>> 8) |
                        ((msbBigEndian & 0xFF00_0000_0000_0000L) >>> 24) |
                        ((msbBigEndian & 0x0000_0000_FF00_0000L) >>> 8) |
                        ((msbBigEndian & 0x0000_0000_00FF_0000L) << 8) |
                        ((msbBigEndian & 0x0000_0000_0000_FF00L) >>> 8) |
                        ((msbBigEndian & 0x0000_0000_0000_00FFL) << 8);

        bb.putLong(msbLittleEndian);
        bb.putLong(uuid.getLeastSignificantBits());
        bb.flip();

        byte[] part1 = new byte[8];
        byte[] part2 = new byte[8];
        byte[] part3 = new byte[4];
        bb.get(part1,2,6);
        bb.get(part2,2,6);
        bb.get(part3,0,4);

        Long p1 = Longs.fromByteArray(part1);
        Long p2 = Longs.fromByteArray(part2);
        Integer p3 = Ints.fromByteArray(part3);

        return p1.toString() + "." +
                p2.toString() + "." +
                p3.toString() + "/";
    }
}
