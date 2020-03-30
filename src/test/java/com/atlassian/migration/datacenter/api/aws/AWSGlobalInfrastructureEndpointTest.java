/*
 * Copyright 2020 Atlassian
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

package com.atlassian.migration.datacenter.api.aws;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AWSGlobalInfrastructureEndpointTest {

    @Mock
    private GlobalInfrastructure mockGlobalInfrastructure;

    @InjectMocks
    private AWSGlobalInfrastructureEndpoint sut;


    @Test
    public void itShouldReturnServerErrorWhenGlobalInfrastructureModuleFails() {
        when(mockGlobalInfrastructure.getRegions()).thenReturn(null);

        Response res = sut.getRegions();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), res.getStatus());
    }

    @Test
    void itShouldReturnAllRegions() {
        final String regionOne = "atlassian-east-1";
        final String regionTwo = "atlassian-west-1";

        when(mockGlobalInfrastructure.getRegions()).thenReturn(Arrays.asList(regionOne, regionTwo));

        Response res = sut.getRegions();

        assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());

        assertEquals(Arrays.asList(regionOne, regionTwo), res.getEntity());
    }
}
