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
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.ws.rs.core.Response

class MigrationEndpointRestTest : BaseRestTest() {

    @BeforeEach
    fun `Enable response logging`(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        Given {
            spec(requestSpec)
        } When {
            delete("migration/reset")
        } Then {
            statusCode(
                    equalTo(Response.Status.OK.statusCode)
            )
        }
    }

    @Test
    fun `Migration endpoint should return 404 if not initialised or 200 when it was created`() {
        Given {
            spec(requestSpec)
        } When {
            get("/migration")
        } Then {
            statusCode(
                equalTo(Response.Status.OK.statusCode)
            )
        }
    }

    @Test
    fun `Migration can be started`() {
        Given {
            spec(requestSpec).contentType(ContentType.JSON)
        } When {
            post("/migration")
            get("/migration")
        } Then {
            statusCode(
                    equalTo(Response.Status.OK.statusCode)
            ).body("stage", equalTo("authentication"))
        }
    }

    @Test
    fun `Only one concurrent migration can be started`() {
        Given {
            spec(requestSpec).contentType(ContentType.JSON)
        } When {
            post("/migration")
            post("/migration")
        } Then {
            statusCode(
                    equalTo(Response.Status.CONFLICT.statusCode)
            ).body("error", equalTo("migration already exists"))
        }
    }
}