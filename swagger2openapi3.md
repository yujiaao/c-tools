
# Migrate from Swagger2 to OpenApi3

swagger2	| OpenAPI 3	| 注解位置
---|---|---
@Api |	@Tag(name = “接口类描述”)	| Controller 类上
@ApiOperation	|@Operation(summary =“接口方法描述”)	|Controller 方法上
@ApiImplicitParams	|@Parameters	|Controller 方法上
@ApiImplicitParam	|@Parameter(description=“参数描述”)	|Controller 方法上 @Parameters 里
@ApiParam	|@Parameter(description=“参数描述”)	|Controller 方法的参数上
@ApiIgnore	|@Parameter(hidden = true) 或 @Operation(hidden = true) 或 @Hidden	|-
@ApiModel	|@Schema	|DTO类上
@ApiModelProperty	|@Schema	|DTO属性上



[see](https://blog.csdn.net/qq_35425070/article/details/105347336）
