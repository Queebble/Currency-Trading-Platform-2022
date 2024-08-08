package Common.Constructors;

import java.io.Serializable;

/**
 * Constructs a new instance of an asset
 */
public class Asset implements Serializable {
    private static final long serialVersionUID = 6503491543949804932L;

    private String assetName;

    /**
     * No args constructor
     */
    public Asset(){
    }

    /**
     * Initialises an asset name
     * @param assetName the name of the asset
     */
    public Asset(String assetName) {
        this.assetName = assetName;
    }

    /**
     * Retrieves the asset name
     * @return the name of the asset
     */
    public String getAssetName() { return assetName; }

    /**
     * Sets the asset name.
     * @param assetName the asset name to be set
     */
    public void setAssetName(String assetName) { this.assetName = assetName; }
}