/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.boot.nacos.config.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.alibaba.boot.nacos.config.NacosConfigConstants;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.config.ConfigType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.Assert;

/**
 * {@link ConfigurationProperties} for configuring Nacos Config.
 * 配置属性集
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 * @see ConfigurationProperties
 */
@ConfigurationProperties(NacosConfigConstants.PREFIX)
public class NacosConfigProperties {

	/**
	 * 服务端地址
	 */
	private String serverAddr = "127.0.0.1:8848";

	private String contextPath;

	/**
	 * 数据编码
	 */
	private String encode;

	/**
	 * 应用端点
	 */
	private String endpoint;

	/**
	 * 命名空间
	 */
	private String namespace;

	/**
	 * 访问键
	 */
	private String accessKey;

	/**
	 * 密钥键
	 */
	private String secretKey;

	/**
	 * 原始角色名称
	 */
	private String ramRoleName;

	/**
	 * 配置自动刷新启用
	 */
	private boolean autoRefresh = false;

	/**
	 * 数据身份
	 */
	private String dataId;

	/**
	 * 数据身份列表
	 */
	private String dataIds;

	/**
	 * 分组身份
	 */
	private String group = Constants.DEFAULT_GROUP;

	/**
	 * 配置类型
	 */
	private ConfigType type = ConfigType.PROPERTIES;

	/**
	 * 最大重试次数
	 */
	private String maxRetry;

	/**
	 * 长轮询连接的超时时间
	 */
	private String configLongPollTimeout;

	/**
	 * 重试超时时间
	 */
	private String configRetryTime;

	/**
	 * 远程同步配置启用
	 */
	private boolean enableRemoteSyncConfig = false;

	/**
	 * 用户名称
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 远程配置优先
	 */
	private boolean remoteFirst = false;

	/**
	 * 扩展配置
	 */
	@JsonIgnore
	private List<Config> extConfig = new ArrayList<>();

	/**
	 * 引导配置属性
	 */
	@NestedConfigurationProperty
	private Bootstrap bootstrap = new Bootstrap();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		Assert.notNull(serverAddr, "nacos config server-addr must not be null");
		this.serverAddr = serverAddr;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getRamRoleName() {
		return ramRoleName;
	}

