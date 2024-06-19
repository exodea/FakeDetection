<template>
    <div class="hello">
        <h1>{{ msg }}</h1>
        <p>
            This is a service that will help you detect fake photos
        </p>

        <h3>Upload photo</h3>
        <div class="upload-container" ref="uploadContainer">
            <div class="upload-form-group">
                <label class="upload-label">
                    <i class="material-icons">attach_file</i>
                    <span class="upload-title">Upload Image</span>
                    <input type="file"
                           v-bind:value="test"
                            v-on:input="handleFileUpload( $event )">
                </label>
            </div>
            <p>
                <input type="checkbox" id="privacyCheckbox" v-model="isPrivacy" :disabled="isPrivacy" @change="changePrivacyStyle()">
                <label for="privacyCheckbox"
                       :style="{'color': privacyColor}"
                >I agree to the Terms and Conditions and Privacy Policy</label>
            </p>
        </div>
        <clip-loader :loading="uploadLoading" :color="loadingColor" :size="loadingSize"></clip-loader>

        <h3>Result of analyzer</h3>
        <clip-loader :loading="noiseLoading" :color="loadingColor" :size="loadingSize"></clip-loader>
        <div class="result" :style="{'color': isImageOriginal ? '#4CAF50' : '#F44336'}">{{resultProbability}}</div>

        <details>
            <summary>Additional info for geeks</summary>
            <div class="geek-info" v-html="geekInfo"></div>
        </details>

        <h3>Contacts</h3>
        <ul>
            <li><a href="mailto:vladislav.ugm@gmail.com" target="_blank">Gmail</a></li>
        </ul>

    </div>
</template>

<script>
    import axios from "axios";
    import ClipLoader from 'vue-spinner/src/ClipLoader.vue'

    export default {
        name: 'MainPage',
        title: 'Fake Detector',
        props: {
            msg: String
        },
        components: {
            ClipLoader
        },

        data() {
            return {
                test: '',
                isPrivacy: false,
                privacyColor: '#2c3e50',
                BLOG: [],
                file: '',
                resultProbability: '',
                isImageOriginal: false,
                geekInfo: '',
                uploadLoading: false,
                noiseLoading: false,
                neuralLoading: false,
                loadingColor: '#42b983',
                loadingSize: '50px'
                // loading: false
            };
        },
        methods: {
            changePrivacyStyle(){
                if(!this.isPrivacy){
                    this.privacyColor = '#F44336';
                }else{
                    this.privacyColor = '#2c3e50';
                }
            },
            handleFileUpload( event ){
                this.changePrivacyStyle();
                if(!this.isPrivacy){
                    return;
                }

                this.file = event.target.files[0];

                // this.$refs.noiseImg.src = '';
                this.hideUploader();
                this.noiseLoading = true;

                let formData = new FormData();
                formData.append('file', this.file);

                axios.post( 'http://localhost:8081/analyzing',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        },
                        responseType: 'json'
                    }
                ).then(response => {
                    console.log(response.data);

                    let metadataOriginal = response.data.metadataResult.originalProbability;

                    const prefix = "This photo is ";
                    let neuralOriginal = response.data.neuralResult.originalProbability;
                    if (metadataOriginal === 0){
                        this.isImageOriginal = false;
                        this.resultProbability = prefix+"FAKE";
                    } else if (neuralOriginal === "FAKE") {
                        this.isImageOriginal = false;
                        this.resultProbability = prefix+"FAKE";
                    } else if (neuralOriginal === "REAL") {
                        this.isImageOriginal = true;
                        this.resultProbability = prefix+"REAL";
                    } else {
                        this.isImageOriginal = false;
                        this.resultProbability = "Can't decide";
                    }

                    this.geekInfo = '';
                    this.geekInfo += 'Fake reasons:\n';
                    this.geekInfo += response.data.metadataResult.fakeReasons;
                    this.geekInfo += '\nMetadata:\n';
                    this.geekInfo += response.data.metadataResult.metadata;
                    this.geekInfo = this.geekInfo.replace(/\n/g, "<br>");



                    this.noiseLoading = false;

                    this.showUploader();
                })
                .catch(error => {
                    console.log('FAILURE!!');
                    console.log(error.response)

                    this.isImageOriginal = false;
                    this.resultProbability = error.response.data;

                    this.noiseLoading = false;
                    this.showUploader();
                });
            },
            showUploader(){
                if(this.noiseLoading === false && this.neuralLoading === false) {
                    this.uploadLoading = false;
                    this.$refs.uploadContainer.style.display = "block";
                }
            },
            hideUploader(){
                this.resultProbability = '';
                this.geekInfo = '';
                this.uploadLoading = true;
                this.$refs.uploadContainer.style.display = "none";
            }
        }
    }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    h3 {
        margin: 40px 0 0;
    }
    ul {
        list-style-type: none;
        padding: 0;
    }
    li {
        display: inline-block;
        margin: 0 10px;
    }
    a {
        color: #42b983;
    }
    summary {
        /*font-family: Avenir, Helvetica, Arial, sans-serif;*/
        /*text-align: center;*/
        color: #2c3e50;
        font-size: 1.17em;
        font-weight: bold;
        margin-bottom: 10px;
    }

    /*.loader {*/
    /*    border: 16px solid #f3f3f3;*/
    /*    border-top: 16px solid #3498db;*/
    /*    border-radius: 50%;*/
    /*    width: 36px;*/
    /*    height: 36px;*/
    /*    animation: spin 2s linear infinite;*/
    /*}*/
    /*@keyframes spin {*/
    /*    0% {*/
    /*        transform: rotate(0deg);*/
    /*    }*/
    /*    100% {*/
    /*        transform: rotate(360deg);*/
    /*    }*/
    /*}*/

    .upload-container .upload-form-group{
        padding:1em;
        margin:1em;
    }
    .upload-container input[type=file]{outline:0;
        opacity:0;
        pointer-events:none;
        user-select:none;
    }
    .upload-container .upload-label{
        width:120px;
        border:2px dashed grey;
        border-radius:5px;
        display:block;
        padding:1.2em;
        transition:border 300ms ease;cursor:pointer;text-align:center;
    }
    .upload-container .upload-label i{
        display:block;
        font-size:42px;
        padding-bottom:16px;
    }
    .upload-container .upload-label i,.upload-container .upload-label .upload-title{
        color:grey;
        transition:200ms color;
    }
    .upload-container .upload-label:hover{
        border:2px solid #000;
    }
    .upload-container .upload-label:hover i,.upload-container .upload-label:hover .upload-title{
        color:#000;
    }
    .upload-label{
        display: block;
        margin: 0 auto;
    }
    .result{
        margin: 25px;
    }
    .geek-info{
        margin: 0 auto;
        width: 40vw;

        text-align: left;
    }
</style>
