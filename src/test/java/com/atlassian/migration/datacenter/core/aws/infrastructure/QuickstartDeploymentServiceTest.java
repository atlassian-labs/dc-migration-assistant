package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuickstartDeploymentServiceTest {

    @Mock
    CfnApi mockCfnApi;

    @InjectMocks
    QuickstartDeploymentService deploymentService;

    @Test
    void shouldDeployQuickStart() {
        final String stackName = "my-stack";
        final HashMap<String, String> stackParams = new HashMap<>();
        stackParams.put("parameter", "value");

        deploymentService.deployApplication(stackName, stackParams);

        verify(mockCfnApi).provisionStack("https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml", stackName, stackParams);
    }

}