	public void setRamRoleName(String ramRoleName) {
		this.ramRoleName = ramRoleName;
	}

	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	public void setAutoRefresh(boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getDataIds() {
		return dataIds;
	}

	public void setDataIds(String dataIds) {
		this.dataIds = dataIds;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public ConfigType getType() {
		return type;
	}

	public void setType(ConfigType type) {
		this.type = type;
	}

	public String getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(String maxRetry) {
		this.maxRetry = maxRetry;
	}

	public String getConfigLongPollTimeout() {
		return configLongPollTimeout;
	}

	public void setConfigLongPollTimeout(String configLongPollTimeout) {
		this.configLongPollTimeout = configLongPollTimeout;
	}

	public String getConfigRetryTime() {
		return configRetryTime;
	}

	public void setConfigRetryTime(String configRetryTime) {
		this.configRetryTime = configRetryTime;
	}

	public boolean isEnableRemoteSyncConfig() {
		return enableRemoteSyncConfig;
	}

	public void setEnableRemoteSyncConfig(boolean enableRemoteSyncConfig) {
		this.enableRemoteSyncConfig = enableRemoteSyncConfig;
	}

	public boolean isRemoteFirst() {
		return remoteFirst;
	}

	public void setRemoteFirst(boolean remoteFirst) {
		this.remoteFirst = remoteFirst;
	}

	public List<Config> getExtConfig() {
		return extConfig;
	}

	public void setExtConfig(List<Config> extConfig) {
		this.extConfig = extConfig;
	}

	public Bootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("NacosConfigProperties{");
		sb.append("serverAddr='").append(serverAddr).append('\'');
		sb.append(", contextPath='").append(contextPath).append('\'');
		sb.append(", encode='").append(encode).append('\'');
		sb.append(", endpoint='").append(endpoint).append('\'');
		sb.append(", namespace='").append(namespace).append('\'');
		sb.append(", accessKey='").append(Objects.isNull(accessKey) ? null : "******").append('\'');
		sb.append(", secretKey='").append(Objects.isNull(secretKey) ? null : "******").append('\'');
		sb.append(", ramRoleName='").append(ramRoleName).append('\'');
		sb.append(", autoRefresh=").append(autoRefresh);
		sb.append(", dataId='").append(dataId).append('\'');
		sb.append(", dataIds='").append(dataIds).append('\'');
		sb.append(", group='").append(group).append('\'');
		sb.append(", type=").append(type);
		sb.append(", maxRetry='").append(maxRetry).append('\'');
		sb.append(", configLongPollTimeout='").append(configLongPollTimeout).append('\'');
		sb.append(", configRetryTime='").append(configRetryTime).append('\'');
		sb.append(", enableRemoteSyncConfig=").append(enableRemoteSyncConfig);
		sb.append(", extConfig=").append(extConfig);
		sb.append(", bootstrap=").append(bootstrap);
		sb.append('}');
		return sb.toString();
	}

	/**
	 * 引导配置
	 */
	public static class Bootstrap {

		/**
		 * 启用开关
		 */
		private boolean enable;

		/**
		 * 日志启用
		 */
		private boolean logEnable;

		/**
		 * 快照启用
		 */
		private boolean snapshotEnable;

		public boolean isEnable() {
			return enable;
		}

		public void setEnable(boolean enable) {
			this.enable = enable;
		}

		public boolean isLogEnable() {
			return logEnable;
		}

		public void setLogEnable(boolean logEnable) {
			this.logEnable = logEnable;
		}

		public boolean isSnapshotEnable() {
			return snapshotEnable;
		}

		public void setSnapshotEnable(boolean snapshotEnable) {
			this.snapshotEnable = snapshotEnable;
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("Bootstrap{");
			sb.append("enable=").append(enable);
			sb.append(", snapshotEnable=").append(snapshotEnable);
			sb.append(", logEnable=").append(logEnable);
			sb.append('}');
			return sb.toString();
		}
	}

	/**
	 * 扩展配置
	 */
	public static class Config {

		/**
		 * 服务端地址
		 */
		private String serverAddr;

		/**
		 * 应用端点
		 */
		private String endpoint;

		/**
		 * 命名空间
		 */
		private String namespace;

		/**
		 * 访问键
		 */
		private String accessKey;

		/**
		 * 密钥键
		 */
		private String secretKey;

		/**
		 * 原始角色名称
		 */
		private String ramRoleName;

		/**
		 * 数据身份
		 */
		private String dataId;

		/**
		 * 数据身份列表
		 */
		private String dataIds;

		/**
		 * 分组身份
		 */
		private String group = Constants.DEFAULT_GROUP;

		/**
		 * 配置类型
		 */
		private ConfigType type;

		/**
		 * 最大重试次数
		 */
		private String maxRetry;

		/**
		 * 长轮询连接的超时时间
		 */
		private String configLongPollTimeout;

		/**
		 * 重试时间
		 */
		private String configRetryTime;

		/**
		 * 配置自动刷新启用
		 */
		private boolean autoRefresh = false;

		/**
		 * 远程同步配置启用
		 */
		private boolean enableRemoteSyncConfig = false;

		/**
		 * 用户名称
		 */
		private String username;

		/**
		 * 密码
		 */
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getServerAddr() {
			return serverAddr;
		}

		public void setServerAddr(String serverAddr) {
			this.serverAddr = serverAddr;
		}

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

		public String getNamespace() {
			return namespace;
		}

		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}

		public String getAccessKey() {
			return accessKey;
		}

		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		public String getSecretKey() {
			return secretKey;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}

		public String getRamRoleName() {
			return ramRoleName;
		}

		public void setRamRoleName(String ramRoleName) {
			this.ramRoleName = ramRoleName;
		}

		public String getDataId() {
			return dataId;
		}

		public void setDataId(String dataId) {
			this.dataId = dataId;
		}

		public String getDataIds() {
			return dataIds;
		}

		public void setDataIds(String dataIds) {
			this.dataIds = dataIds;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public ConfigType getType() {
			return type;
		}

		public void setType(ConfigType type) {
			this.type = type;
		}

		public String getMaxRetry() {
			return maxRetry;
		}

		public void setMaxRetry(String maxRetry) {
			this.maxRetry = maxRetry;
		}

		public String getConfigLongPollTimeout() {
			return configLongPollTimeout;
		}

		public void setConfigLongPollTimeout(String configLongPollTimeout) {
			this.configLongPollTimeout = configLongPollTimeout;
		}

		public String getConfigRetryTime() {
			return configRetryTime;
		}

		public void setConfigRetryTime(String configRetryTime) {
			this.configRetryTime = configRetryTime;
		}

		public boolean isAutoRefresh() {
			return autoRefresh;
		}

		public void setAutoRefresh(boolean autoRefresh) {
			this.autoRefresh = autoRefresh;
		}

		public boolean isEnableRemoteSyncConfig() {
			return enableRemoteSyncConfig;
		}

		public void setEnableRemoteSyncConfig(boolean enableRemoteSyncConfig) {
			this.enableRemoteSyncConfig = enableRemoteSyncConfig;
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("Config{");
			sb.append("serverAddr='").append(serverAddr).append('\'');
			sb.append(", endpoint='").append(endpoint).append('\'');
			sb.append(", namespace='").append(namespace).append('\'');
			sb.append(", accessKey='").append(Objects.isNull(accessKey) ? null : "******").append('\'');
			sb.append(", secretKey='").append(Objects.isNull(secretKey) ? null : "******").append('\'');
			sb.append(", ramRoleName='").append(ramRoleName).append('\'');
			sb.append(", dataId='").append(dataId).append('\'');
			sb.append(", dataIds='").append(dataIds).append('\'');
			sb.append(", group='").append(group).append('\'');
			sb.append(", type=").append(type);
			sb.append(", maxRetry='").append(maxRetry).append('\'');
			sb.append(", configLongPollTimeout='").append(configLongPollTimeout)
					.append('\'');
			sb.append(", configRetryTime='").append(configRetryTime).append('\'');
			sb.append(", autoRefresh=").append(autoRefresh);
			sb.append(", enableRemoteSyncConfig=").append(enableRemoteSyncConfig);
			sb.append('}');
			return sb.toString();
		}
	}
}
