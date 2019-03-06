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

import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.camunda.bpm.spring.boot.starter.property.ManagementProperties;

/**
 * @author Nikola Koevski
 */
public class DeploymentProperty {

  private String name = "SpringAutoDeployment";
  private String tenantId = null;
  private String[] resourcePattern = CamundaBpmProperties.initDeploymentResourcePattern();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getResourcePattern() {
    return resourcePattern;
  }

  public void setResourcePattern(String[] resourcePattern) {
    this.resourcePattern = resourcePattern;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  @Override
  public String toString() {
    return "DeploymentProperty{" + "resourcePattern='" + resourcePattern + '\'' + ", tenantId='" + tenantId + '\'' + '}';
  }
}
