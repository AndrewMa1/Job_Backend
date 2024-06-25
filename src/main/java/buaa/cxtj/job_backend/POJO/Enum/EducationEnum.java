package buaa.cxtj.job_backend.POJO.Enum;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;


public enum EducationEnum {
    MIDDLE_SCHOOL(1, "初中"),
    HIGH_SCHOOL(2, "高中"),
    BACHELOR(3, "学士"),
    GRADUATE(4, "硕士"),
    DOCTOR(5, "博士");

    @Getter
    @EnumValue
    private final Integer value;
    @JsonValue
    private final String status;

    EducationEnum(Integer value, String status) {
        this.value = value;
        this.status = status;
    }

    public static EducationEnum getEnum(Integer value) {
        for (EducationEnum item : values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
