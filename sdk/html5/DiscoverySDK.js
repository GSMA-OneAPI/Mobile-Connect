if (typeof console == "undefined" || typeof console.log == "undefined") var console = { log: function() {} };
/**
 * @constructor
 * @this {Link}
 * @param href {?String} url
 * @param rel {?String} name
 * @throws {callApiDiscoveryException}  Will throw an error if the arguments has not the correct type.
 */
function Link(href, rel) {
	if(!isAString(href))
		throw (new callApiDiscoveryException("Link object create error", "Bad format: href is not a String"));
		if(!isAString(rel))
			throw (new callApiDiscoveryException("Link object create error","Bad format: rel is not a String"));
			this.href = href;
			this.rel = rel;
}
var console = (window.console = window.console || {});
/**
 * Get href of a Link object
 * @returns href {?String} url
 */
Link.prototype.getHref = function(){
	return(this.href);
};

/**
 * Set href of a Link object
 * @param href {?String} url
 */
Link.prototype.setHref = function(href){
	this.href=href;
};

/**
 * Get rel of a Link object
 * @returns rel {?String} name
 */
Link.prototype.getRel = function(){
	return(this.rel);
};

/**
 * Get rel of a Link object
 * @param rel {?String} name
 */
Link.prototype.setRel = function(rel){
	this.rel=rel;
};

/**
 * isAString
 * @access private
 * @param obj {Object | string}
 * @returns {Boolean} 
 */
function isAString(obj) {		
	return((typeof(obj)==='string')||(typeof(obj)==="String"));
}
/**
 * isANumber
 * @access private
 * @param obj {Object | number}
 * @returns {Boolean}
 */
function isANumber(obj) {		
	return((typeof(obj)==='number')||(typeof(obj)==="Number"));
}



/**
 * @constructor
 * @throws {callApiDiscoveryException}  Will throw an error if the arguments has not the correct type.
 * @this {Api}
 * @param links {Array}
 */
function Api(links){
	if(!links instanceof Array)
		throw (new callApiDiscoveryException("Api object create error", "Bad format: links is not an Array[]"));
		this.links = links;
}

/**
 * Set a Link into a Api object in a position n
 * @param link {Link[]} 
 */
Api.prototype.setLink = function(link){
	this.links = link;
};

/**
 * Get link list of Api object
 * @returns {?Link[]}
 */
Api.prototype.getLink = function() {
	return(this.links);
};



/**
 * @constructor
 * @this {DiscoveryItem}
 * @param response {DiscoveryResponse}
 * @param TTL {?String}
 * @throws {callApiDiscoveryException}  Will throw an error if the arguments has not the correct type.
 */
function DiscoveryItem(response, ttl) {
	if(isAString(ttl))
		ttl = parseInt(ttl);
	if(!isANumber(ttl))
		throw (new callApiDiscoveryException("DiscoveryItem object create error", "Bad format: ttl is not an String"));
		if(!response instanceof DiscoveryResponse)
			throw (new callApiDiscoveryException("DiscoveryItem object create error", "Bad format: response is not an Object"));
			this.response = response;
			this.ttl = ttl;
}

/**
 * Get TTL of a DiscoveryItem object
 * @returns TTL {?String}
 */
DiscoveryItem.prototype.getTtl = function() {
	return (this.ttl);
};

/**
 * Set TTL of a DiscoveryItem object
 * @param TTL {?String}
 */
DiscoveryItem.prototype.setTtl = function(ttl) {
	this.ttl = ttl;
};

/**
 * Get response of a DiscoveryItem object
 * @returns response {?DiscoveryResponse}
 */
DiscoveryItem.prototype.getResponse = function() {
	return (this.response);
};

/**
 * Set response of a DiscoveryItem object
 * @param response {DiscoveryResponse}
 */
DiscoveryItem.prototype.setResponse = function(response) {
	this.response = response;
};



/**
 * @constructor
 * @this {DiscoveryResponse}
 * @param apis {Array}
 * @param client_id {String}
 * @param client_secret {String}
 * @param country {String}
 * @param currency {String}
 * @param subscriber_operator {String}
 * @param subscriber_id {?String}
 * @throws {callApiDiscoveryException}  Will throw an error if the arguments has not the correct type.
 */
