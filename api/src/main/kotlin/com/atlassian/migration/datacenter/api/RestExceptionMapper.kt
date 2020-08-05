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

package com.atlassian.migration.datacenter.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider

/***
 * Any uncaught exceptions in the REST API will by default return XML to the caller.
 * This forces a JSON wrapper around the exception.
 */
@Provider
class RestExceptionMapper : ExceptionMapper<Exception>
{
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    private fun traceToString(e: Exception): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    @Produces(MediaType.APPLICATION_JSON)
    override fun toResponse(e: Exception): Response {
        val strace = traceToString(e)
        val se = e.toString()
        log.error("Caught uncaught API exception: {}: {}", se, strace)

        return Response.status(500)
                .entity(mapOf(
                        "exception" to se,
                        "stacktrace" to strace
                )).build()
    }
}