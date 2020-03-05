package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.spi.MigrationServiceV2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuickstartDeploymentServiceTest {

    static final String STACK_NAME = "my-stack";
    static final HashMap<String, String> STACK_PARAMS = new HashMap<String, String>(){{
        put("parameter", "value");
    }};

    @Mock
    CfnApi mockCfnApi;

    @Mock
    MigrationServiceV2 mockMigrationService;

    @InjectMocks
    QuickstartDeploymentService deploymentService;

    @Test
    void shouldDeployQuickStart() {
        deploySimpleStack();

        verify(mockCfnApi).provisionStack("https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml", STACK_NAME, STACK_PARAMS);
    }

    @Test
    void shouldTransitionMigrationServiceStateWhenDeploymentFinishes() throws InterruptedException {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_COMPLETE);

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).nextStage();
    }

    @Test
    void shouldTransitionMigrationServiceToErrorWhenDeploymentFails() throws InterruptedException {
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_FAILED);

        deploySimpleStack();

        Thread.sleep(100);

        verify(mockMigrationService).error();
    }

    private void deploySimpleStack() {
        deploymentService.deployApplication(STACK_NAME, STACK_PARAMS);
    }

}