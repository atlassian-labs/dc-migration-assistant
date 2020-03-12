package com.atlassian.migration.datacenter.core.aws.region;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;

import java.util.List;

public interface AvailabilityZoneService {

    List<AvailabilityZone> getAZForRegion(Region region) throws InvalidAWSRegionException;

}
