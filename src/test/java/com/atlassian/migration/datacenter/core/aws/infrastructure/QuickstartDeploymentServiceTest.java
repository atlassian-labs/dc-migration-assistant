package com.atlassian.migration.datacenter.core.aws.infrastructure;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.migration.datacenter.core.aws.CfnApi;
import com.atlassian.migration.datacenter.dto.MigrationContext;
import com.atlassian.migration.datacenter.spi.MigrationServiceV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;

import java.util.HashMap;

import static com.atlassian.migration.datacenter.spi.infrastructure.ApplicationDeploymentService.ApplicationDeploymentStatus.CREATE_IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuickstartDeploymentServiceTest {

    static final String STACK_NAME = "my-stack";
    static final HashMap<String, String> STACK_PARAMS = new HashMap<String, String>() {{
        put("parameter", "value");
    }};

    @Mock
    CfnApi mockCfnApi;

    @Mock
    MigrationServiceV2 mockMigrationService;

    @Mock
    ActiveObjects mockAo;

    @InjectMocks
    QuickstartDeploymentService deploymentService;

    @Mock
    MigrationContext mockContext;

    @BeforeEach
    void setUp() {
        when(mockAo.find(MigrationContext.class)).thenReturn(new MigrationContext[]{mockContext});
    }

    @Test
    void shouldDeployQuickStart() {
        deploySimpleStack();

        verify(mockCfnApi).provisionStack("https://aws-quickstart.s3.amazonaws.com/quickstart-atlassian-jira/templates/quickstart-jira-dc-with-vpc.template.yaml", STACK_NAME, STACK_PARAMS);
    }

    @Test
    void shouldReturnInProgressWhileDeploying() {
        when(mockContext.getApplicationDeploymentId()).thenReturn(STACK_NAME);
        when(mockCfnApi.getStatus(STACK_NAME)).thenReturn(StackStatus.CREATE_IN_PROGRESS);

        deploySimpleStack();

        assertEquals(CREATE_IN_PROGRESS, deploymentService.getDeploymentStatus());
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