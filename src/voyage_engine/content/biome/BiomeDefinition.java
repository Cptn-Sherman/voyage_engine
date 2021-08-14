package voyage_engine.content.biome;

import voyage_engine.content.metadata.TaggableContent;

public class BiomeDefinition extends TaggableContent {
    
    private String name;
    private LatitudeRegion latitude_region;
    private SoilType[] dominate_soil_types;
    private int average_annual_rainfall;
    private int min_latitude, max_latitude;
    private float min_temperature, max_temperature;

    public BiomeDefinition() {
        super();
    }










}
