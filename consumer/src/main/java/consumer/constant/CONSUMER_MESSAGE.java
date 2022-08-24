package consumer.constant;

import com.yanwentech.framework.contract.IResponseResult;
import lombok.Getter;

@Getter
public class CONSUMER_MESSAGE implements IResponseResult {


//    SYSTEM_ERROR("-1","系统异常"),
//
//    REQUEST_URI_ERROR("4001","请求URI错误"),
//
//    REQUEST_BODY_ERROR("4002","请求报文不能为空"),
//
//    REQUEST_SIGNATURE_ERROR("4003","签名错误"),
//
//    INTERNAL_ERROR("5001","系统异常"),
//            ;

    /**
     * 相应码
     */
    private final String code;

    /**
     * 描述信息
     */
    private final String message;

    CONSUMER_MESSAGE(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
