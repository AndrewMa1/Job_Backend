package buaa.cxtj.job_backend.POJO.Enum;

import buaa.cxtj.job_backend.POJO.Entity.Job;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum JobEnum {
    TESTER(1, "测试工程师");
    @Getter
    @EnumValue
    private final Integer value;
    @JsonValue
    private final String status;

    JobEnum(int value, String status) {
        this.status = status;
        this.value = value;
    }

    public static JobEnum getEnum(Integer value) {
        for (JobEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