function DiscoveryResponse(apis, client_id, client_secret, country, currency, subscriber_operator, subscriber_id) {
	if(!apis instanceof Array)
		throw (new callApiDiscoveryException("DiscoveryResponse object create error", "Bad format: apis is not an Array"));
		if(!isAString(client_id))
			throw (new callApiDiscoveryException("DiscoveryResponse object create error", "Bad format: client_id is not an String"));
			if(!isAString(client_secret))
				throw (new callApiDiscoveryException("DiscoveryResponse object create error", "Bad format: client_secret is not an String"));
				if(!isAString(country))
					throw (new callApiDiscoveryException("DiscoveryResponse object create error", "Bad format: country is not an String"));
					if(!isAString(currency))
						throw (new callApiDiscoveryException("DiscoveryResponse object create error", "Bad format: currency is not an String"));
						if(!isAString(subscriber_operator))
							throw (new callApiDiscoveryException("DiscoveryResponse object create error", "Bad format: subscriber_operator is not an String"));
							this.apis = apis;
							this.client_id = client_id;
							this.client_secret = client_secret;
							this.country = country;
							this.currency = currency;
							this.subscriber_operator = subscriber_operator;
							if (subscriber_id) {
								this.subscriber_id = subscriber_id;
							} else {
								this.subscriber_id = localStorage.getObject('subscriber_id');
							}
}

/**
 * Get an Api called name of a DiscoveryResponse object
 * @param name {?String}
 * @returns apis {?Apis[]|Link[]}
 */
DiscoveryResponse.prototype.getApi = function(name) {
	if(name!=null&&this.apis!=null){
		if(name==='payment'){
			if(this.apis.payment!=undefined)
				return(this.apis.payment.link);
			else
				return(null);
		}else if(name==='operatorid'){
			if(this.apis.operatorid!=undefined)
				return(this.apis.operatorid.link);
			else
				return(null);
		}else
			return(this.apis);
	}else
		return(this.apis);
};

/**
 * Get Api array of a DiscoveryResponse object
 * @returns apis {?Array}
 */
DiscoveryResponse.prototype.getApis = function() {
	return(this.apis);
};

/**
 * Get specific function of a named API
 * @returns endpoint url {?String}
 */
DiscoveryResponse.prototype.getApiFunction = function(apiname, functionname) {
    var endpoint=null;
    if (this.apis && this.apis!=null && apiname!=null && functionname!=null) {
        var apiendpoints=this.apis[apiname];
        if (apiendpoints && apiendpoints!=null && apiendpoints.link &&
            apiendpoints.link!=null && apiendpoints.link.length>0) {
            for (i=0; i<apiendpoints.link.length && endpoint==null; i++) {
                var eppair=apiendpoints.link[i];
                if (eppair && eppair!=null && eppair.rel==functionname) {
                    endpoint=eppair.href;
                }
            }
        }
    }
    return(endpoint);
};

/**
 * Get client_id of a DiscoveryResponse object
 * @returns client_id {?String}
 */
DiscoveryResponse.prototype.getClient_id = function() {
	return(this.client_id);
};

/**
 * Set client_id of a DiscoveryResponse object
 * @param client_id {String}
 */
DiscoveryResponse.prototype.setClient_id = function(client_id) {
	this.client_id = client_id;
};

/**
 * Get client_secret of a DiscoveryResponse object
 * @returns client_secret {?String}
 */
DiscoveryResponse.prototype.getClient_secret = function() {
	return(this.client_secret);
};

/**
 * Set client_secret of a DiscoveryResponse object
 * @param client_secret {String}
 */
DiscoveryResponse.prototype.setClient_secret = function(client_secret) {
	this.client_secret = client_secret;
};

/**
 * get subscriber_operator of a DiscoveryResponse object
 * @returns subscriber_operator {?String}
 */
DiscoveryResponse.prototype.getSubscriber_operator = function() {
	return(this.subscriber_operator);
};

/**
 * Set subscriber_operator of a DiscoveryResponse object
 * @param subscriber_operator {String}
 */
