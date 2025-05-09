package gov.nist.asbestos.mhd.transforms;

import gov.nist.asbestos.client.channel.ChannelConfig;
import gov.nist.asbestos.client.resolver.IdBuilder;
import gov.nist.asbestos.client.resolver.ResourceMgr;
import gov.nist.asbestos.client.resolver.ResourceWrapper;
import gov.nist.asbestos.mhd.channel.CanonicalUriCodeEnum;
import gov.nist.asbestos.mhd.channel.MhdIgInterface;
import gov.nist.asbestos.mhd.channel.MhdIgImplEnum;
import gov.nist.asbestos.mhd.transactionSupport.AssigningAuthorities;
import gov.nist.asbestos.mhd.transactionSupport.CodeTranslator;
import gov.nist.asbestos.simapi.validation.Val;
import gov.nist.asbestos.simapi.validation.ValE;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import org.hl7.fhir.r4.model.Binary;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.ListResource;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * Mappings for XDS and MHD Mapping (XDS)
 * MHD 4.0.1
 * SubmissionSet
 * List	XDS SubmissionSet
 *    meta
 *       profile	SubmissionSet.limitedMetadata
 *    extension (designationType)	SubmissionSet.contentTypeCode
 *    extension (sourceId)	SubmissionSet.sourceId
 *    (IGNORED MAPPING) extension (intendedRecipient)	SubmissionSet.intendedRecipient
 *    identifier	SubmissionSet.entryUUID and SubmissionSet.uniqueId
 *    status	SubmissionSet.availabilityStatus
 *    mode	shall be 'working'
 *    title	SubmissionSet.title
 *    code	shall be 'submissionset'
 *    subject	SubmissionSet.patientId
 *    date	SubmissionSet.submissionTime
 *    (IGNORED) source	SubmissionSet.author
 *       extension (authorOrg)	SubmissionSet.author when the author is an Organization
 *    (IGNORED) note	SubmissionSet.comments
 *    (IGNORED?) entry is [0..1]
 *       item	references to DocumentReference(s) and Folder List(s)
 *       (To be verified by the DocumentSource tests and Inspector Inspect Request, ie. Assertion ID FTK4RM500 ?)
 *
 *      TODO - Contained option
 *      static String containedMetadataProfile = "http://profiles.ihe.net/ITI/MHD/4.0.1/StructureDefinition/IHE.MHD.UnContained.Comprehensive.ProvideBundle";
 */
public class MhdV4 implements MhdIgInterface {
    private final String BUNDLE_RESOURCES_DOC_REF = String.format("3.65.4.1.2.1 Bundle Resources. %s",  getDocBase("ITI-65.html#23654121-bundle-resources"));
    private static List<Class<?>> acceptableResourceTypes = Arrays.asList(ListResource.class, DocumentReference.class, Binary.class);
    private static MhdIgImplEnum mhdVersionEnum = MhdIgImplEnum.MHDv4;

    private CanonicalUriCodeEnum mhdBundleProfileEnum;
    private static final Logger logger = Logger.getLogger(MhdV4.class.getName());

    public MhdV4() {
        /*
        try {
            this.mhdBundleProfileEnum = getUriCodesClass().detectBundleProfileType(bundle).getKey();
        } catch (Exception ex) {
            this.mhdBundleProfileEnum = null;
            logger.warning("mhdBundleProfileEnum is null. Exception: " + ex );
        }

         */
    }



    @Override
    public MhdIgImplEnum getMhdIgImpl() {
        return mhdVersionEnum;
    }


    @Override
    public String getIheReference() {
        return BUNDLE_RESOURCES_DOC_REF;
    }



    @Override
    public List<Class<?>> getAcceptableResourceTypes() {
        return acceptableResourceTypes;
    }

    /**
     * Builds a submission set only if FHIR List resource matches a submission set type.
     * Returns null if resource is not a submission set type.
     * @param wrapper
     * @param vale
     * @param idBuilder
     * @param channelConfig
     * @param codeTranslator
     * @param assigningAuthorities
     * @return
     */
    @Override
    public RegistryPackageType buildSubmissionSet(MhdTransforms mhdTransforms, ResourceWrapper wrapper, Val val, ValE vale, IdBuilder idBuilder, ChannelConfig channelConfig, CodeTranslator codeTranslator, AssigningAuthorities assigningAuthorities, CanonicalUriCodeEnum canonicalUriCodeEnum) {

        /*
        if resource is of ListResource class type
            submissionset code must exist
                iterate codes
                must have an entry for
                         system value="http://profiles.ihe.net/ITI/MHD/CodeSystem/MHDlistTypes"/>
                        <code value="submissionset"/>
                only one submissionset bundle entry is allowed.

            create a  new method to buildRegistryPackageType
         */

        return new MhdV410Common(this, mhdTransforms, canonicalUriCodeEnum).buildSubmissionSet( wrapper, val, vale, idBuilder, channelConfig, codeTranslator, assigningAuthorities);
    }




    @Override
    public String getExtrinsicId(ValE valE, ResourceMgr rMgr, List<Identifier> identifiers) {
        return rMgr.allocateSymbolicId();
    }

}

