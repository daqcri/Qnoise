/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.*;
import qa.qcri.qnoise.constraint.Constraint;
import qa.qcri.qnoise.constraint.ConstraintFactory;
import qa.qcri.qnoise.internal.GranularityType;
import qa.qcri.qnoise.internal.NoiseSpec;
import qa.qcri.qnoise.internal.NoiseType;
import qa.qcri.qnoise.model.NoiseModel;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NoiseJsonAdapterDeserializer implements JsonDeserializer<NoiseJsonAdapter> {
    @Override
    public NoiseJsonAdapter deserialize(
        JsonElement rootElement,
        Type type,
        JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        NoiseJsonAdapter adapter = new NoiseJsonAdapter();

        JsonObject rootObj = (JsonObject)rootElement;

        // ---------------- parsing source --------------------- //
        JsonObject source = (JsonObject)(rootObj.get("source"));
        Preconditions.checkNotNull(source != null, "Source is not found.");

        JsonPrimitive pathPrimitive = source.getAsJsonPrimitive("path");
        Preconditions.checkNotNull(pathPrimitive);
        adapter.inputFile = pathPrimitive.getAsString();

        JsonArray schemaArray = source.getAsJsonArray("type");
        if (schemaArray != null) {
            adapter.schema = Lists.newArrayList();
            for (JsonElement primitive : schemaArray) {
                adapter.schema.add(primitive.getAsString());
            }
        }

        JsonPrimitive csvSeparator = source.getAsJsonPrimitive("csvSeparator");
        if (csvSeparator != null) {
            adapter.csvSeparator = csvSeparator.getAsCharacter();
        } else {
            adapter.csvSeparator = NoiseJsonAdapter.DEFAULT_CSV_SEPARATOR;
        }

        // ---------------- parsing noises --------------------- //
        JsonElement noiseElement = rootObj.get("noise");
        JsonArray noiseArray;
        if (noiseElement instanceof JsonArray) {
            noiseArray = (JsonArray)(rootObj.get("noise"));
        } else {
            noiseArray = new JsonArray();
            noiseArray.add(noiseElement);
        }

        Preconditions.checkArgument(
            noiseArray != null && noiseArray.size() > 0,
            "Noise specification is null or empty."
        );

        adapter.specs = Lists.newArrayList();
        for (JsonElement specJson : noiseArray) {
            JsonObject noiseObj = (JsonObject)specJson;
            NoiseSpec spec = new NoiseSpec();

            JsonPrimitive noiseType = noiseObj.getAsJsonPrimitive("noiseType");
            Preconditions.checkNotNull(noiseType);
            spec.noiseType = NoiseType.fromString(noiseType.getAsString());

            JsonPrimitive granularity = noiseObj.getAsJsonPrimitive("granularity");
            if (granularity != null) {
                spec.granularity = GranularityType.fromString(granularity.getAsString());
            } else {
                // assign default granularity
                if (spec.noiseType == NoiseType.Duplicate)
                    spec.granularity = GranularityType.Row;
                else
                    spec.granularity = GranularityType.Cell;
            }

            JsonPrimitive percentage = noiseObj.getAsJsonPrimitive("percentage");
            if (percentage != null)
                spec.percentage = percentage.getAsDouble();
            else
                throw new IllegalArgumentException("Percentage cannot be null.");

            JsonPrimitive model = noiseObj.getAsJsonPrimitive("model");
            if (model != null) {
                spec.model = NoiseModel.fromString(model.getAsString());
            } else {
                spec.model = NoiseModel.Random;
            }

            JsonArray column = noiseObj.getAsJsonArray("column");
            if (column != null) {
                spec.filteredColumns = new String[column.size()];
                for (int i = 0; i < column.size(); i ++)
                    spec.filteredColumns[i] = column.get(i).getAsString();
            }

            JsonPrimitive numberOfSeed = noiseObj.getAsJsonPrimitive("numberOfSeed");
            if (numberOfSeed != null) {
                spec.numberOfSeed = numberOfSeed.getAsDouble();
            }

            JsonArray distance = noiseObj.getAsJsonArray("distance");
            if (distance != null) {
                spec.distance = new double[distance.size()];
                for (int i = 0; i < distance.size(); i ++)
                    spec.distance[i] = distance.get(i).getAsDouble();
            } else if (spec.filteredColumns != null)
                spec.distance = new double[spec.filteredColumns.length];

            // domain or distance
            if (spec.distance == null) {
                JsonArray domain = noiseObj.getAsJsonArray("domain");
                if (domain != null) {
                    spec.distance = new double[domain.size()];
                    for (int i = 0; i < domain.size(); i ++)
                        spec.distance[i] = domain.get(i).getAsDouble();
                } else if (spec.filteredColumns != null)
                    spec.distance = new double[spec.filteredColumns.length];
            }

            JsonArray constraints = noiseObj.getAsJsonArray("constraint");
            if (constraints != null) {
                spec.constraint = new Constraint[constraints.size()];
                for (int i = 0; i < constraints.size(); i ++)
                    spec.constraint[i] =
                        ConstraintFactory.createConstraintFromString(
                            constraints.get(i).getAsString()
                        );
            }

            JsonPrimitive logFile = noiseObj.getAsJsonPrimitive("logFile");
            if (logFile != null) {
                spec.logFile = logFile.getAsString();
            } else {
                Calendar calendar = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
                spec.logFile = "log" + dateFormat.format(calendar.getTime()) + ".csv";
            }
            adapter.specs.add(spec);
        }


        // -------------- verify specifications ------------------- //
        for (NoiseSpec spec : adapter.specs) {
            String errorMessage = NoiseHelper.verify(spec);
            if (errorMessage != null)
                throw new IllegalArgumentException(errorMessage);
        }

        return adapter;
    }
}
