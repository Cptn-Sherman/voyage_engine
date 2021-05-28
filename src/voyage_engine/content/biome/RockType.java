package voyage_engine.content.biome;

public enum RockType {
    // intrusive
    Granite(RockPrimaryType.Igneous, RockSecondaryType.Felsic), Diorite(RockPrimaryType.Igneous, RockSecondaryType.Intermediate),
    Gabbro(RockPrimaryType.Igneous, RockSecondaryType.Mafic), Peridotite(RockPrimaryType.Igneous, RockSecondaryType.Ultramafic),
    // extrusive
    Rhyolite(RockPrimaryType.Igneous, RockSecondaryType.Felsic),
    Andesite(RockPrimaryType.Igneous, RockSecondaryType.Intermediate), Basalt(RockPrimaryType.Igneous, RockSecondaryType.Mafic),
    Komatiite(RockPrimaryType.Igneous, RockSecondaryType.Ultramafic),
    Obsidian(RockPrimaryType.Igneous, RockSecondaryType.Intermediate),
    Pumice(RockPrimaryType.Igneous, RockSecondaryType.Intermediate),
    Tuff(RockPrimaryType.Igneous, RockSecondaryType.Intermediate),

    Slate(RockPrimaryType.Metamorphic, RockSecondaryType.Foliated),
    Schist(RockPrimaryType.Metamorphic, RockSecondaryType.Foliated),
    Phyllite(RockPrimaryType.Metamorphic, RockSecondaryType.Foliated),
    Gneiss(RockPrimaryType.Metamorphic, RockSecondaryType.Foliated),

    Anthracite(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Amphibolite(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Hornfels(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    LapisLazuli(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Marble(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Mariposite(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Novaculite(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Quartzite(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Soapstone(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),
    Skarn(RockPrimaryType.Metamorphic, RockSecondaryType.Nonfoliated),

    Conglomerate(RockPrimaryType.Sedimentary, RockSecondaryType.Clastic),
    Breccia(RockPrimaryType.Sedimentary, RockSecondaryType.Clastic),
    Sandstone(RockPrimaryType.Sedimentary, RockSecondaryType.Clastic),
    Siltstone(RockPrimaryType.Sedimentary, RockSecondaryType.Clastic),
    Shale(RockPrimaryType.Sedimentary, RockSecondaryType.Clastic),

    GypsumRock(RockPrimaryType.Sedimentary, RockSecondaryType.Chemical),
    Dolomite(RockPrimaryType.Sedimentary, RockSecondaryType.Chemical),
    Chert(RockPrimaryType.Sedimentary, RockSecondaryType.Chemical),
    Flint(RockPrimaryType.Sedimentary, RockSecondaryType.Chemical),
    IronOre(RockPrimaryType.Sedimentary, RockSecondaryType.Chemical),
    Limestone(RockPrimaryType.Sedimentary, RockSecondaryType.Chemical),
    RockSalt(RockPrimaryType.Sedimentary, RockSecondaryType.Chemical),

    Chalk(RockPrimaryType.Sedimentary, RockSecondaryType.Organic), Coal(RockPrimaryType.Sedimentary, RockSecondaryType.Organic),
    Diatomite(RockPrimaryType.Sedimentary, RockSecondaryType.Organic);

    RockType(RockPrimaryType catagory, RockSecondaryType subCatagory) {
        this.catagory = catagory;
        this.subCatagory = subCatagory;
    }

    public RockPrimaryType catagory;
    public RockSecondaryType subCatagory;
}
