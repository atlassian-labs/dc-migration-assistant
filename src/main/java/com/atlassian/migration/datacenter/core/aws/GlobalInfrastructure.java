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

package com.atlassian.migration.datacenter.core.aws;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.RegionMetadata;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalInfrastructure {

    public List<String> getRegions() {
        return Region.regions()
                .stream()
                .filter(region -> {
                    RegionMetadata regionMetadata = RegionMetadata.of(region);
                    if (regionMetadata == null) {
                        return false;
                    }
                    PartitionMetadata partitionMetadata = regionMetadata.partition();
                    if (partitionMetadata == null) {
                        return false;
                    }
                    return partitionMetadata.id().equals("aws");
                })
                .map(Region::toString)
                .collect(Collectors.toList());
    }
}
