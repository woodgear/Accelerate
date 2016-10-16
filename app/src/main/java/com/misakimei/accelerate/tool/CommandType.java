package com.misakimei.accelerate.tool;

public enum CommandType {
    //Path
    PATH_MOVE_START,
    PATH_MOVE_ON,
    PATH_MOVE_END,
    //Camera
    CAMERA_CHANGE_START,
    CAMERA_CHANGE_ON,
    CAMERA_CHANGE_END,

    //SYSN
    SYSN_START,
    DEVICE_SYSN,
    CAMERA_SYSN,
    SYSN_END,

    //Paint
    PAINT_CHANGE,

    //undo redo
    UNDO,
    REDO,
    //wrong
    NULL,
    //形状修正
    SHAPECORRECT,
}
