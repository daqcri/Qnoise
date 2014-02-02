/*
 * Copyright (c) Qatar Computing Research Institute, 2013.
 * Licensed under the MIT license <http://www.opensource.org/licenses/MIT>.
 */

package qa.qcri.qnoise;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.*;
import qa.qcri.qnoise.constraint.ConstraintFactory;
import qa.qcri.qnoise.model.NoiseModel;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NoiseSpecDeserializer implements JsonDeserializer<NoiseSpec> {
    @Override
    public NoiseSpec deserialize(
        JsonElement rootElement,
        Type type,
        JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        NoiseSpec spec = new NoiseSpec();
        JsonObject rootObj = (JsonObject)rootElement;
        JsonObject source = (JsonObject)(rootObj.get("source"));
        Preconditions.checkNotNull(source != null, "Source is not found.");

        JsonPrimitive pathPrimitive = source.getAsJsonPrimitive("path");
        Preconditions.checkNotNull(pathPrimitive);
        spec.inputFile = pathPrimitive.getAsString();

        JsonArray schemaArray = source.getAsJsonArray("type");
        if (schemaArray != null) {
            spec.schema = Lists.newArrayList();
            for (JsonElement primitive : schemaArray) {
                spec.schema.add(primitive.getAsString());
            }
        }

        JsonPrimitive csvSeparator = source.getAsJsonPrimitive("csvSeparator");
        if (csvSeparator != null) {
            spec.csvSeparator = csvSeparator.getAsCharacter();
        } else {
            spec.csvSeparator = NoiseSpec.DEFAULT_CSV_SEPARATOR;
        }

        JsonObject noiseObj = (JsonObject)(rootObj.get("noise"));
        JsonPrimitive noiseType = noiseObj.getAsJsonPrimitive("noiseType");
        Preconditions.checkNotNull(noiseType);
        spec.noiseType = NoiseType.fromString(noiseType.getAsString());

        JsonPrimitive granularity = noiseObj.getAsJsonPrimitive("granularity");
        if (granularity != null) {
            spec.granularity = GranularityType.fromString(granularity.getAsString());
        } else {
            // assign default granularity
            if (spec.noiseType == NoiseType.Simple)
                spec.granularity = GranularityType.Cell;
            else
                spec.granularity = GranularityType.Row;
        }

        JsonPrimitive percentage = noiseObj.getAsJsonPrimitive("percentage");
        Preconditions.checkArgument(percentage != null);
        spec.percentage = percentage.getAsDouble();

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

        JsonPrimitive constraint = noiseObj.getAsJsonPrimitive("constraint");
        if (constraint != null) {
            spec.constraint =
                ConstraintFactory.createConstraintFromString(constraint.getAsString());
        }

        JsonPrimitive logFile = noiseObj.getAsJsonPrimitive("logFile");
        if (logFile != null) {
            spec.logFile = logFile.getAsString();
        } else {
            Calendar calendar = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
            spec.logFile = "log" + dateFormat.format(calendar.getTime()) + ".csv";
        }

        // input rules
        if (spec.noiseType == NoiseType.Inconsistency)
            Preconditions.checkArgument(spec.constraint != null);

        if (spec.noiseType == NoiseType.Duplicate)
            Preconditions.checkArgument(
                spec.numberOfSeed != null,
                spec.distance
            );

        if (spec.noiseType == NoiseType.Simple)
            Preconditions.checkArgument(
                spec.granularity == GranularityType.Cell &&
                spec.distance != null,
                "Input value is missing or incorrect."
            );

        return spec;
    }
}
