package org;

import lombok.Data;

//表示RPC的返回
@Data
public class Response {
    //服务返回编码：0为成功，非0为失败
    private int code = 0;
    //具体错误信息
    private String message = "OK";
    //返回数据
    private Object data;
}
