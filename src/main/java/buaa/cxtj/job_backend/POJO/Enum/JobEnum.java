package buaa.cxtj.job_backend.POJO.Enum;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum JobEnum {
    BACKEND_DEV_1(1, "后端开发工程师"),
    BACKEND_DEV_JAVA(2, "Java后端开发工程师"),
    BACKEND_DEV_C(3, "C/C++后端开发工程师"),
    BACKEND_DEV_PHP(4, "PHP后端开发工程师"),
    BACKEND_DEV_PYTHON(5, "Python后端开发工程师"),
    BACKEND_DEV_CSHARP(6, "C#后端开发工程师"),
    BACKEND_DEV_DOTNET(7, ".NET后端开发工程师"),
    BACKEND_DEV_GOLANG(8, "Golang后端开发工程师"),
    BACKEND_DEV_NODEJS(9, "Node.js后端开发工程师"),
    BACKEND_DEV_HADOOP(10, "Hadoop后端开发工程师"),
    BACKEND_DEV_AUDIO_VIDEO(11, "语音/视频/图形开发工程师"),
    GIS_ENGINEER(12, "GIS工程师"),
    BLOCKCHAIN_ENGINEER(13, "区块链工程师"),
    FULL_STACK_ENGINEER(14, "全栈工程师"),
    OTHER_BACKEND_DEV(15, "其他后端开发工程师"),
    FRONTEND_MOBILE_DEV(16, "前端/移动开发工程师"),
    FRONTEND_ENGINEER(17, "前端开发工程师"),
    ANDROID_DEV(18, "Android开发工程师"),
    IOS_DEV(19, "iOS开发工程师"),
    U3D_DEV(20, "U3D开发工程师"),
    UE4_DEV(21, "UE4开发工程师"),
    COCOS_DEV(22, "Cocos开发工程师"),
    TECHNICAL_ART(23, "技术美术"),
    JAVASCRIPT_DEV(24, "JavaScript开发工程师"),
    HARMONY_DEV(25, "鸿蒙开发工程师"),
    TEST_ENGINEER(26, "测试工程师"),
    SOFTWARE_TEST(27, "软件测试工程师"),
    AUTOMATION_TEST(28, "自动化测试工程师"),
    FUNCTIONAL_TEST(29, "功能测试工程师"),
    TEST_DEV(30, "测试开发工程师"),
    HARDWARE_TEST(31, "硬件测试工程师"),
    GAME_TEST(32, "游戏测试工程师"),
    PERFORMANCE_TEST(33, "性能测试工程师"),
    PENETRATION_TEST(34, "渗透测试工程师"),
    TEST_MANAGER(35, "测试经理"),
    OPERATION_MAINTENANCE(36, "运维工程师"),
    IT_SUPPORT(37, "IT技术支持工程师"),
    NETWORK_ENGINEER(38, "网络工程师"),
    NETWORK_SECURITY(39, "网络安全工程师"),
    SYSTEM_ENGINEER(40, "系统工程师"),
    OPERATION_DEV(41, "运维开发工程师"),
    SYSTEM_ADMIN(42, "系统管理员"),
    DBA(43, "DBA"),
    SYSTEM_SECURITY(44, "系统安全工程师"),
    TECHNICAL_DOC_ENGINEER(45, "技术文档工程师"),
    ARTIFICIAL_INTELLIGENCE(46, "人工智能工程师"),
    IMAGE_ALGORITHM(47, "图像算法工程师"),
    NLP_ALGORITHM(48, "自然语言处理算法工程师"),
    LARGE_MODEL_ALGORITHM(49, "大模型算法工程师"),
    DATA_MINING(50, "数据挖掘工程师"),
    CONTROL_ALGORITHM(51, "规控算法工程师"),
    SLAM_ALGORITHM(52, "SLAM算法工程师"),
    RECOMMENDATION_ALGORITHM(53, "推荐算法工程师"),
    SEARCH_ALGORITHM(54, "搜索算法工程师"),
    VOICE_ALGORITHM(55, "语音算法工程师"),
    RISK_CONTROL_ALGORITHM(56, "风控算法工程师"),
    ALGORITHM_RESEARCHER(57, "算法研究员"),
    ALGORITHM_ENGINEER(58, "算法工程师"),
    MACHINE_LEARNING(59, "机器学习工程师"),
    DEEP_LEARNING(60, "深度学习工程师"),
    AUTONOMOUS_DRIVING_SYSTEM(61, "自动驾驶系统工程师"),
    DATA_ANNOTATION_TRAINER(62, "数据标注/AI训练师");


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
