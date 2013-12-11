/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.collect.Maps;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Noise generation specification.
 */
public class NoiseSpec {
    private HashMap<SpecEntry, Object> entries;
    public enum SpecEntry {
        InputFile,
        Schema,
        NoiseType,
        Granularity,
        Percentage,
        Model,
        Column,
        NumberOfSeed,
        Distance,
        Constraint;

        public static SpecEntry fromString(String v) {
            SpecEntry[] dict = SpecEntry.values();
            SpecEntry match = null;
            for (SpecEntry entry : dict) {
                if (entry.name().equalsIgnoreCase(v)) {
                    match = entry;
                    break;
                }
            }

            if (match == null) {
                throw new IllegalArgumentException("Unknown spec. entry " + v);
            }
            return match;
        }
    }

    private NoiseSpec() {
        entries = Maps.newHashMap();
    }

    @SuppressWarnings("unchecked")
    public NoiseSpec(NoiseSpec spec) {
        this.entries = (HashMap<SpecEntry, Object>)spec.entries.clone();
    }

    public boolean hasEntry(SpecEntry entry) {
        return entries.containsKey(entry);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(SpecEntry entry) {
        return (T)entries.get(entry);
    }

    @SuppressWarnings("unchecked")
    public static NoiseSpec valueOf(JSONObject jsonObject) {
        NoiseSpec spec = new NoiseSpec();

        JSONObject sourceObj = (JSONObject)jsonObject.get("source");
        String inputFile = (String)sourceObj.get("path");
        spec.entries.put(SpecEntry.InputFile, inputFile);
        if (sourceObj.containsKey("type")) {
            spec.entries.put(SpecEntry.Schema, sourceObj.get("type"));
        } else {
            // TODO: remove the trick
            spec.entries.put(SpecEntry.Schema, null);
        }

        JSONObject noise = (JSONObject)jsonObject.get("noise");
        Set<Map.Entry<String, String>> entries = noise.entrySet();
        for (Map.Entry<String, String> entryObj : entries) {
            SpecEntry entry = SpecEntry.fromString(entryObj.getKey());
            spec.entries.put(entry, entryObj.getValue());
        }

        return spec;
    }
}
