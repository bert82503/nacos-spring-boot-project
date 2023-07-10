/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.boot.nacos.config.autoconfigure;

import java.util.Properties;
import java.util.function.Function;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.boot.nacos.config.util.NacosConfigLoader;
import com.alibaba.boot.nacos.config.util.NacosConfigLoaderFactory;
import com.alibaba.boot.nacos.config.util.NacosConfigPropertiesUtils;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.utils.SnapShotSwitch;
import com.alibaba.nacos.spring.factory.CacheableEventPublishingNacosServiceFactory;
import com.alibaba.nacos.spring.util.NacosBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.2
 */
public class NacosConfigApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final Logger logger = LoggerFactory
			.getLogger(NacosConfigApplicationContextInitializer.class);
	/**
	 * 配置环境处理器
	 */
	private final NacosConfigEnvironmentProcessor processor;
	/**
	 * 可缓存的事件发布的服务工厂
	 */
	private final CacheableEventPublishingNacosServiceFactory singleton = CacheableEventPublishingNacosServiceFactory
			.getSingleton();
	private final Function<Properties, ConfigService> builder = properties -> {
		try {
			// 创建配置服务
			return singleton.createConfigService(properties);
		}
		catch (NacosException e) {
			throw new NacosBootConfigException(
					"ConfigService can't be created with properties : " + properties, e);
		}
	};
	/**
	 * 可配置的环境组件
	 */
	private ConfigurableEnvironment environment;
	/**
	 * 配置属性集
	 */
	private NacosConfigProperties nacosConfigProperties;

	public NacosConfigApplicationContextInitializer(
			NacosConfigEnvironmentProcessor configEnvironmentProcessor) {
		this.processor = configEnvironmentProcessor;
	}

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		// 应用上下文、环境组件、配置属性集
		singleton.setApplicationContext(context);
		environment = context.getEnvironment();
		// 构建配置属性集
		nacosConfigProperties = NacosConfigPropertiesUtils
				.buildNacosConfigProperties(environment);
		// 配置加载器
		final NacosConfigLoader configLoader = NacosConfigLoaderFactory.getSingleton(
				nacosConfigProperties, environment, builder);

        if (!processor.snapshotEnable()) {
            SnapShotSwitch.setIsSnapShot(false);
        }

		if (!enable()) {
			logger.info("[Nacos Config Boot] : The preload configuration is not enabled");
		}
		else {

			// If it opens the log level loading directly will cache
			// DeferNacosPropertySource release

			if (processor.enable()) {
				// 发布延迟服务
				processor.publishDeferService(context);
				// 增加自动刷新的监视器
				configLoader.addListenerIfAutoRefreshed(
						processor.getDeferPropertySources());
			}
			else {
				// 加载配置
				configLoader.loadConfig();
				// 增加自动刷新的监视器
				configLoader.addListenerIfAutoRefreshed();
			}
		}

		final ConfigurableListableBeanFactory factory = context.getBeanFactory();
		if (!factory.containsSingleton(NacosBeanUtils.GLOBAL_NACOS_PROPERTIES_BEAN_NAME)) {
			// 注册从配置加载器加载的全局属性集
			factory.registerSingleton(NacosBeanUtils.GLOBAL_NACOS_PROPERTIES_BEAN_NAME,
					configLoader.getGlobalProperties());
		}
	}

	private boolean enable() {
		return processor.enable() || nacosConfigProperties.getBootstrap().isEnable();
	}

}
