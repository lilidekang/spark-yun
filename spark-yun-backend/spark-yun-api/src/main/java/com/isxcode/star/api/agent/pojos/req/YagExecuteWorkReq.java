package com.isxcode.star.api.agent.pojos.req;

import com.isxcode.star.api.func.pojos.dto.FuncInfo;
import lombok.Data;

import java.util.List;

@Data
public class YagExecuteWorkReq {

	private PluginReq pluginReq;

	private SparkSubmit sparkSubmit;

	private String agentHomePath;

	private String agentType;

	private String sparkHomePath;

	private String[] args;

	private String argsStr;

	/**
	 * 作业类型.
	 */
	private String workType;

	private String workId;

	/**
	 * 依赖配置.
	 */
	private List<String> libConfig;

	/**
	 * 自定义函数配置.
	 */
	private List<FuncInfo> funcConfig;

	private String workInstanceId;
}
