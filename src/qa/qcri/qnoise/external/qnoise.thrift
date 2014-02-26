namespace java qa.qcri.qnoise.external

enum TNoiseType {
    Missing = 1,
    Inconsistency = 2,
    Outlier = 3,
    Error = 4,
    Duplicate = 5
}

enum TNoiseModel {
    Random = 1,
    Histogram = 2
}

struct TQnoiseSpec {
    1: required TNoiseType noiseType,
    2: required double percentage,
    3: required TNoiseModel model,
    4: optional bool isOnCell,
    5: optional list<string> filteredColumns,
    6: optional double numberOfSeed,
    7: optional list<double> distance,
    8: optional list<string> constraint,
    9: optional string logfile
}

struct TQnoiseInput {
    1: required list<list<string>> data,
    2: required list<string> header,
    3: optional list<string> type,
    4: required list<TQnoiseSpec> specs
}

service TQnoise {
    list<list<string>> inject(1: TQnoiseInput param);
}
