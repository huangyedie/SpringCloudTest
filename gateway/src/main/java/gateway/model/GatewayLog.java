package gateway.model;

import lombok.Data;


/**
 * @author : huangmingshun
 * @description : gateWayLog
 * @Date : 2022/8/24 11:19
 */
@Data
public class GatewayLog {
    private String requestPath;
    private String requestMethod;
    private String schema;
    private String requestBody;
    private String responseBody;
    private String ip;
    private String requestTime;
    private String responseTime;
    private Long executeTime;
    private String trackId;

}
