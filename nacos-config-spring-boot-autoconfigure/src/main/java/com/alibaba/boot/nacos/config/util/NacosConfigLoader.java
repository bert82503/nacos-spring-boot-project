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
package com.alibaba.boot.nacos.config.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.function.Function;

import com.alibaba.boot.nacos.config.properties.NacosConfigProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import com.alibaba.nacos.spring.core.env.NacosPropertySourcePostProcessor;
import com.alibaba.nacos.spring.util.NacosUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

import static com.alibaba.nacos.spring.util.NacosUtils.buildDefaultPropertySourceName;

/**
 * 配置加载器
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since 0.2.3
 */
public class NacosConfigLoader {

    private final Logger logger = LoggerFactory.getLogger(NacosConfigLoader.class);

    /**
     * 配置属性集
     */
    private final NacosConfigProperties nacosConfigProperties;

    /**
     * 全局配置集
     */
    private final Properties globalProperties;

    /**
     * 可配置的环境组件
     */
    private final ConfigurableEnvironment environment;
    /**
     * 配置服务构建者
     */
    private final Function<Properties, ConfigService> builder;
    /**
     * 延迟服务的配置属性源列表
     */
    private final List<DeferNacosPropertySource> nacosPropertySources = new LinkedList<>();

    public NacosConfigLoader(
            NacosConfigProperties nacosConfigProperties,
            ConfigurableEnvironment environment,
            Function<Properties, ConfigService> builder) {
        this.nacosConfigProperties = nacosConfigProperties;
        this.environment = environment;
        this.builder = builder;
        globalProperties = this.buildGlobalNacosProperties();
    }

    // 加载配置

    /**
     * 加载配置
     */
    public void loadConfig() {
        // 应用的可变的配置属性源列表
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        // 全局的配置属性源列表
        List<NacosPropertySource> sources = reqGlobalNacosConfig(globalProperties,
                nacosConfigProperties.getType());
        for (NacosConfigProperties.Config config : nacosConfigProperties.getExtConfig()) {
            // 请求子的配置属性源列表
            List<NacosPropertySource> elements = reqSubNacosConfig(config,
                    globalProperties, config.getType());
            sources.addAll(elements);
        }
        if (nacosConfigProperties.isRemoteFirst()) {
            // 远程配置优先
            for (ListIterator<NacosPropertySource> itr = sources.listIterator(sources.size()); itr.hasPrevious(); ) {
                mutablePropertySources.addAfter(
                        StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, itr.previous());
            }
        } else {
            for (NacosPropertySource propertySource : sources) {
                mutablePropertySources.addLast(propertySource);
            }
        }
    }

    // 配置属性集

    /**
     * 构建全局的配置属性集
     */
    public Properties buildGlobalNacosProperties() {
        // 构建配置属性集
        return NacosPropertiesBuilder.buildNacosProperties(environment,
                nacosConfigProperties.getServerAddr(),
                nacosConfigProperties.getNamespace(), nacosConfigProperties.getEndpoint(),
                nacosConfigProperties.getSecretKey(),
                nacosConfigProperties.getAccessKey(),
                nacosConfigProperties.getRamRoleName(),
                nacosConfigProperties.getConfigLongPollTimeout(),
                nacosConfigProperties.getConfigRetryTime(),
                nacosConfigProperties.getMaxRetry(),
                nacosConfigProperties.getContextPath(),
                nacosConfigProperties.isEnableRemoteSyncConfig(),
                nacosConfigProperties.getUsername(), nacosConfigProperties.getPassword());
    }

    /**
     * 构建子的配置属性集
     */
    private Properties buildSubNacosProperties(
            Properties globalProperties,
            NacosConfigProperties.Config config) {
        // 构建配置属性集
        Properties sub = NacosPropertiesBuilder.buildNacosProperties(environment,
                config.getServerAddr(), config.getNamespace(), config.getEndpoint(),
                config.getSecretKey(), config.getAccessKey(), config.getRamRoleName(),
                config.getConfigLongPollTimeout(), config.getConfigRetryTime(),
                config.getMaxRetry(), null, config.isEnableRemoteSyncConfig(),
                config.getUsername(), config.getPassword());
        // 合并配置属性集
        NacosPropertiesBuilder.merge(sub, globalProperties);
        return sub;
    }

    // 配置

    private List<NacosPropertySource> reqGlobalNacosConfig(
            Properties globalProperties,
            ConfigType type) {
        // 数据身份列表
        List<String> dataIds = new ArrayList<>();
        // Loads all data-id information into the list in the list
        if (!StringUtils.hasLength(nacosConfigProperties.getDataId())) {
            // 解析数据身份列表
            final String ids = environment
                    .resolvePlaceholders(nacosConfigProperties.getDataIds());
            if (StringUtils.hasText(ids)) {
                dataIds.addAll(Arrays.asList(ids.split(",")));
            }
        } else {
            // 数据身份
            dataIds.add(nacosConfigProperties.getDataId());
        }
        // 分组名称
        final String groupName = environment
                .resolvePlaceholders(nacosConfigProperties.getGroup());
        // 配置自动刷新
        final boolean isAutoRefresh = nacosConfigProperties.isAutoRefresh();
        return new ArrayList<>(Arrays.asList(this.reqNacosConfig(globalProperties,
                dataIds.toArray(new String[0]), groupName, type, isAutoRefresh)));
    }