DiscoveryResponse.prototype.setSubscriber_operator = function(subscriber_operator) {
	this.subscriber_operator = subscriber_operator;
};

/**
 * Get country of a DiscoveryResponse object
 * @returns country {?String}
 */
DiscoveryResponse.prototype.getCountry = function() {
	return(this.country);
};

/**
 * Set country of a DiscoveryResponse object
 * @param country {String}
 */
DiscoveryResponse.prototype.setCountry = function(country) {
	this.country = country;
};

/**
 * Get currency of a DiscoveryResponse object
 * @returns currency {?String}
 */
DiscoveryResponse.prototype.getCurrency = function() {
	return(this.currency);
};

/**
 * Set currency of a DiscoveryResponse object
 * @param currency {String}
 */
DiscoveryResponse.prototype.setCurrency = function(currency) {
	this.currency = currency;
};

/**
 * Set subscriber_id of a DiscoveryResponse object
 * @param subscriber_id {String}
 */
DiscoveryResponse.prototype.setSubscriber_id = function(subscriber_id) {
        this.subscriber_id = subscriber_id;
};

/**
 * Get subscriber_id of a DiscoveryResponse object
 * @returns subscriber_id {?String}
 */
DiscoveryResponse.prototype.getSubscriber_id = function() {
        return(this.subscriber_id);
};

//var mcc;
//var mnc;
var keyStr = "ABCDEFGHIJKLMNOP" +
"QRSTUVWXYZabcdef" +
"ghijklmnopqrstuv" +
"wxyz0123456789+/" +
"=";
var _retries = 0;
/**
 * discoveryGetFunction(authorization, mcc_mnc, accept, ipAddress)
 * Construct an send get request
 * @access private
 * @param url {String} api url location 
 * @param authorization {String} Base64 encoded username/password 
 * @param mcc_mnc {?String} Mobile Country Code and Mobile Network Code separate by a underscore 262_01
 * @param msisdn {?String} MSISDN if known by the application
 * @param ipAddress {?String} if it's not null it will be sent as a header
 * @param redirectUri {?String} location which continues the discovery process
 * @param callbackFunction {function} receive DiscoveryResponse object or an array with redirect url which could be accessed with result["operatorSelection"] or an error{error:'', error_description:''}
 */

function addParameter(root, name, value) {
    var resp=root;
    if (name && value && name!=null && value!=null) {
        if (resp.indexOf('?')>=0) {
            resp+='&';
        } else {
            resp+='?';
        }
        resp=resp+encodeURI(name)+'='+encodeURI(value);
    }
    return resp;
}

