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

package com.atlassian.migration.datacenter.core.aws.region;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesResponse;

import java.util.List;

public class AvailabilityZoneManager implements AvailabilityZoneService {

    private final AwsCredentialsProvider credentialsProvider;
    private final GlobalInfrastructure globalInfrastructure;

    public AvailabilityZoneManager(AwsCredentialsProvider credentialsProvider, GlobalInfrastructure globalInfrastructure) {
        this.credentialsProvider = credentialsProvider;
        this.globalInfrastructure = globalInfrastructure;
    }

    /**
     * @param region
     * @return a list of {@link software.amazon.awssdk.services.ec2.model.AvailabilityZone AvailabilityZone} for a region
     * @throws InvalidAWSRegionException
     */
    @Override
    public List<AvailabilityZone> getAZForRegion(Region region) throws InvalidAWSRegionException {
        if (this.isValidRegion(region.toString())) {
            Ec2Client ec2 = getEC2Client(region);
            DescribeAvailabilityZonesResponse zonesResponse = ec2.describeAvailabilityZones();
            return zonesResponse.availabilityZones();
        } else {
            throw new InvalidAWSRegionException();
        }

    }

    private Ec2Client getEC2Client(Region region) {
        return Ec2Client.builder().region(region).credentialsProvider(this.credentialsProvider).build();
    }

    private boolean isValidRegion(String testRegion) {
        return this.globalInfrastructure
                .getRegions()
                .stream()
                .anyMatch(region -> region.equals(testRegion));
    }

}
