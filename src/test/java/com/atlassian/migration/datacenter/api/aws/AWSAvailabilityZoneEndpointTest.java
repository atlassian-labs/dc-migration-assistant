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

import com.atlassian.migration.datacenter.core.aws.region.AvailabilityZoneService;
import com.atlassian.migration.datacenter.core.aws.region.InvalidAWSRegionException;
import com.atlassian.migration.datacenter.core.aws.region.RegionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AWSAvailabilityZoneEndpointTest {

    @Mock
    AvailabilityZoneService availabilityZoneService;

    @Mock
    RegionService regionService;

    @InjectMocks
    AWSAvailabilityZoneEndpoint availabilityZoneEndpoint;

    private static List<AvailabilityZone> buildAZList() {
        List<AvailabilityZone> azList = new ArrayList<>();
        azList.add(AvailabilityZone.builder().zoneName("eu-central-1a").build());
        azList.add(AvailabilityZone.builder().zoneName("eu-central-1b").build());
        azList.add(AvailabilityZone.builder().zoneName("eu-central-1c").build());
        return azList;
    }

    @Test
    public void testAvailabilityZoneEndpointWithRegion() throws InvalidAWSRegionException {
        Region mockRegion = Region.of("eu-central-1");
        when(this.regionService.getRegion()).thenReturn("eu-central-1");
        when(this.availabilityZoneService.getAZForRegion(mockRegion)).thenReturn(buildAZList());

        Response response = this.availabilityZoneEndpoint.getAvailabilityZoneList(mockRegion.toString());
        List<String> responseList = (List<String>) response.getEntity();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(3, responseList.size());
    }

    @Test
    public void testAvailabilityZoneEndpointWithInvalidRegion() throws InvalidAWSRegionException {
        Region mockRegion = Region.of("eu-central-2");
        when(this.regionService.getRegion()).thenReturn("eu-central-2");
        when(this.availabilityZoneService.getAZForRegion(mockRegion)).thenThrow(new InvalidAWSRegionException());

        Response response = this.availabilityZoneEndpoint.getAvailabilityZoneList(mockRegion.toString());
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAvailabilityZoneEndpoint() throws InvalidAWSRegionException {
        Region mockRegion = Region.of("eu-central-1");
        when(this.regionService.getRegion()).thenReturn("eu-central-1");
        when(this.availabilityZoneService.getAZForRegion(mockRegion)).thenReturn(buildAZList());

        Response response = this.availabilityZoneEndpoint.getAvailabilityZoneList();
        List<String> responseList = (List<String>) response.getEntity();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(3, responseList.size());
    }

    @Test
    public void testAvailabilityZoneEndpointNotFound() throws InvalidAWSRegionException {
        Region mockRegion = Region.of("eu-central-2");
        when(this.regionService.getRegion()).thenReturn("eu-central-2");
        when(this.availabilityZoneService.getAZForRegion(mockRegion)).thenThrow(new InvalidAWSRegionException());

        Response response = this.availabilityZoneEndpoint.getAvailabilityZoneList();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}
