package com.misakimei.accelerate.tool;

import com.misakimei.accelerate.geom.ShapeType;

import java.util.HashMap;

public class CommandMap {

    private static final String TAG = "CommandMap";


    //TODO 双向map 最好写出来
    private static final HashMap<Byte, Integer> byteintmap = new HashMap<>();
    private static final HashMap<CommandType, Integer> typeintmap = new HashMap<>();
    private static final HashMap<Byte, ShapeType> byteshapemap = new HashMap<>();
    private static final HashMap<ShapeType, Byte> shapetypemap = new HashMap<>();
    private static final bean[] map = {
            new bean(9, CommandType.PATH_MOVE_START, (byte) 0x1),
            new bean(9, CommandType.PATH_MOVE_ON, (byte) 0x2),
            new bean(1, CommandType.PATH_MOVE_END, (byte) 0x3),
            new bean(1, CommandType.CAMERA_CHANGE_START, (byte) 0x4),
            new bean(13, CommandType.CAMERA_CHANGE_ON, (byte) 0x5),
            new bean(1, CommandType.CAMERA_CHANGE_END, (byte) 0x6),
            new bean(1, CommandType.SYSN_START, (byte) 0x7),
            new bean(13, CommandType.DEVICE_SYSN, (byte) 0x8),
            new bean(21, CommandType.CAMERA_SYSN, (byte) 0x9),
            new bean(1, CommandType.SYSN_END, (byte) 0xa),
            new bean(13, CommandType.PAINT_CHANGE, (byte) 0xb),
            new bean(1, CommandType.UNDO, (byte) 0xc),
            new bean(1, CommandType.REDO, (byte) 0xd),
            new bean(1, CommandType.NULL, (byte) 0xe),
            new bean(10, CommandType.SHAPECORRECT, (byte) 0xf),

    };

    static {
        byteshapemap.put((byte) 0x1, ShapeType.LINE);
        byteshapemap.put((byte) 0x2, ShapeType.TRIANGLE);
        byteshapemap.put((byte) 0x3, ShapeType.RECTANGLE);
        byteshapemap.put((byte) 0x4, ShapeType.ELLIPSES);
        byteshapemap.put((byte) 0x5, ShapeType.CIRCLE);
        byteshapemap.put((byte) 0x6, ShapeType.CURVE);


        shapetypemap.put(ShapeType.LINE, (byte) 0x1);
        shapetypemap.put(ShapeType.TRIANGLE, (byte) 0x2);
        shapetypemap.put(ShapeType.RECTANGLE, (byte) 0x3);
        shapetypemap.put(ShapeType.ELLIPSES, (byte) 0x4);
        shapetypemap.put(ShapeType.CIRCLE, (byte) 0x5);
        shapetypemap.put(ShapeType.CURVE, (byte) 0x6);
    }

    static {
        byteintmap.put((byte) 0x1, 0);
        byteintmap.put((byte) 0x2, 1);
        byteintmap.put((byte) 0x3, 2);
        byteintmap.put((byte) 0x4, 3);
        byteintmap.put((byte) 0x5, 4);
        byteintmap.put((byte) 0x6, 5);
        byteintmap.put((byte) 0x7, 6);
        byteintmap.put((byte) 0x8, 7);
        byteintmap.put((byte) 0x9, 8);
        byteintmap.put((byte) 0xa, 9);
        byteintmap.put((byte) 0xb, 10);
        byteintmap.put((byte) 0xc, 11);
        byteintmap.put((byte) 0xd, 12);
        byteintmap.put((byte) 0xe, 13);


        typeintmap.put(CommandType.PATH_MOVE_START, 0);
        typeintmap.put(CommandType.PATH_MOVE_ON, 1);
        typeintmap.put(CommandType.PATH_MOVE_END, 2);
        typeintmap.put(CommandType.CAMERA_CHANGE_START, 3);
        typeintmap.put(CommandType.CAMERA_CHANGE_ON, 4);
        typeintmap.put(CommandType.CAMERA_CHANGE_END, 5);
        typeintmap.put(CommandType.SYSN_START, 6);
        typeintmap.put(CommandType.DEVICE_SYSN, 7);
        typeintmap.put(CommandType.CAMERA_SYSN, 8);
        typeintmap.put(CommandType.SYSN_END, 9);
        typeintmap.put(CommandType.PAINT_CHANGE, 10);
        typeintmap.put(CommandType.UNDO, 11);
        typeintmap.put(CommandType.REDO, 12);
        typeintmap.put(CommandType.NULL, 13);
        typeintmap.put(CommandType.SHAPECORRECT, 14);//TODO SHAPECORRECT 需要更改整个Map


        byteshapemap.put((byte) 0x1, ShapeType.LINE);
        byteshapemap.put((byte) 0x2, ShapeType.TRIANGLE);
        byteshapemap.put((byte) 0x3, ShapeType.RECTANGLE);
        byteshapemap.put((byte) 0x4, ShapeType.ELLIPSES);
        byteshapemap.put((byte) 0x5, ShapeType.CIRCLE);
        byteshapemap.put((byte) 0x6, ShapeType.CURVE);


        shapetypemap.put(ShapeType.LINE, (byte) 0x1);
        shapetypemap.put(ShapeType.TRIANGLE, (byte) 0x2);
        shapetypemap.put(ShapeType.RECTANGLE, (byte) 0x3);
        shapetypemap.put(ShapeType.ELLIPSES, (byte) 0x4);
        shapetypemap.put(ShapeType.CIRCLE, (byte) 0x5);
        shapetypemap.put(ShapeType.CURVE, (byte) 0x6);

    }

    public static ShapeType getShapeType(byte b) {
        return byteshapemap.get(b);
    }

    public static byte getShapeTypeByte(ShapeType type) {
        return shapetypemap.get(type);
    }

    public static CommandType getCommandTypeFromByte(byte data) {
        return map[byteintmap.get(data)].type;
    }

    public static byte getByteFromCommandType(CommandType type) {
        return map[typeintmap.get(type)].typebyte;
    }

    public static int getTypeLength(CommandType type) {
        return map[typeintmap.get(type)].length;
    }

    static class bean {
        byte typebyte;
        CommandType type;
        int length;

        public bean(int length, CommandType type, byte typebyte) {
            this.length = length;
            this.type = type;
            this.typebyte = typebyte;
        }
    }
}