    private List<NacosPropertySource> reqSubNacosConfig(
            NacosConfigProperties.Config config, Properties globalProperties,
            ConfigType type) {
        Properties subConfigProperties = buildSubNacosProperties(globalProperties,
                config);
        ArrayList<String> dataIds = new ArrayList<>();
        if (!StringUtils.hasLength(config.getDataId())) {
            final String ids = environment.resolvePlaceholders(config.getDataIds());
            dataIds.addAll(Arrays.asList(ids.split(",")));
        } else {
            dataIds.add(config.getDataId());
        }
        // 分组名称
        final String groupName = environment.resolvePlaceholders(config.getGroup());
        final boolean isAutoRefresh = config.isAutoRefresh();
        return new ArrayList<>(Arrays.asList(this.reqNacosConfig(subConfigProperties,
                dataIds.toArray(new String[0]), groupName, type, isAutoRefresh)));
    }

    /**
     * 请求配置
     *
     * @param configProperties 配置属性集
     * @param dataIds          数据身份列表
     * @param groupId          分组身份
     * @param type             配置类型
     * @param isAutoRefresh    配置自动刷新
     */
    private NacosPropertySource[] reqNacosConfig(
            Properties configProperties,
            String[] dataIds, String groupId, ConfigType type, boolean isAutoRefresh) {
        // 配置属性源列表
        final NacosPropertySource[] propertySources = new NacosPropertySource[dataIds.length];
        for (int i = 0; i < dataIds.length; i++) {
            if (!StringUtils.hasLength(dataIds[i])) {
                continue;
            }
            // Remove excess Spaces
            // 数据身份
            final String dataId = environment.resolvePlaceholders(dataIds[i].trim());
            // 通过数据身份和分组身份获取配置内容
            final String config = NacosUtils.getContent(builder.apply(configProperties),
                    dataId, groupId);
            // 配置属性源
            final NacosPropertySource nacosPropertySource = new NacosPropertySource(
                    dataId, groupId,
                    NacosUtils.buildDefaultPropertySourceName(dataId, groupId, configProperties),
                    config, type.getType());
            // 数据身份
            nacosPropertySource.setDataId(dataId);
            // 分组身份
            nacosPropertySource.setGroupId(groupId);
            nacosPropertySource.setType(type.getType());
            nacosPropertySource.setAutoRefreshed(isAutoRefresh);
            logger.info("load config from nacos, data-id is : {}, group is : {}",
                    nacosPropertySource.getDataId(), nacosPropertySource.getGroupId());
            propertySources[i] = nacosPropertySource;
            // 延迟服务的配置属性源
            DeferNacosPropertySource defer = new DeferNacosPropertySource(
                    nacosPropertySource, configProperties, environment);
            nacosPropertySources.add(defer);
        }
        return propertySources;
    }

    // 增加自动刷新的监视器

    public void addListenerIfAutoRefreshed() {
        addListenerIfAutoRefreshed(nacosPropertySources);
    }

    public void addListenerIfAutoRefreshed(
            final List<DeferNacosPropertySource> deferNacosPropertySources) {
        for (DeferNacosPropertySource deferNacosPropertySource : deferNacosPropertySources) {
            // 增加自动刷新的监视器
            NacosPropertySourcePostProcessor.addListenerIfAutoRefreshed(
                    deferNacosPropertySource.getNacosPropertySource(),
                    deferNacosPropertySource.getProperties(),
                    deferNacosPropertySource.getEnvironment());
        }
    }

    // 配置属性源列表

    public List<DeferNacosPropertySource> getNacosPropertySources() {
        return nacosPropertySources;
    }


    public Properties getGlobalProperties() {
        return globalProperties;
    }

    /**
     * 延迟Nacos配置数据源对象，用于日志级别的加载时间、缓存配置，等待Spring Context完成后创建发布
     */
    // Delay Nacos configuration data source object, used for log level of loading time,
    // the cache configuration, wait for after the completion of the Spring Context
    // created in the release
    public static class DeferNacosPropertySource {
        /**
         * 配置属性源
         */
        private final NacosPropertySource nacosPropertySource;
        /**
         * 可配置的环境组件
         */
        private final ConfigurableEnvironment environment;
        /**
         * 配置属性集
         */
        private final Properties properties;

        DeferNacosPropertySource(NacosPropertySource nacosPropertySource,
                                 Properties properties, ConfigurableEnvironment environment) {
            this.nacosPropertySource = nacosPropertySource;
            this.properties = properties;
            this.environment = environment;
        }

        NacosPropertySource getNacosPropertySource() {
            return nacosPropertySource;
        }

        ConfigurableEnvironment getEnvironment() {
            return environment;
        }

        public Properties getProperties() {
            return properties;
        }
    }
}