function discoveryGetFunction(url, authorization, mcc_mnc, msisdn, ipAddress, redirectUri, callbackFunction, followRedirect){
	var parameters = url;

	if(!!redirectUri)
		parameters = addParameter(parameters,'redirect_uri',redirectUri);
  
	if(!!mcc_mnc)
		parameters = addParameter(parameters, 'mcc_mnc', mcc_mnc);

	if (!!msisdn) 
		parameters = addParameter(parameters, 'msisdn', msisdn);

	var accept = 'application/json';
	var xhr = new XMLHttpRequest();
	xhr.open('GET', parameters, true);
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) { //DONE
			var result = xhr;
			if(xhr.status===200){
				var responseObj = JSON.parse(xhr.responseText);
				setDiscoveryData(responseObj);
				result = createADiscoveryItemObject(responseObj);
				callbackFunction(result);
			}else if (xhr.status===202){
				var aux = JSON.parse(xhr.responseText);
				result={};
				for ( var int = 0; int < aux.links.length; int++) {
					if(aux.links[int].rel.toLowerCase()==="operatorselection"){
						result["operatorSelection"] = aux.links[int].href;
					}
				}
				console.log('202 – Mcc & mnc not specified, redirect uri is provided to select your operator '+JSON.stringify(result));
				if(followRedirect && !!result["operatorSelection"]){
					selectOperator(result, function(mcc, mnc){
						if(mcc != null && mcc >= 0){
							discoveryGetFunction(url, authorization, mcc+"_"+mnc, msisdn, ipAddress, redirectUri, callbackFunction, false);
						}else{
							callbackFunction({"error":"1001","error_description":"The user has closed the popup window without providing an operator"});	
						}
					});	
				}else
					callbackFunction(result);				
			}else if(xhr.status===400){
				result = JSON.parse(xhr.responseText);
				console.log(result.error+' – '+result.error_description);
				callbackFunction(result);
			}else if(xhr.status===401){
				console.log('401 – Unauthorized');
				console.log(result.error+' – '+result.error_description);
				result = JSON.parse(xhr.responseText);
				callbackFunction(result);
			}else if(xhr.status===404){
				console.log('404 – Not found: mistake in the host or path of the service URI');
				console.log(result.error+' – '+result.error_description);
				result = JSON.parse(xhr.responseText);
				console.log(redirectUri);
				callbackFunction(result);
			}else if(xhr.status===503){
				console.log('503 – Server busy, server error or service unavailable. Please retry the request');
				console.log(result.error+' – '+result.error_description);
				if(_retries<10){
					setTimeout(function(){
						_retries++;
						discoveryGetFunction(authorization, mcc_mnc, accept, ipAddress);
					}, 1000);
				}else{
					result = {"error":"503","error_description":"Server busy, server error or service unavailable. Please retry the request"};
					callbackFunction(result);
				}
			}else{
				console.log(xhr.status+" --- "+xhr.responseText);
				if(xhr.responseText!='')
					result = JSON.parse(xhr.responseText);
				else
					result = {"error":"0","error_description":"no description received"};
				callbackFunction(result);
			}
		}
	};
	xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xhr.setRequestHeader("Authorization", authorization);
	xhr.setRequestHeader('Accept',accept);
	if(ipAddress!=null)
		xhr.setRequestHeader('x-source-ip', ipAddress);
	xhr.send();
}

/**
 * selectOperator
 * @param result {Array}
 * @param callbackFunction {function}
 */
function selectOperator(result, callbackFunction){
	var ancho = 400;
	var alto = 400;
	var posicion_x; 
	var posicion_y; 
	posicion_x=(screen.width/2)-(ancho/2); 
	posicion_y=(screen.height/2)-(alto/2); 
	var aux = window.open(result["operatorSelection"], "selectionOperator", "width="+ancho+",height="+alto+",menubar=0,toolbar=0,directories=0,scrollbars=no,resizable=no,left="+posicion_x+",top="+posicion_y+"");
	var eventListener = function(m){
		window.clearInterval(interval);
		removeEventListener("message", eventListener);
		if(m.data.indexOf('mcc_mnc')<0){
			callbackFunction(-1,-1);			
		}else{
			var mcc_mnc=getValueFromUrl(m.data, "mcc_mnc");
                        var subscriber_id=getValueFromUrl(m.data, "subscriber_id");
			//var mcc_mnc = m.data.substring(m.data.indexOf('mcc_mnc=')+8,m.data.length);
			aux.close();
			if(!!localStorage.getObject('mcc_mnc'))
				localStorage.removeItem('mcc_mnc');
			localStorage.setObject('mcc_mnc',mcc_mnc);
                        if(!!localStorage.getObject('subscriber_id'))
                                localStorage.removeItem('subscriber_id');
                        localStorage.setObject('subscriber_id',subscriber_id);

			callbackFunction(getMccFromCombined(mcc_mnc),getMncFromCombined(mcc_mnc));
		}
	};
	addEventListener("message", eventListener,false);
	var interval = setInterval(function(){
		if(!aux.closed){
			aux.postMessage(window.location.href, "*");
		}else{
			window.clearInterval(interval);
			callbackFunction(-1,-1);
		}
	}, 1000);	
}
/**
 * Obtain the mcc and mnc values in 'mcc_mnc' format from URL
 * @returns mcc_mnc{?String}
 */
function getMccAndMncFromUrl() {
	var mcc_mnc = null;
	var m = window.location.href;
	if(m.data.indexOf('mcc_mnc')>=0){
		mcc_mnc = m.data.substring(m.data.indexOf('mcc_mnc=')+8,m.data.length);
	}
	return mcc_mnc;
}

