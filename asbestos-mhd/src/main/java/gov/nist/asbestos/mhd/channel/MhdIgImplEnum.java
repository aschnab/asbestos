package gov.nist.asbestos.mhd.channel;

import gov.nist.asbestos.client.channel.IgNameConstants;
import gov.nist.asbestos.mhd.transforms.*;

import java.util.Objects;

/**
 * @author skb1
 */
public enum MhdIgImplEnum {
    MHDv3x(IgNameConstants.MHDV_3_X, MhdV3x.class, MhdV3xCanonicalUriCodes.class ),
    MHDv4(IgNameConstants.MHDV_4, MhdV4.class, MhdV4CanonicalUriCodes.class ),
    MHDv410(IgNameConstants.MHDV_410, MhdV410.class, MhdV410CanonicalUriCodes.class ),
    MHDv420(IgNameConstants.MHDV_420, MhdV420.class, MhdV410CanonicalUriCodes.class /* MHD 420 uses same codes as MHD 410 */);

    private IgNameConstants igName;
    private Class<? extends MhdIgInterface> mhdImplClass;
    private Class<? extends MhdCanonicalUriCodeInterface> uriCodesClass;

    MhdIgImplEnum(IgNameConstants igName, Class<? extends MhdIgInterface> mhdImplClass, Class<? extends MhdCanonicalUriCodeInterface> mhdCanonicalUriImplClass) {
        this.igName = igName;
        this.mhdImplClass = mhdImplClass;
        this.uriCodesClass = mhdCanonicalUriImplClass;
    }

    static public MhdIgImplEnum find(String s) {
        Objects.requireNonNull(s);
        for (MhdIgImplEnum p : values()) {
            if (s.equals(p.igName.toString())) return p;
            try {
                if (p == MhdIgImplEnum.valueOf(s)) return p;
            } catch (IllegalArgumentException e) {
                // continue;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return igName.getIgName();
    }

    public boolean equals(MhdIgImplEnum p) {
        return (p.toString().equals(this.toString()));
    }

    public boolean equals(String s) {
        return (this.toString().equals(s));
    }

    public IgNameConstants getIgName() { return igName; }


    public Class<? extends MhdIgInterface> getMhdImplClass() {
        return mhdImplClass;
    }

    public Class<? extends MhdCanonicalUriCodeInterface> getUriCodesClass() {
        return uriCodesClass;
    }


}
