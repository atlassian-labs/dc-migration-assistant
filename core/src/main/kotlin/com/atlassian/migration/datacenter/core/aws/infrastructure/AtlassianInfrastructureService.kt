package com.atlassian.migration.datacenter.core.aws.infrastructure

import com.atlassian.migration.datacenter.core.aws.CfnApi
import software.amazon.awssdk.services.cloudformation.model.Stack

/**
 * Encapsulates some higher-level operations relating to Atlassian-defined VPC stacks (ASIs).
 */
class AtlassianInfrastructureService(val cfnApi: CfnApi) {

    fun findASIs() : List<Stack> {
        val stacks = cfnApi.listStacksFull();
        // Rough heuristic to find candidates for ASIs
        val matching = stacks
                .filter {
                    it.outputs()
                            .map { it.outputKey() }
                            .containsAll(setOf(
                                    "VPCID",
                                    "PrivateSubnets",
                                    "PublicSubnets"))
                }
        return matching
    }
}