function getValueFromUrl(url, paramName) {
    var paramValue=null;
    if (url && paramName && url.indexOf("?")>=0) {
        var pos=url.indexOf("?");
        var query = url.substring(pos+1);
        var vars = query.split('&');
        var finished = false;
        for (var i = 0; i < vars.length && !finished; i++) {
            var pair = vars[i].split('=');
            if (decodeURIComponent(pair[0]) == paramName) {
                paramValue=decodeURIComponent(pair[1]);
                finished=true;
            }
        }
    }
    return paramValue;
}

/**
 * Call a ‘Discovery’ API to identify the network operator.
 * The discovery API results are locally cacheable for a period of time identified by a time to live value in the response.
 * @param serviceUri {String} Api location.
 * @param consumerKey {String} Username used for authorisation of the API requests with the OneAPI Exchange server.
 * @param consumerSecret {String} Password used for authorisation of the API requests with the OneAPI Exchange server.
 * @param encrypt {String} (none, basic or sha-256) Credentials usually sent using HTTP Basic auth over HTTPS connection.
 * @param mcc {?String} Mobile Country Code
 * @param mnc {?String} Mobile Network Code 
 * @param msisdn {?String} MSISDN if known by the application
 * @param sourceIP {?String} Source IP (of the client application if this request is invoked server side)
 * @param redirectUri {?String} Specifies location which continues the discovery process
 * @param callbackFunction {function} It will be called with result as single param. The result could be a DiscoveryItem object or an array with redirect url which could be accessed with result["operatorSelection"] or an error {error: String, error_description: String}
 */
function getDiscoveryPassive(serviceUri, consumerKey, consumerSecret, encrypt, mcc, mnc, msisdn, sourceIP, redirectUri, callbackFunction) {
	//Get MCC
	var authorization = consumerKey+":"+consumerSecret;
	if(encrypt)
		encrypt = encrypt.toLowerCase();
	
	discoveryGetFunction(serviceUri,getAuthorizationToCallGetDiscovery(encrypt,authorization),getMCCandMNCToCallGetDiscovery(mcc,mnc), msisdn, sourceIP,redirectUri, callbackFunction);
}
/**
 * Call a ‘Discovery’ API to identify the network operator.
 * The discovery API results are locally cacheable for a period of time identified by a time to live value in the response.
 * @param serviceUri {String} Api location.
 * @param consumerKey {String} Username used for authorisation of the API requests with the OneAPI Exchange server.
 * @param consumerSecret {String} Password used for authorisation of the API requests with the OneAPI Exchange server.
 * @param encrypt {String} (none, basic or sha-256) Credentials usually sent using HTTP Basic auth over HTTPS connection.
 * @param mcc {?String} Mobile Country Code
 * @param mnc {?String} Mobile Network Code 
 * @param msisdn {?String} MSISDN if known by the application
 * @param sourceIP {?String} Source IP (of the client application if this request is invoked server side)
 * @param redirectUri {?String} Specifies location which continues the discovery process
 * @param callbackFunction {function} It will be called with result as single param. The result could be a DiscoveryItem object or an error {error: String, error_description: String}
 */
function getDiscoveryActive(serviceUri, consumerKey, consumerSecret, encrypt, mcc, mnc, msisdn, sourceIP, redirectUri, callbackFunction) {
	//Get MCC
	var authorization = consumerKey+":"+consumerSecret;
	if(encrypt)
		encrypt = encrypt.toLowerCase();
	
	discoveryGetFunction(serviceUri,getAuthorizationToCallGetDiscovery(encrypt,authorization),getMCCandMNCToCallGetDiscovery(mcc,mnc), msisdn, sourceIP, redirectUri, callbackFunction, true);
}

/**
 * Handles the second stage of discovery after 'Active' mode has required the user to confirm their MSISDN/ network
 * @access private
 * @param serviceUri {String} Api location.
 * @param consumerKey {String} Username used for authorisation of the API requests with the OneAPI Exchange server.
 * @param consumerSecret {String} Password used for authorisation of the API requests with the OneAPI Exchange server.
 * @param encrypt {String} (none, basic or sha-256) Credentials usually sent using HTTP Basic auth over HTTPS connection.
 * @param callbackFunction {function} It will be called with result as single param. The result should be a DiscoveryItem object
 */
