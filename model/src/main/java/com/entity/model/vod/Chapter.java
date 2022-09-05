package com.entity.model.vod;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.entity.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Chapter")
@TableName("chapter")
public class Chapter extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	@TableId(type = IdType.INPUT)
	private Long id;

	@ApiModelProperty(value = "课程ID")
	@TableField("course_id")
	private Long courseId;

	@ApiModelProperty(value = "章节名称")
	@TableField("title")
	private String title;

	@ApiModelProperty(value = "显示排序")
	@TableField("sort")
	private Integer sort;

}