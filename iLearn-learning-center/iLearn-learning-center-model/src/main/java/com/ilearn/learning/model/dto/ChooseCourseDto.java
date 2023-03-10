package com.ilearn.learning.model.dto;

import com.ilearn.learning.model.po.ChooseCourse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoxiaoyi
 * @version 1.0
 * @description 选课Dto
 * @date 3/9/2023 4:29 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChooseCourseDto extends ChooseCourse {

    /**
     * 学习资格:
     * [
     * {"code":"702001","desc":"正常学习"},
     * {"code":"702002","desc":"没有选课或选课后没有支付"},
     * {"code":"702003","desc":"已过期需要申请续期或重新支付"}
     * ]
     */
    public String learningStatus;

}
