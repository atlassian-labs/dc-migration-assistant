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

package com.atlassian.migration.datacenter.core.aws.infrastructure

import com.atlassian.migration.datacenter.core.aws.CfnApi
import software.amazon.awssdk.services.cloudformation.model.Stack

/**
 * Encapsulates some higher-level operations relating to Atlassian-defined VPC stacks (ASIs).
 */
class AtlassianInfrastructureService(private val cfnApi: CfnApi) {

    fun findASIs() : List<Stack> {
        val stacks = cfnApi.listStacksFull();
        // Rough heuristic to find candidates for ASIs
        val matching = stacks
                .filter {
                    val pkeys = it.parameters().map { it.parameterKey() }
                    val okeys = it.outputs().map { it.outputKey() }

                    okeys.containsAll(setOf(
                            "VPCID",
                            "PrivateSubnets",
                            "PublicSubnets"))
                            &&
                            pkeys.contains("ExportPrefix")
                }
        return matching
    }
}
