package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.mockito.Mockito.verify;

class QuickstartDeploymentServiceTest {

    QuickstartDeploymentService deploymentService;

    @Mock
    CfnApi mockCfnApi;

    @BeforeEach
    void setUp() {
        deploymentService = new QuickstartDeploymentService();
    }

    @Test
    void shouldDeployQuickStart() {
        deploymentService.deployApplication("my-stack", new HashMap<>());

        verify(mockCfnApi).provisionStack("https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yml", "my-stack", new HashMap<>());
    }

}