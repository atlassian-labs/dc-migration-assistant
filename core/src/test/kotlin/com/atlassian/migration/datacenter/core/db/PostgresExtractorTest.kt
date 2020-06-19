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

package com.atlassian.migration.datacenter.core.db

import net.swiftzer.semver.SemVer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class PostgresExtractorTest {

    companion object {
        @JvmStatic
        fun versions() = listOf<Pair<String, SemVer>>(
            // Ubuntu trusty
            Pair("pg_dump (PostgreSQL) 9.3.24\n", SemVer(9, 3, 24)),

            // Ubuntu xenial
            Pair("pg_dump (PostgreSQL) 9.5.21\n", SemVer(9, 5, 21)),

            // Ubuntu bionic
            Pair("pg_dump (PostgreSQL) 10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)\n", SemVer(10, 12)),

            // Ubuntu eoan
            Pair("pg_dump (PostgreSQL) 11.7 (Ubuntu 11.7-0ubuntu0.19.10.1)\n", SemVer(11, 7)),

            // Ubuntu focal
            Pair("pg_dump (PostgreSQL) 12.2 (Ubuntu 12.2-4)\n", SemVer(12, 2)),

            // Ubuntu groovy
            Pair("pg_dump (PostgreSQL) 12.3 (Ubuntu 12.3-1)\n", SemVer(12, 3)),

            // Debian Buster
            Pair("pg_dump (PostgreSQL) 11.7 (Debian 11.7-0+deb10u1)\n", SemVer(11, 7)),

            // Debian Stretch
            Pair("pg_dump (PostgreSQL) 9.6.17\n", SemVer(9, 6, 17)),

            // CentOS 7
            Pair("pg_dump (PostgreSQL) 9.2.24\n", SemVer(9, 2, 24)),

            // CentOS 8
            Pair("pg_dump (PostgreSQL) 10.6\n", SemVer(10, 6)),

            // Amazon Linux 1
            Pair("pg_dump (PostgreSQL) 9.2.24\n", SemVer(9, 2, 24)),

            // Amazon Linux 2
            Pair("pg_dump (PostgreSQL) 9.2.24\n", SemVer(9, 2, 24))
        )
    }

    @ParameterizedTest
    @MethodSource("versions")
    fun testWithKnownValues(param: Pair<String, SemVer>) {
        assertEquals(param.second, PostgresExtractor.parsePgDumpVersion(param.first))
    }


}
