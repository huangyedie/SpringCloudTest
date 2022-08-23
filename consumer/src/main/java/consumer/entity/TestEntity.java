package consumer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_task")
public class TestEntity {
    private Long TaskId;
    private Long TaskCode;
    private Integer Status;
    private String BusinessCode;
    private String BusinessBody;
    private Date CreateTime;
}
