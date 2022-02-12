package voyage_engine.assets;

public abstract class Asset {
	private String assetID;
	private boolean referenceCounted;
	private int references;
	protected boolean isReady;
	protected String filename;

	public Asset(boolean referenceCounted) {
		this.referenceCounted = referenceCounted;
		references = 0;
	}

	public boolean isReferencedCounted() {
		return referenceCounted;
	}

	public int getReferenceCount() {
		return references;
	}

	public void updateReferenceCount(int val) {
		references += val;
	}

	public String getAssetID() {
		return assetID;
	}

	public void setAssetID(String id) {
		assetID = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String name) {
		filename = name;
	}

	public abstract boolean isReady();

	public void setReady(boolean ready) {
		this.isReady = ready;
	}

	public String toAssetString() {
		return "[id: 0x" + String.format("%08x", assetID).toUpperCase() + ", type: " + this.getClass().getSimpleName().toUpperCase() + ", references " + getReferenceCount() + ", filename: \"" + getFilename() + "\"]";
	}
}
