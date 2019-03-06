/*
 * Copyright © 2015-2018 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.spring.boot.starter.configuration.impl;

import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.CamundaProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import java.util.Optional;
import java.util.UUID;

public class DefaultProcessEngineConfiguration extends AbstractCamundaConfiguration implements CamundaProcessEngineConfiguration {

  @Autowired
  private Optional<IdGenerator> idGenerator;

  private String contextPath;

  @Override
  public void preInit(SpringProcessEngineConfiguration configuration) {
    setProcessEngineName(configuration);
    setDefaultSerializationFormat(configuration);
    setIdGenerator(configuration);
    setJobExecutorAcquireByPriority(configuration);
    setDefaultNumberOfRetries(configuration);
    configuration.setDefaultContextPath(contextPath);
  }

  private void setIdGenerator(SpringProcessEngineConfiguration configuration) {
    idGenerator.ifPresent(configuration::setIdGenerator);
  }

  private void setDefaultSerializationFormat(SpringProcessEngineConfiguration configuration) {
    String defaultSerializationFormat = camundaBpmProperties.getDefaultSerializationFormat();
    if (StringUtils.hasText(defaultSerializationFormat)) {
      configuration.setDefaultSerializationFormat(defaultSerializationFormat);
    } else {
      logger.warn("Ignoring invalid defaultSerializationFormat='{}'", defaultSerializationFormat);
    }
  }

  private void setProcessEngineName(SpringProcessEngineConfiguration configuration) {
    String processEngineName = StringUtils.trimAllWhitespace(camundaBpmProperties.getProcessEngineName());
    if (camundaBpmProperties.getGenerateUniqueEngineName()) {
      processEngineName = UUID.randomUUID().toString();
    }
    if (!StringUtils.isEmpty(processEngineName) && !processEngineName.contains("-")) {
      configuration.setProcessEngineName(processEngineName);
    } else {
      logger.warn("Ignoring invalid processEngineName='{}' - must not be null, blank or contain hyphen", camundaBpmProperties.getProcessEngineName());
    }
  }

  private void setJobExecutorAcquireByPriority(SpringProcessEngineConfiguration configuration) {
    Optional.ofNullable(camundaBpmProperties.getJobExecutorAcquireByPriority())
      .ifPresent(configuration::setJobExecutorAcquireByPriority);
  }

  private void setDefaultNumberOfRetries(SpringProcessEngineConfiguration configuration) {
    Optional.ofNullable(camundaBpmProperties.getDefaultNumberOfRetries())
      .ifPresent(configuration::setDefaultNumberOfRetries);
  }

  @ConditionalOnWebApplication
  @Configuration
  class ContextPathConfiguration implements ServletContextAware {

    @Override
    public void setServletContext(ServletContext servletContext) {
      if (!StringUtils.isEmpty(servletContext.getContextPath())) {
        contextPath = servletContext.getContextPath();
      }
    }
  }
}