function completeDiscovery(serviceUri, consumerKey, consumerSecret, encrypt, callbackFunction) {
        var authorization = consumerKey+":"+consumerSecret;
        encrypt = encrypt.toLowerCase();

        // Get MCC/MNC from window.location
        var mcc = null;
        var mnc = null;

        var query = window.location.search.substring(1);
        var vars = query.split('&');
        var finished = false;
        var mcc_mnc = null;
        for (var i = 0; i < vars.length && !finished; i++) {
                var pair = vars[i].split('=');
                if (decodeURIComponent(pair[0]) == 'mcc_mnc') {
                        mcc_mnc=decodeURIComponent(pair[1]);
                        var mcc_mnc_pair = mcc_mnc.split("_");
                        if (mcc_mnc_pair[0] && mcc_mnc_pair[1]) {
                                mcc=mcc_mnc_pair[0];
                                mnc=mcc_mnc_pair[1];
                                finished=true;
                        }
                }
        }

        if (mcc && mnc) {
                discoveryGetFunction(serviceUri,getAuthorizationToCallGetDiscovery(encrypt,authorization),getMCCandMNCToCallGetDiscovery(mcc,mnc),null, null,null, callbackFunction,false);
        } else {
                throw (new callApiDiscoveryException("completeDiscovery", "unable to identify MCC/MNC"));
        }
}

function getAuthorizationToCallGetDiscovery(encrypt,authorization){
	if(encrypt==='basic'){
		authorization = 'Basic '+base64_encode(authorization);
	}else if(encrypt==='sha-256'){
		authorization = 'Basic '+sha256_encode(authorization);
	} else if (encrypt==='none') {
		authorization = '';
	}else {
		authorization = 'Basic '+(authorization);
	}		
	return(authorization);
}
function getMCCandMNCToCallGetDiscovery(mcc,mnc) {
	var mcc_mnc = null;
	if(!!mcc && !!mnc){
		mcc_mnc = mcc+"_"+mnc;
		if(!!localStorage.getObject('mcc_mnc'))
			localStorage.removeItem('mcc_mnc');
		localStorage.setObject('mcc_mnc',mcc_mnc);
	}else{
		getMccAndMnc();
		if(localStorage.getObject('mcc_mnc')){
			mcc_mnc = localStorage.getObject('mcc_mnc');
		}else if(document.cookie.indexOf('mcc_mnc=')>=0){
			mcc_mnc = document.cookie.substr(document.cookie.indexOf('mcc_mnc=')+8,document.cookie.length);
		}		
	}
	return(mcc_mnc);	
}
/**
 * createADiscoveryItemObject
 * @access private
 * @param json {Object}
 * @returns {DiscoveryItem|String} 
 * @throws {callApiDiscoveryException}
 */
function createADiscoveryItemObject(json) {
	var links = new Array();
	try{
        var apis=json.response.apis;
		var discoveryResponse = new DiscoveryResponse(apis, json.response.client_id, json.response.client_secret, json.response.country, json.response.currency, json.response.subscriber_operator, json.response.subscriber_id);
		var discoveryItem = new DiscoveryItem(discoveryResponse, json.ttl);
		return (discoveryItem);
	}catch (aux) {
		console.log(aux.name+" : "+aux.message);
		throw (new callApiDiscoveryException(aux.name,aux.description));
	}
}

/**
 * @access private
 * @event
 * @param key {String}
 * @param value {String}
 */
Storage.prototype.setObject = function(key, value) {
	this.setItem(key, JSON.stringify(value));
};

/**
 * @access private
 * @event
 * @param key {String}
 * @returns value {Object} 
 */
Storage.prototype.getObject = function(key) {
	return JSON.parse(this.getItem(key));
};

/**
 * Get the discovery API results cached if exits and is not expired.
 * @returns data {?DiscoveryItem}
 */
