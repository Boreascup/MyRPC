package protocol;

import lombok.Data;

//表示RPC的返回
@Data
public class Response {
    //状态信息：0为成功，1为失败
    private int code = 0;
    //具体错误信息
    private String message;
    //返回数据
    private Object data;
    public void fail(String message){
        this.code = 1;
        this.message = message;
    }
}
