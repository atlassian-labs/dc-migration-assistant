package com.atlassian.migration.datacenter.core.aws.infrastructure

import com.atlassian.migration.datacenter.core.aws.CfnApi
import software.amazon.awssdk.services.cloudformation.model.Stack
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import software.amazon.awssdk.services.cloudformation.model.Output
import software.amazon.awssdk.services.cloudformation.model.Parameter
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class AtlassianInfrastructureServiceTest {

    @MockK
    lateinit var cfnApi: CfnApi

    @InjectMockKs
    lateinit var service: AtlassianInfrastructureService

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    val vpcid = Output.builder()
            .outputKey("VPCID")
            .outputValue("ANY")
            .build()
    val privatesn = Output.builder()
            .outputKey("PrivateSubnets")
            .outputValue("ANY")
            .build()
    val publicsn = Output.builder()
            .outputKey("PublicSubnets")
            .outputValue("ANY")
            .build()
    val dummy1 = Output.builder()
            .outputKey("DUMMY1")
            .outputValue("ANY")
            .build()
    val dummy2 = Output.builder()
            .outputKey("DUMMY2")
            .outputValue("ANY")
            .build()
    val export = Parameter.builder()
            .parameterKey("ExportPrefix")
            .parameterValue("ANY")
            .build()

    @Test
    fun testSingleValid() {
        every { cfnApi.listStacksFull() } returns listOf(
                Stack.builder()
                        .outputs(vpcid, privatesn, publicsn)
                        .parameters(export)
                        .build()
        )
        assertEquals(1, service.findASIs().count())
    }

    @Test
    fun testSinglePartial() {
        every { cfnApi.listStacksFull() } returns listOf(
                Stack.builder().outputs(vpcid, privatesn).build()
        )
        assertEquals(0, service.findASIs().count())
    }

    @Test
    fun testSingleInMiddle() {
        every { cfnApi.listStacksFull() } returns listOf(
                Stack.builder().outputs(dummy1).parameters(export).build(),
                Stack.builder().outputs(vpcid, privatesn, publicsn).parameters(export).build(),
                Stack.builder().outputs(vpcid, privatesn, dummy2).build()
        )
        assertEquals(1, service.findASIs().count())
    }

}