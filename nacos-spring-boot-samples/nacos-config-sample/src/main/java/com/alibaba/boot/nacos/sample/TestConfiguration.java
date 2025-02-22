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
package com.alibaba.boot.nacos.sample;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.annotation.NacosValue;

import org.springframework.context.annotation.Configuration;

/**
 * 配置属性
 *
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @see Configuration
 */
@Configuration
public class TestConfiguration {

	/**
	 * 配置属性值
	 */
	@NacosValue(value = "${people.count:0}", autoRefreshed = true)
	private String count;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * 配置监视器
	 *
	 * @see NacosConfigListener
	 */
	@NacosConfigListener(dataId = "listener.test", timeout = 500)
	public void onChange(String newContent) throws Exception {
		System.out.println("onChange : " + newContent);
	}
}
