# px2csv

A Java port of the px2csv utility.

```shell
java -Xmx16m -Xms16m \
  -jar build/libs/px2csv-java-1.0-SNAPSHOT.jar \
     ./statfin_vtp_pxt_124l.px /dev/null
```

#### Profiling with Flight Recorder.

```shell
/usr/bin/time -v ~/.sdkman/candidates/java/17.0.5-amzn/bin/java \
  -XX:StartFlightRecording:filename=r17.jfr,dumponexit=true,settings=profile.jfc \
  -Xmx16m -Xms16m -jar build/libs/px2csv-java-1.0-SNAPSHOT.jar \
    ./statfin_vtp_pxt_124l.px /dev/null ISO-8859-1 10

/usr/bin/time -v ~/.sdkman/candidates/java/19.0.1-amzn/bin/java \
  -XX:StartFlightRecording:filename=r19.jfr,dumponexit=true,settings=profile.jfc \
  -Xmx16m -Xms16m -jar build/libs/px2csv-java-1.0-SNAPSHOT.jar \
    ./statfin_vtp_pxt_124l.px /dev/null ISO-8859-1 10
```

#### Parquet

https://github.com/apache/parquet-mr/blob/master/parquet-cli/src/main/java/org/apache/parquet/cli/csv/AvroCSV.java
