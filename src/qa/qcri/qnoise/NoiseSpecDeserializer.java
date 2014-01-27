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
            spec.filteredColumns = Lists.newArrayList();
            for (JsonElement element : column) {
                spec.filteredColumns.add(element.getAsString());
            }
        }

        JsonPrimitive numberOfSeed = noiseObj.getAsJsonPrimitive("numberOfSeed");
        if (numberOfSeed != null) {
            spec.numberOfSeed = numberOfSeed.getAsDouble();
        }

        JsonPrimitive distance = noiseObj.getAsJsonPrimitive("distance");
        if (distance != null) {
            spec.distance = distance.getAsDouble();
        }

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
            Preconditions.checkArgument(constraint != null);

        if (spec.noiseType == NoiseType.Duplicate)
            Preconditions.checkArgument(numberOfSeed != null);

        return spec;
    }
}
