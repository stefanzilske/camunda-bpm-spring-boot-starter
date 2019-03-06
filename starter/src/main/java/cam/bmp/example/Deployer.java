/*
 * Copyright © 2013-2019 camunda services GmbH and various authors (info@camunda.com)
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
package cam.bmp.example;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.spring.boot.starter.configuration.CamundaDeploymentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipInputStream;

/**
 * @author Nikola Koevski
 */
@Component
public class Deployer {

  @Autowired
  ProcessEngine processEngine;

  @Autowired
  CamundaDeploymentConfiguration configuration;

  protected List<String> deploymentIds = new ArrayList<>();

  @Autowired
  RepositoryService repositoryService;

  public Deployer() {
  }

  @PreDestroy
  public void deleteOnUndeploy() {
    for (String id : deploymentIds) {
      if (configuration.isDeleteUponUndeploy()) {
        repositoryService.deleteDeployment(id);
      }
    }
  }

  @PostConstruct
  public void testMethod() {
    List<DeploymentProperty> deployments = configuration.getDeployments();
    deployments.add(configuration.getDeployment());

    for (DeploymentProperty deploymentProperty : deployments) {
      deploymentIds.add(autoDeployResources(deploymentProperty, processEngine));
    }
  }

  protected String autoDeployResources(DeploymentProperty deploymentProperty, ProcessEngine processEngine) {
    Set<Resource> resourceSet = configuration.getDeploymentResources(deploymentProperty.getResourcePattern());
    if (resourceSet!=null && resourceSet.size() > 0) {
      RepositoryService repositoryService = processEngine.getRepositoryService();

      DeploymentBuilder deploymentBuilder = repositoryService
        .createDeployment()
        .enableDuplicateFiltering(configuration.isDeployChangedOnly())
        .name(deploymentProperty.getName())
        .tenantId(deploymentProperty.getTenantId());

      if (configuration.isScanForProcessDefinitions()) {
        for (Resource resource : resourceSet) {
          String resourceName = null;

          if (resource instanceof ContextResource) {
            resourceName = ((ContextResource) resource).getPathWithinContext();

          } else if (resource instanceof ByteArrayResource) {
            resourceName = resource.getDescription();

          } else {
            resourceName = getFileResourceName(resource);
          }

          try {
            if (resourceName.endsWith(".bar") || resourceName.endsWith(".zip") || resourceName.endsWith(".jar")) {
              deploymentBuilder.addZipInputStream(new ZipInputStream(resource.getInputStream()));
            } else {
              deploymentBuilder.addInputStream(resourceName, resource.getInputStream());
            }
          } catch (IOException e) {
            throw new ProcessEngineException("couldn't auto deploy resource '" + resource + "': " + e.getMessage(), e);
          }
        }
      }

      return  deploymentBuilder.deploy().getId();
    }

    return null;
  }

  protected String getFileResourceName(Resource resource) {
    try {
      return resource.getFile().getAbsolutePath();
    } catch (IOException e) {
      return resource.getFilename();
    }
  }
}
