package buaa.cxtj.job_backend.POJO.Enum;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PositionEnum {
    TESTER(1, "测试工程师");
    @EnumValue
    private final int value;
    @JsonValue
    private final String status;

    PositionEnum(int value, String status) {
        this.status = status;
        this.value = value;
    }
}
