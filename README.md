Qnoise <img src="https://api.travis-ci.org/Qatar-Computing-Research-Institute/Qnoise.png" />
======

### What is Qnoise?

Qnoise is a Java based noise data generator, developed by the data analytic group at [Qatar Computing Research Institute](da.qcri.org). 

### See it in Action

```
usage: qnoise.sh -f <input JSON noise spec. file> -o <output file>
All the options:
 -f <file>          Input JSON file path.
 -help              Print this message.
 -o <output file>   Output file path.
 -v                 Verbose output.

```

Currently Qnoise supports the following types of noises

* **Missing value noises (null).**
* **Duplication noises.**
* **Inconsistency noises based on certain contraints.**
* **Outlier value noises.**

A simple example of generating duplication spec. is as following

```
{
    "source" : "test/src/input/dumptest.csv",
    "noises" : [{
        "type" : "m",
        "granularity" : "cell",
        "percentage" : 0.2,
        "model" : "r"
    }]
}
```

More details on the usage can be found in the examples. 

### License

Qnoise is released under the terms of the [MIT License](http://opensource.org/licenses/MIT).

### Contact

For any issues or enhancement please use the issue pages on Github, 
or contact [siyin@qf.org.qa](mailto:siyin@qf.org.qa). We will try our best to help you sort it out.


