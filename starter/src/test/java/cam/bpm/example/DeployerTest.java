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
package cam.bpm.example;

import cam.bmp.example.DeploymentProperty;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.spring.boot.starter.property.CamundaBpmProperties;
import org.camunda.bpm.spring.boot.starter.test.pa.TestProcessApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Nikola Koevski
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestProcessApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations="classpath:application-deployer.properties")
public class DeployerTest {

  @Autowired
  RepositoryService repositoryService;

  @Autowired
  CamundaBpmProperties properties;

  @Test
  public void testDeploymentCount() {
    long count = repositoryService.createDeploymentQuery().count();
    assertThat(count).isEqualTo(3);
  }

  @Test
  public void testProperties() {
    List<DeploymentProperty> deployments = properties.getDeployments();
    assertThat(deployments.size()).isEqualTo(3);
    assertThat(deployments.get(0).getTenantId()).isEqualTo("foo");
    assertThat(deployments.get(1).getTenantId()).isEqualTo("bar");
  }

  @Test
  public void testTenantDeployments() {
    long countFoo = repositoryService.createDeploymentQuery().tenantIdIn("foo").count();
    assertThat(countFoo).isEqualTo(1l);

    long countDracula = repositoryService.createDeploymentQuery().tenantIdIn("bar").count();
    assertThat(countDracula).isEqualTo(1l);
  }

  @Test
  public void testSingleDeployment() {
    List<Deployment> deployments = repositoryService.createDeploymentQuery().withoutTenantId().list();
    assertThat(deployments.size()).isEqualTo(1l);
    Deployment countDracula = deployments.get(0);
    assertThat(countDracula.getName()).isEqualTo("test");
    assertThat(countDracula.getTenantId()).isNull();
  }

  @Test
  public void testDeploymentName() {
    Deployment deployment1 = repositoryService.createDeploymentQuery().tenantIdIn("foo").singleResult();
    assertThat(deployment1.getName()).isEqualTo("deployment1");
    Deployment deployment2 = repositoryService.createDeploymentQuery().tenantIdIn("bar").singleResult();
    assertThat(deployment2.getName()).isEqualTo("deployment2");
  }


}
