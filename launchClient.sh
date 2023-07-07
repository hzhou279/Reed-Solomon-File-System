#!/bin/bash

# java -cp .:./maven-repo/reed-solomon-1.0.jar edu.cmu.reedsolomonfs.client.Client

# /usr/bin/env /home/vscode/.vscode-remote/extensions/redhat.java-1.20.0-linux-x64/jre/17.0.7-linux-x86_64/bin/java @/tmp/cp_vuk5cdv54yla9eeb6dl1w69c.argfile edu.cmu.reedsolomonfs.client.Client cluster 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083,127.0.0.1:8084,127.0.0.1:8085,127.0.0.1:8086
# command=("mvn" "exec:java" "-Dexec.mainClass=edu.cmu.reedsolomonfs.client.Client" "-Dexec.args=cluster 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083,127.0.0.1:8084,127.0.0.1:8085,127.0.0.1:8086")
# "${command[@]}"

mvn exec:java -Dexec.mainClass=edu.cmu.reedsolomonfs.client.Client -Dexec.args="cluster 127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083,127.0.0.1:8084,127.0.0.1:8085,127.0.0.1:8086" -Dexec.classpathScope="compile" -Dexec.additionalClasspath="/maven-repo/reed-solomon-1.0.jar"
