# qnoise R API
#
#   mat - input data matrix
#   noiseType - 
#       0: Missing
#       1: Inconsistency
#       2: Outlier
#       3: Error
#       4: Duplicate
#   percentage - percentage of the noise seed
#   granularity -
#       0: ROW
#       1: CELL
#   model - tuple picking model
#       0: Random
#       1: Histogram
#   filteredColumn - the columns to be filtered, a list of string.
#   seed - seed for duplicatations [0 - 1]
#   distance - distance from active domain, a list of double.
#   constraints - string constraints
#   logFile - logFile    
inject <- function(
    mat, noiseType, percentage, 
    granularity=0L, model=0L, filteredColumns=NULL, 
    seed=0.0, distance=NULL, constraints=NULL, logFile=NULL
) {
    library(rJava)
    .jinit()
    .jaddClassPath("out/bin/qnoise.jar")
    .jaddClassPath("out/bin/guava-14.0.1.jar")
    .jaddClassPath("out/bin/opencsv-2.3.jar")
    .jaddClassPath("out/bin/javatuples-1.2.jar")
    
    # convert data
    mat[] <- as.character(mat)
    data <- .jarray(mat, dispatch=TRUE)
    data <- .jcast(data, "[[Ljava/lang/String;")
    
    # filtered columns    
    if (is.null(filteredColumns)) {
        filteredColumns <- .jcast(.jnull(), "[Ljava/lang/String;")
    } else {
        filteredColumns <- .jarray(filteredColumns)
    }
        
    # distance
    if (is.null(distance)) {
        distance <- .jcast(.jnull(), "[D")
    } else {
        distance <- .jarray(distance)
    }
        
    # constraints
    if (is.null(constraints)) {
        constraints <- .jcast(.jnull(), "[Ljava/lang/String;")   
    } else {
        constraints <- .jarray(constraints)
    }
    
    # logFile
    if (is.null(logFile)) {
        logFile <- .jcast(.jnull(), "java/lang/String")
    }
    
    # convert mat
    result <- .jcall(
        "qa.qcri.qnoise.QnoiseFacade", 
        "[[Ljava/lang/String;", 
        "inject", 
        data, 
        noiseType, 
        granularity, 
        percentage, 
        model, 
        filteredColumns,
        seed,
        distance,
        constraints,
        logFile
    )
    
    return (lapply(result, .jevalArray))
}