function getCacheDiscoveryItem() {
	var discoveryData = localStorage.getObject('discoveryData');
	if(!discoveryData)
		return (null);
	discoveryData = JSON.parse(discoveryData);
	var d = new Date();
    var tsec = d.getTime()/1000;
	if( tsec <= discoveryData.ttl )
		return (createADiscoveryItemObject(discoveryData));
	else{
		localStorage.removeItem('discoveryData');
		return(null);
	}
}

/**
 * saveDiscoveryData
 * @access private
 * @param data {json|DiscoveryItem}
 * @throws {callApiDiscoveryException}  Will throw an error if the arguments has not the correct type.
 */
function setDiscoveryData(data) {
	var discoveryData = localStorage.getObject('discoveryData');
	if(!data)
		throw (new callApiDiscoveryException("DiscoveryData saving error", "No data to save: received null param"));
		data = JSON.stringify(data);
		if(!!discoveryData)
			localStorage.removeItem('discoveryData');
		localStorage.setObject('discoveryData',data);
}

/**
 * Remove saved data about mcc, mnc & discovery API results.
 */
function clearCacheDiscoveryItem() {
	var discoveryData = localStorage.getObject('discoveryData');
	if(!!discoveryData)
		localStorage.removeItem('discoveryData');
	if(!!localStorage.getObject('mcc_mnc'))
		localStorage.removeItem('mcc_mnc');
}
/**
 * getMccAndMnc()
 * get MCC and MNC for FirefoxOS systems 
 * @access private
 */
function getMccAndMnc() {
}

function base64_encode(data) {
	var b64 = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
	var o1, o2, o3, h1, h2, h3, h4, bits, i = 0,
	ac = 0,
	enc = '',
	tmp_arr = [];
	if (!data) {
		return data;
	}
	do { // pack three octets into four hexets
		o1 = data.charCodeAt(i++);
		o2 = data.charCodeAt(i++);
		o3 = data.charCodeAt(i++);
		bits = o1 << 16 | o2 << 8 | o3;
		h1 = bits >> 18 & 0x3f;
		h2 = bits >> 12 & 0x3f;
		h3 = bits >> 6 & 0x3f;
		h4 = bits & 0x3f;
		// use hexets to index into b64, and append result to encoded string
		tmp_arr[ac++] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4);
	} while (i < data.length);
	enc = tmp_arr.join('');
	var r = data.length % 3;
	return (r ? enc.slice(0, r - 3) : enc) + '==='.slice(r || 3);
}

