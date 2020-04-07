package com.atlassian.migration.datacenter.spi.infrastructure;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProvisioningConfigTest {

    @Test
    void shouldGetListParametersAsCommaSeparatedValues() {
        ProvisioningConfig config = new ProvisioningConfig("https://template.url", "stackName", new HashMap<String, Object>() {{
            put("azs", new ArrayList<String>() {{
                add("us-east-2a");
                add("us-east-2b");
            }});
            put("password", "iamsupersecure.trustme");
            put("instanceCount", 2);
        }});

        Map<String, String> params = config.getParams();
        assertEquals("us-east-2a,us-east-2b", params.get("azs"));
        assertEquals("iamsupersecure.trustme", params.get("password"));
        assertEquals("2", params.get("instanceCount"));
    }
}
