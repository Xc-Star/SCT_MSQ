package com.sct.exception.file;

import com.sct.exception.BaseException;

/**
 * 文件信息异常类
 *
 * @Author hongshu
 */
public class FileException extends BaseException {

    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args) {
        super("file", code, args, null);
    }

}