function sha256_encode (msg) {
	function rotate_left(n,s) {
		var t4 = ( n<<s ) | (n>>>(32-s));
		return t4;
	}; 
	function lsb_hex(val) {
		var str="";
		var i;
		var vh;
		var vl; 
		for( i=0; i<=6; i+=2 ) {
			vh = (val>>>(i*4+4))&0x0f;
			vl = (val>>>(i*4))&0x0f;
			str += vh.toString(16) + vl.toString(16);
		}
		return str;
	}; 
	function cvt_hex(val) {
		var str="";
		var i;
		var v; 
		for( i=7; i>=0; i-- ) {
			v = (val>>>(i*4))&0x0f;
			str += v.toString(16);
		}
		return str;
	}; 
	function Utf8Encode(string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = ""; 
		for (var n = 0; n < string.length; n++) { 
			var c = string.charCodeAt(n); 
			if (c < 128) {
				utftext += String.fromCharCode(c);
			} else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}  else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
		}
		return utftext;
	}; 
	var blockstart;
	var i, j;
	var W = new Array(80);
	var H0 = 0x67452301;
	var H1 = 0xEFCDAB89;
	var H2 = 0x98BADCFE;
	var H3 = 0x10325476;
	var H4 = 0xC3D2E1F0;
	var A, B, C, D, E;
	var temp; 
	msg = Utf8Encode(msg); 
	var msg_len = msg.length; 
	var word_array = new Array();
	for( i=0; i<msg_len-3; i+=4 ) {
		j = msg.charCodeAt(i)<<24 | msg.charCodeAt(i+1)<<16 |
		msg.charCodeAt(i+2)<<8 | msg.charCodeAt(i+3);
		word_array.push( j );
	} 
	switch( msg_len % 4 ) {
	case 0:
		i = 0x080000000;
		break;
	case 1:
		i = msg.charCodeAt(msg_len-1)<<24 | 0x0800000;
		break;

	case 2:
		i = msg.charCodeAt(msg_len-2)<<24 | msg.charCodeAt(msg_len-1)<<16 | 0x08000;
		break;

	case 3:
		i = msg.charCodeAt(msg_len-3)<<24 | msg.charCodeAt(msg_len-2)<<16 | msg.charCodeAt(msg_len-1)<<8    | 0x80;
		break;
	} 
	word_array.push( i ); 
	while( (word_array.length % 16) != 14 ) word_array.push( 0 );
	word_array.push( msg_len>>>29 );
	word_array.push( (msg_len<<3)&0x0ffffffff );
	for ( blockstart=0; blockstart<word_array.length; blockstart+=16 ) {
		for( i=0; i<16; i++ ) W[i] = word_array[blockstart+i];
		for( i=16; i<=79; i++ ) W[i] = rotate_left(W[i-3] ^ W[i-8] ^ W[i-14] ^ W[i-16], 1);
		A = H0;
		B = H1;
		C = H2;
		D = H3;
		E = H4;
		for( i= 0; i<=19; i++ ) {
			temp = (rotate_left(A,5) + ((B&C) | (~B&D)) + E + W[i] + 0x5A827999) & 0x0ffffffff;
			E = D;
			D = C;
			C = rotate_left(B,30);
			B = A;
			A = temp;
		}
		for( i=20; i<=39; i++ ) {
			temp = (rotate_left(A,5) + (B ^ C ^ D) + E + W[i] + 0x6ED9EBA1) & 0x0ffffffff;
			E = D;
			D = C;
			C = rotate_left(B,30);
			B = A;
			A = temp;
		} 
		for( i=40; i<=59; i++ ) {
			temp = (rotate_left(A,5) + ((B&C) | (B&D) | (C&D)) + E + W[i] + 0x8F1BBCDC) & 0x0ffffffff;
			E = D;
			D = C;
			C = rotate_left(B,30);
			B = A;
			A = temp;
		} 
		for( i=60; i<=79; i++ ) {
			temp = (rotate_left(A,5) + (B ^ C ^ D) + E + W[i] + 0xCA62C1D6) & 0x0ffffffff;
			E = D;
			D = C;
			C = rotate_left(B,30);
			B = A;
			A = temp;
		} 
		H0 = (H0 + A) & 0x0ffffffff;
		H1 = (H1 + B) & 0x0ffffffff;
		H2 = (H2 + C) & 0x0ffffffff;
		H3 = (H3 + D) & 0x0ffffffff;
		H4 = (H4 + E) & 0x0ffffffff;
	} 
	var temp = cvt_hex(H0) + cvt_hex(H1) + cvt_hex(H2) + cvt_hex(H3) + cvt_hex(H4);
	return temp.toLowerCase();

}

/**
 * @constructor
 * @param name {?String}
 * @param description {?String}
 * @returns {callApiDiscoveryException}
 */
function callApiDiscoveryException(name, description) {
	this.name = name;
	this.description = description;
}

/**
 * Get name of callApiDiscoveryException Object
 * @returns {String}
 */
callApiDiscoveryException.prototype.getName = function() {
	return (this.name);
};

/**
 * Get description of callApiDiscoveryException object
 * @returns {String}
 */
callApiDiscoveryException.prototype.getDescription = function() {
	return (this.description);
};

/**
 * getMccFromCombined
 * @param mcc_mnc {String}
 * @returns mcc {String}
 */
function getMccFromCombined (mcc_mnc){
	return (mcc_mnc.substr(0,mcc_mnc.indexOf('_')));
}

/**
 * getMncFromCombined
 * @param mcc_mnc {String}
 * @returns mnc {String}
 */
function getMncFromCombined (mcc_mnc){
	return (mcc_mnc.substr(1+mcc_mnc.indexOf('_'),mcc_mnc.length));
}
/**
 * helperRedirectMccMnc
 */
function helperRedirectMccMnc() {
	if(!!window.opener && !window.opener.closed){
		addEventListener("message",function(m){
    		window.opener.postMessage(window.location.href, m.data);
		},false);
	}
}
