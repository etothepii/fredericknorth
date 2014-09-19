package uk.co.epii.conservatives.fredericknorth.opendata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 02:30
 */
public class DwellingGroupFactoryImpl implements DwellingGroupFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DwellingGroupFactoryImpl.class);

    private final HashMap<String, PostcodeDatumImpl> postcodes = new HashMap<String, PostcodeDatumImpl>();
    private final HashSet<String> postCodePreficesRequired = new HashSet<String>();
    private final Pattern postcodePrefixPattern = Pattern.compile("([A-Z]+)[0-9]+.*");

    @Override
    public PostcodeDatumImpl getInstance(String postcode) {
        return getImplementation(postcode);
    }

    @Override
    public Collection<? extends PostcodeDatum> getPostcodes(Rectangle bounds) {
        ArrayList<PostcodeDatumImpl> postcodeDatumList = new ArrayList<PostcodeDatumImpl>();
        for (PostcodeDatumImpl postcode : postcodes.values()) {
            if (bounds.contains(postcode.getPoint())) {
                postcodeDatumList.add(postcode);
            }
        }
        return postcodeDatumList;
    }

//    @Override
//    public String[] getRequiredPostcodePrefices() {
//        return postCodePreficesRequired.toArray(new String[postCodePreficesRequired.size()]);
//    }

    private PostcodeDatumImpl getImplementation(String postcode) {
        PostcodeDatumImpl postcodeDatum = postcodes.get(postcode);
        if (postcodeDatum == null) {
            postcodeDatum = new PostcodeDatumImpl(postcode);
            postcodes.put(postcode, postcodeDatum);
            try {
                Matcher mathcer = postcodePrefixPattern.matcher(postcode);
                if (mathcer.matches()) {
                    postCodePreficesRequired.add(mathcer.group(1));
                }
            }
            catch (IllegalStateException ise) {
                LOG.error(postcode);
                throw ise;
            }
        }
        return postcodeDatum;
    }
}
