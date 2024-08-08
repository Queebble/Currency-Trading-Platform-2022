package Common.Constructors;

import java.io.Serializable;

/**
 * Constructs a new instance of an organisation's-asset
 */
public class OrgAsset implements Serializable {
    private static final long serialVersionUID = -2864446587422276351L;

    private String orgUnit;
    private String assetName;
    private double assetQty;

    /**
     * No args constructor
     */
    public OrgAsset(){
    }

    /**
     * Initialises a new orgAsset object
     * @param orgUnit the name of the organisation
     * @param assetName the name of the asset
     * @param assetQty the quantity of the asset
     */
    public OrgAsset(String orgUnit, String assetName, double assetQty) {
        this.orgUnit = orgUnit;
        this.assetName = assetName;
        this.assetQty = assetQty;
    }

    /**
     * Retrieves the name of the organisation
     * @return the organisation unit
     */
    public String getOrgUnit() { return orgUnit; }

    /**
     * Sets the organisation's name
     * @param orgUnit the organisation unit to be set
     */
    public void setOrgUnit(String orgUnit) { this.orgUnit = orgUnit; }

    /**
     * Retrieves the name of the asset
     * @return the name of the assets
     */
    public String getAssetName() { return assetName; }

    /**
     * Sets the name of the asset
     * @param assetName the asset name unit to be set
     */
    public void setAssetName(String assetName) { this.assetName = assetName; }


    /**
     * Retrieves the quantity of an asset owned by a particular org
     * @return the quantity of an asset
     */
    public double getAssetQty() { return assetQty; }

    /**
     * Sets the quantity of an asset owned by a particular org
     * @param assetQty the quantity to be set
     */
    public void setAssetQty(double assetQty) { this.assetQty = assetQty; }
}