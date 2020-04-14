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

package com.atlassian.migration.api

import com.atlassian.migration.test.BaseRestTest
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test
import javax.ws.rs.core.Response

class MigrationEndpointRestTest : BaseRestTest() {

    @Test
    fun `Migration endpoint should return 404 if not initialised`() {
        Given {
            spec(requestSpec)
        } When {
            get("/migration")
        } Then {
            statusCode(Response.Status.NOT_FOUND.statusCode)
        }
    }
}