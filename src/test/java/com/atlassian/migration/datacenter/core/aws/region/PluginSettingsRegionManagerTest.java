package com.atlassian.migration.datacenter.core.aws.region;

import com.atlassian.migration.datacenter.core.aws.GlobalInfrastructure;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;

import java.util.ArrayList;

import static com.atlassian.migration.datacenter.core.aws.region.PluginSettingsRegionManager.AWS_REGION_PLUGIN_STORAGE_KEY;
import static com.atlassian.migration.datacenter.core.aws.region.PluginSettingsRegionManager.REGION_PLUGIN_STORAGE_SUFFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PluginSettingsRegionManagerTest {

    PluginSettingsRegionManager pluginSettingsRegionManager;
    @Mock
    private GlobalInfrastructure globalInfrastructure;
    @Mock
    private PluginSettingsFactory pluginSettingsFactory;
    @Mock
    private PluginSettings pluginSettings;
    private String pluginSettingsRegionKey = AWS_REGION_PLUGIN_STORAGE_KEY + REGION_PLUGIN_STORAGE_SUFFIX;

    @BeforeEach
    void setUp() {
        this.pluginSettingsRegionManager = new PluginSettingsRegionManager(() -> this.pluginSettingsFactory, globalInfrastructure);
        when(this.pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);
        this.pluginSettingsRegionManager.postConstruct();
    }

    @Test
    void shouldGetRegionFromPluginSettingsWhenKeyExists() {
        when(this.pluginSettings.get(pluginSettingsRegionKey)).thenReturn("area-51");

        String region = this.pluginSettingsRegionManager.getRegion();
        assertEquals("area-51", region);
    }

    @Test
    void shouldDefaultToUsEast1aRegionFromPluginSettingsWhenKeyDoesNotExists() {
        when(this.pluginSettings.get(pluginSettingsRegionKey)).thenReturn("");

        String region = this.pluginSettingsRegionManager.getRegion();
        assertEquals(Region.US_EAST_1.toString(), region);
    }

    @Test
    void shouldStoreValidRegion() throws Exception {
        String validRegion = "area-52";
        when(this.globalInfrastructure.getRegions()).thenReturn(new ArrayList<String>() {{
            add("area-50");
            add("area-51");
            add("area-52");
        }});

        this.pluginSettingsRegionManager.storeRegion(validRegion);
        verify(this.pluginSettings).put(pluginSettingsRegionKey, validRegion);
    }

    @Test
    void shouldThrowExceptionWhenTryingToStoreAnInvalidRegion() {
        String invalidRegion = "area-53";
        when(this.globalInfrastructure.getRegions()).thenReturn(new ArrayList<>());

        assertThrows(InvalidAWSRegionException.class, () -> {
            this.pluginSettingsRegionManager.storeRegion(invalidRegion);
        });
        verify(this.pluginSettings, never()).put(anyString(), anyString());
    }
}