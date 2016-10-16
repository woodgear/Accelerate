package com.misakimei.accelerate;

import com.misakimei.accelerate.exception.RecoverFailException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 18754 on 2016/8/10.
 * 重生
 */
public interface reincarnation {
    void init(InputStream in) throws RecoverFailException, IOException;

    int preserve(OutputStream out) throws RecoverFailException;

    byte getByte();

}
