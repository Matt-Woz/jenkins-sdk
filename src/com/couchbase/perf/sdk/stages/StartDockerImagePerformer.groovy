package com.couchbase.perf.sdk.stages

import groovy.transform.CompileStatic
import com.couchbase.context.StageContext
import com.couchbase.stages.Stage

@CompileStatic
class StartDockerImagePerformer extends Stage {

    private String imageName
    private String containerName
    private int port
    private final String version

    StartDockerImagePerformer(String imageName, String containerName, int port, String version) {
        this.version = version
        this.imageName = imageName
        this.containerName = containerName
        this.port = port
    }

    @Override
    String name() {
        return "Start docker image $imageName with name ${containerName} on $port"
    }

    @Override
    void executeImpl(StageContext ctx) {
        // -d so this will run in background
        ctx.env.execute("docker run --rm -d --network perf -p $port:8060 --name ${containerName} $imageName",
                false, false, true)
        // Stream the logs in the background
        ctx.env.execute("docker logs --follow ${containerName}", false, false, true, true)
        // Neither of the commands above will block
    }

    @Override
    void finishImpl(StageContext ctx) {
        ctx.env.executeSimple("docker kill ${containerName}")
    }
}