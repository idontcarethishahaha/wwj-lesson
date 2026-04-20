package org.example.exception;

import lombok.Getter;
import org.example.result.ResultCode;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-19 11:56
 */
@Getter
public class ServiceException extends RuntimeException {

    private final ResultCode resultCode;

    public ServiceException(ResultCode resultCode, String coderMessage) {
        super(coderMessage);
        this.resultCode = resultCode;
    }
}
