package com.atlassian.migration.datacenter.core.aws.region;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.migration.datacenter.core.aws.auth.AtlassianPluginAWSCredentialsProvider;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesResponse;

import java.util.List;

@Component
public class AvailabilityZoneManager implements AvailabilityZoneService {

    private final AtlassianPluginAWSCredentialsProvider credentialsProvider;
    private final GlobalInfrastructure globalInfrastructure;

    public AvailabilityZoneManager(AtlassianPluginAWSCredentialsProvider credentialsProvider, GlobalInfrastructure globalInfrastructure) {
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
