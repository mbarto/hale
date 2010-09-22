var wpsURL= "http://localhost:8080/cst-wps/IOBridgeServlet.py";
var uploadURL= "http://localhost:8080/cst-wps/upload";
var IFrameObj;
var map;
var ingml;
var outgml;
var result;

/**
 * Initializa the whole application
 */
var init = function(){
        
    if (window.location.port != 8080) {
        OpenLayers.ProxyHost="/cgi-bin/olproxy.cgi?url=";
    }
    var wps = new OpenLayers.WPS(wpsURL,{onDescribedProcess: onDescribedProcess});
    var iobridge = new OpenLayers.WPS.Process({identifier:"iobridge"});
    wps.addProcess(iobridge);
    //wps.describeProcess("iobridge");

    // modify the form
    document.forms[0].onsubmit=function() {
                document.forms[0].target = "upload_target";
                document.getElementById("upload_target").contentWindow.location = "server.html";
                return true;
    }

    /*******/
     return;
    /*******/

    // init map
    //
    map = new OpenLayers.Map( 'map' );
    layer = new OpenLayers.Layer.WMS( "OpenLayers WMS",
            "http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic'} );
    map.addLayer(layer);
    map.zoomToMaxExtent();
    map.addControl(new OpenLayers.Control.LayerSwitcher());

};

/**
 * Called from the inner frame, from the response of the upload servlet
 */
var handleResponse = function(result) {
    result = result;
    execute(result.gml,result.oml,result.schema);
    //try {
    //document.getElementById("indicator").style.display="none";
    //if (ingml) {
    //    map.removeLayer(ingml);
    //    ingml.destroy();
    //    ingml = undefined;
    //}
    //if (outgml) {
    //    map.removeLayer(ingml);
    //    ingml.destroy();
    //    ingml = undefined;
    //}
    //ingml = new OpenLayers.Layer.GML("Input vector data",result.gml);
    //map.addLayer(ingml);
    ////map.zoomToExtent(ingml.getDataExtent());
    //}catch(e){console.log(e)}
};

/**
 * Fill the schemes select box with available schemes
 */
var onDescribedProcess = function(process){
    document.getElementById("indicator").style.display="none";
    var schemaInput = process.getInput("schema");
    for (var i = 0; i < schemaInput.allowedValues.length; i++) {
        var option = document.createElement("option");
        option.setAttribute("value",schemaInput.allowedValues[i]);
        option.appendChild(document.createTextNode(schemaInput.allowedValues[i]));
        document.forms[0].schema.appendChild(option);
    }

}

/**
 * Files are uploaded, call the execute request
 */
var execute = function(gml,oml,schema) {
    document.getElementById("indicator").style.display="block";
    // define the WPS instance
    var wps = new OpenLayers.WPS(wpsURL,{onSucceeded: onExecuted,
                                                onFailed: onFailed});

    // define inputs and outputs
    var schemaInput = new OpenLayers.WPS.ComplexPut({identifier:"schema",
            value: window.location.href+schema,asReference: true});
    var omlInput = new OpenLayers.WPS.ComplexPut({identifier:"oml",
            value: window.location.href+oml,asReference: true});
    var gmlInput = new OpenLayers.WPS.ComplexPut({identifier:"gml",
            value: window.location.href+gml,asReference: true});

    var gmlOutput = new OpenLayers.WPS.ComplexPut({identifier:"gml",
            asReference: true});

    // define the iobridge process
    var ioBridgeProcess = new OpenLayers.WPS.Process({identifier:"iobridge",
            inputs: [schemaInput, omlInput, gmlInput],
            outputs: [gmlOutput]});

    // register process
    wps.addProcess(ioBridgeProcess);
    
    // execute process
    wps.execute(ioBridgeProcess.identifier);
};

/**
 * Called when WPS successfully finished
 */
var onExecuted = function(process) {
    document.getElementById("indicator").style.display="none";
    var gmlUrl = process.outputs[0].getValue();
    document.getElementById("wps-results").innerHTML = gmlUrl;
};

/**
 * Called when WPS failed
 */
var onFailed = function(process) {
    document.getElementById("indicator").style.display="none";
    document.getElementById("wps-results").innerHTML = "<b>"+process.exception.code+": </b>"+
        process.exception.text;
};
