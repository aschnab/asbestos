<template>
    <div>
        <div class="left">
            <div class="tool-title">Get/Inspect/Validate</div>
            <p>Inspect a Resource from one of several sources. Validation is run in the Inspector.</p>


            <div class="vdivider"></div>
            <div class="vdivider"></div>

            <input type="checkbox" id="doGzip" v-model="gzip">
            <label for="doGzip">Use GZip in GET request?</label>
            <br />
            <br />

            <!--   GET URL  -->
            <div>
                <div class="left">
                    <button class="left" @click="run()">INSPECT</button>
                    <label for="urlInput">URL</label>
                    <div class="divider"> </div>
                    <input id="urlInput" v-model="url" size="80" v-on:keyup="maybeRun">
                </div>

            </div>

            <!--   GET CapabilityStatement  -->
            <button class="left" @click="runCapStmt()">INSPECT</button> CapabilityStatement from selected Channel ({{channelName}})
            <div class="left">

            </div>

            <!--   INSPECT  -->
            <button class="left" @click="inspect">INSPECT</button>
            <label for="json">Resource XML/JSON</label>
            <br />
            <textarea id="json" v-model="resourceText" cols="80" rows="15" placeholder="Paste Resource JSON or XML here"> </textarea>

            <!--   Display area   -->
            <template v-if="isLoading"><p>Loading...</p></template>
            <template v-else>
            <div v-if="theUrl" class="request-response" :key="rerenderkey">
                <log-analysis-report
                        :session-id="sessionId"
                        :channel-name="channelName"
                        :the-url="theUrl"
                        :gzip="gzip"
                        :use-proxy="useProxy"> </log-analysis-report>
            </div>
            <div v-else-if="capStmt" class="request-response" :key="reCapStmt">
                <div class="left">
                    <log-analysis-report
                            :session-id="sessionId"
                            :channel-name="channelName"
                            :the-url="`${getProxyBase({channelName: channelName, sessionId: sessionId})}/metadata`"
                            :gzip="gzip"
                            :use-proxy="useProxy"
                            :ignore-bad-refs="true"> </log-analysis-report>
                </div>
            </div>
            <div v-else-if="inspection" class="request-response">
                <log-analysis-report> </log-analysis-report>
            </div>
            </template>
        </div>
    </div>
</template>

<script>
    import LogAnalysisReport from "../logViewer/LogAnalysisReport";
    import {FHIRTOOLKITBASEURL, UtilFunctions} from "../../common/http-common";
    import Vue from "vue"
    import VueSimpleAlert from "vue-simple-alert";

    Vue.use(VueSimpleAlert);
    export default {
        data() {
            return {
                url: null,
                theUrl: null,
                gzip: true,
                useProxy: false,
                rerenderkey: 0,
                selection: null,
                validate: false,
                reCapStmt: 0,
                capStmt: null,
                reValidation: 0,
                validation: null,
                inspection: false,
                resourceText: "",
                isLoading: false,
            }
        },
        methods : {
            getProxyBase(parms)  {
                if (parms === null)
                    return UtilFunctions.getProxyBase()
                const channelName = parms.channelName
                const sessionId = parms.sessionId
                return `${UtilFunctions.getProxyBase()}/${sessionId}__${channelName}`
            },
            inspect() {
                console.log(`running inspect`)
                this.isLoading = true
                this.inspection = true
                this.theUrl = null
                this.capStmt = false
                this.$store.dispatch('analyseResource', this.resourceText)
                    .then( ()=>{this.isLoading = false; })
                    .catch(()=>{this.isLoading = false; })
                    .finally(() => {this.isLoading = false;})

            },
            run() {   // GET from URL
                this.theUrl = this.url
                this.capStmt = false
                this.inspection = false
                this.rerenderkey += 1
            },
            runCapStmt() {
                this.capStmt = true
                this.inspection = false
                this.theUrl = null
                this.reCapStmt += 1
            },
            maybeRun(key) {
                if (key.keyCode === 13) // enter
                    this.run()
            },
            runCapStmtValidation() {
                this.validate = true
                this.reValidation += 1
            },
            runValidation() {
                this.$store.dispatch('getValidation')
            },
            async validateCall(resourceType, url) {
                const valUrl =`${FHIRTOOLKITBASEURL}/validate/${resourceType}?${url}`
                console.log(`valUrl is ${valUrl}`)
            }
        },
        computed: {
            validateCallUrl() {
                const url = this.getProxyBase({channelName: this.channelName, sessionId: this.sessionId}) + '/metadata'
                return this.validateCall('CapabilityStatement', url)
            },
        },
        created() {
            if (this.$store.state.log.validationServer === null)
                this.$store.dispatch('getValidationServer')
        },
        props: [
            'sessionId', 'channelName'
        ],
        components: { LogAnalysisReport },
        name: "Getter"
    }
</script>

<style scoped>
    .request-response {
        position: relative;
        left: 60px;
        text-align: left;
    }
    .work-area {
        border: 2px black solid;
    }
    /*// offset for LogAnalysisReport*/
    .indent {
        left: 60px;
    }
</style>
