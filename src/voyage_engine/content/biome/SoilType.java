package voyage_engine.content.biome;

public enum SoilType {
    ALFISOLS(SoilFertility.HIGH),
    ANDISOLS(SoilFertility.HIGH),
    ARIDISOLS(SoilFertility.LOW),
    ENTISOLS(SoilFertility.LOW),
    GELISOLS(SoilFertility.MODERATE),
    HISTOSOLS(SoilFertility.LOW),
    INCEPTISOLS(SoilFertility.HIGH),
    MOLLISOLS(SoilFertility.HIGH),
    OXISOLS(SoilFertility.LOW),
    SPODOSOLS(SoilFertility.LOW),
    ULTISOLS(SoilFertility.LOW),
    VERTISOLS(SoilFertility.MODERATE);

    SoilType (SoilFertility fert) {
        fertility = fert;
    }
    private SoilFertility fertility;

    public SoilFertility getFertility() {
        return fertility;
    }

    public enum SoilFertility {
        HIGH,
        MODERATE,
        LOW;
    }
}
