if (typeof console == "undefined" || typeof console.log == "undefined") var console = { log: function() {} };
/**
 * @constructor
 * @this {LogoItem}
 * @param operatorId {?String}
 * @param apiName {?String}
 * @param language {?String}
 * @param size {?String}
 * @param height {?String}
 * @param width {?String}
 * @param action {?String}
 * @param url {?String}
 * @param bgColourRange {?String}
 * @param bgColor {?String}
 * @param aspectRatio {?String}
 * @throws {callApiLogoException} Will throw an error if the arguments has not the correct type.
 */
function LogoItem(operatorId, apiName, language, size, height, width, action, url, bgColourRange, bgColor, aspectRatio) {
	if((!isAString(operatorId))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'operatorId: Bad format: apis is not an String'));
	if((!isAString(apiName))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'apiName: Bad format: apis is not an String'));
	if((!isAString(language))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'language: Bad format: apis is not an String'));
	if((!isAString(size))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'size: Bad format: apis is not an String'));
	if((!isAString(height))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'height: Bad format: apis is not an String'));
	if((!isAString(width))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'width: Bad format: apis is not an String'));
	if((!isAString(action))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'action: Bad format: apis is not an String'));
	if((!isAString(url))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'url: Bad format: apis is not an String'));
	if((!isAString(bgColourRange))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'bgColourRange: Bad format: apis is not an String'));
	if((!isAString(aspectRatio))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'aspectRatio: Bad format: apis is not an String'));
	if((!isAString(bgColor))&&(!null))
		throw (new callApiLogoException("LogoItem object create error", 'bgColor: Bad format: apis is not an String'));
	this.operatorId = operatorId;
	this.apiName = apiName;
	this.language = language;
	this.size = size;
	this.height = height;
	this.width = width;
	this.action = action;
	this.url = url;
	this.bgColourRange = bgColourRange;
	this.bgColor = bgColor;
	this.aspectRatio = aspectRatio;
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
 * 
 * Get operatorId of a LogoItem object
 * @returns {?String} operatorId
 */
LogoItem.prototype.getOperatorId = function() {
	return(this.operatorId);
};

/**
 * 
 * Set operatorId of a LogoItem object
 * @param {?String} operatorId
 */
LogoItem.prototype.setOperatorId = function(operatorId) {
	this.operatorId = operatorId; 
};

/**
 * 
 * Get apiName of a LogoItem object
 * @returns {?String} apiName
 */
LogoItem.prototype.getApiName = function() {
	return(this.apiName);
};

/**
 * 
 * Set apiName of a LogoItem object
 * @param {?String} apiName
 */
LogoItem.prototype.setApiName = function(apiName) {
	this.apiName = apiName; 
};

/**
 * 
 * Get languague of a LogoItem object
 * @returns {?String} languague
 */
LogoItem.prototype.getLanguague = function() {
	return(this.language);
};

/**
 * 
 * Set languague of a LogoItem object
 * @param {?String} languague
 */
LogoItem.prototype.setLanguague = function(languague) {
	this.language = languague; 
};

/**
 * 
 * Get size of a LogoItem object
 * @returns {?String} size
 */
LogoItem.prototype.getSize = function() {
	return(this.size);
};

/**
 * 
 * Set size of a LogoItem object
 * @param {String} size
 */
LogoItem.prototype.setSize = function(size) {
	this.size = size;
};

/**
 * 
 * Get height of a LogoItem object
 * @returns {?String} height
 */
LogoItem.prototype.getHeight = function() {
	return(this.height);
};

/**
 * 
 * Get aspect ratio of a LogoItem object
 * @returns {?String} aspectRatio
 */
LogoItem.prototype.getAspectRatio = function() {
	return(this.aspectRatio);
};

/**
 * 
 * Get color of a LogoItem object
 * @returns {?String} bgColor
 */
LogoItem.prototype.getBgColor = function() {
	return(this.bgColor);
};
/**
 * 
 * Set background color range of a LogoItem object
 * @param {String} bgColourRange
 */
LogoItem.prototype.setBgColor = function(bgColor) {
	this.bgColor = bgColor;
};

/**
 * 
 * Set aspect ratio range of a LogoItem object
 * @param {String} aspectRatio
 */
LogoItem.prototype.setAspectRatio = function(aspectRatio) {
	this.aspectRatio = aspectRatio;
};

/**
 * 
 * Set height of a LogoItem object
 * @param {String} height
 */
LogoItem.prototype.setHeight = function(height) {
	this.height = height;
};

/**
 * 
 * Get width of a LogoItem object
 * @returns {?String} width
 */
LogoItem.prototype.getWidth = function() {
	return(this.width);
};

/**
 * 
 * Set width of a LogoItem object
 * @param {String} width
 */
LogoItem.prototype.setWidth = function(width) {
	this.width = width;
};

/**
 * 
 * Get action of a LogoItem object
 * @returns {?String} action
 */
LogoItem.prototype.getAction = function() {
	return(this.action);
};

/**
 * 
 * Set action of a LogoItem object
 * @param {String} action
 */
LogoItem.prototype.setAction = function(action) {
	this.action = action;
};

/**
 * 
 * Get url of a LogoItem object
 * @returns {?String} url
 */
LogoItem.prototype.getUrl = function() {
	return(this.url);
};

/**
 * 
 * Set url of a LogoItem object
 * @param {?String} url
 */
LogoItem.prototype.setUrl = function(url) {
	this.url = url;
};

/**
 * 
 * Get bgColourRange of a LogoItem object
 * @returns {?String} bgColourRange
 */
LogoItem.prototype.getBgColourRange = function() {
	return(this.bgColourRange);
};

/**
 * 
 * Set bgColourRange of a LogoItem object
 * @param {?String} bgColourRange
 */
LogoItem.prototype.setBgColourRange = function(bgColourRange) {
	this.bgColourRange = bgColourRange;
};

/**
 * @constructor
 * @param name {?String}
 * @param description {?String}
 * @returns {callApiLogoException}
 */
function callApiLogoException(name, description) {
	this.name = name;
	this.description = description;
}

/**
 * Get name of callApiLogoException Object
 * @returns {String}
 */
callApiLogoException.prototype.getName = function() {
	return (this.name);
};

/**
 * Get description of callApiLogoException object
 * @returns {String}
 */
callApiLogoException.prototype.getDescription = function() {
	return (this.description);
};

/**
 * @constructor
 * @this {LogoItemArray}
 * @param logos {LogoItem[]}
 */
function LogoItemArray(logos) {
	this.logos = logos;
}

/**
 * 
 * Get logos of a LogoItemArray object
 * @returns {LogoItem[]}
 */
LogoItemArray.prototype.getLogos = function(){
	return(this.logos);
};

/**
 * 
 * Set logos of a LogoItemArray object
 * @param logos {LogoItem[]}
 */
LogoItemArray.prototype.setLogos = function(logos){
	this.logos = logos;
};



/**
 * logoGetFunction(url, mcc_mnc, ipAddress, callbackFunction)
 * @access private
 * @param url {String}
 * @param mcc_mnc {String}
 * @param ipAddress {?String}
 * @param logosize {?String}
 * @param bg_color {?String}
 * @param aspect_ratio {?String}
 * @param callbackFunction {Function}
 */
function logoGetFunction(url, mcc_mnc, ipAddress, apiname, logosize, bg_color, aspect_ratio,  callbackFunction){
	var parameters = "?mcc_mnc=" + mcc_mnc;
	if(!!logosize)
		parameters += "&logosize=" + logosize.toLowerCase();
	if(!!bg_color)
		parameters += "&bg_color=" + bg_color.toLowerCase();
	if(!!aspect_ratio)
		parameters += "&aspect_ratio=" + aspect_ratio.toLowerCase();
    var extrapath='';
    if (!!apiname) {
        extrapath="/apis/"+apiname;
    }
	
	var xhr = new XMLHttpRequest();
    var requesturl=url+extrapath+parameters;
	xhr.open('GET', requesturl, true);
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) { //DONE
			var result;
			if(xhr.status===200){
				result = createALogoItemObject(JSON.parse(xhr.response));
				setSavedLogoData(xhr.response);
			}else if(xhr.responseText!='')
				result = JSON.parse(xhr.responseText);
			else
				result = { "error":"0" , "error_description":"no description received" };
			console.log(xhr.status+" --- "+xhr);
			callbackFunction(result);
		}
	};
	xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	xhr.setRequestHeader('Accept', "application/json");
	if(ipAddress!=null)
		xhr.setRequestHeader('x-source-ip', ipAddress);
	xhr.send();
}
 
/**
 * Get operator specific logo if operator discovered, or a generic one if operator not discovered.
 * @param serviceUri {String} Api location.
 * @param mcc {?String} Mobile Country Code
 * @param mnc {?String} Mobile Network Code 
 * @param sourceIP {?String} Source IP
 * @param logosize {?String} (small, medium or large) 
 * @param bg_color {?String} logo colors (normal|reversed)
 * @param aspect_ratio {?String} logo layouts (square|landscape)
 * @param callbackFunction {function} It will be called with LogoItemArray result as single param.
 */
function getLogo(serviceUri, mcc, mnc, sourceIP, apiname, logosize, bg_color, aspect_ratio, callbackFunction) {
	var mcc_mnc = "_";
	if(mcc && mnc){
		mcc_mnc = mcc+"_"+mnc;
	}else if(document.cookie.indexOf('mcc_mnc=')>=0){
		mcc_mnc = document.cookie.substr(document.cookie.indexOf('mcc_mnc=')+8,document.cookie.length);
	}
	logoGetFunction(serviceUri, mcc_mnc, sourceIP, apiname, logosize, bg_color, aspect_ratio,  callbackFunction);
}

/**
 * createALogoItemObject
 * @access private
 * @param json {json}
 * @returns {LogoItemArray}
 * @throws {callApiLogoException}
 */
function createALogoItemObject(json) {	
	try{	
		var logos = new Array();
		for ( var int = 0; int < json.length; int++) { 
			logos[int] = new LogoItem(json[int].operatorId, json[int].apiName, json[int].language, json[int].size, json[int].height, json[int].width, json[int].action, json[int].url, json[int].bgColourRange, json[int].bgColor, json[int].aspectRatio);
		}
		return(new  LogoItemArray(logos));
	}catch (aux) {
		throw (new callApiLogoException(aux.getName(), aux.getDescription()));
	}
}
/**
 * getCacheApiLogo
 * Get url of operator api image
 * @param operatorName {String}
 * @param api {String} type (payment or operatorid)
 * @param logosize {String} size (Small, Medium or Large) 
 * @param bg_color {String} logo colors (normal|reversed|black)
 * @param aspect_ratio {String} logo layouts (square|landscape)
 * @return Url image if exists {?String}
 */
function getCacheApiLogo(api,logosize,bg_color,aspect_ratio) {
	if(operatorName)
		operatorName = operatorName.toLowerCase();
	if(logosize)
		logosize = logosize.toLowerCase();
	if(api)
		api = api.toLowerCase();
	if(bg_color)
		bg_color = bg_color.toLowerCase();
	if(aspect_ratio)
		aspect_ratio = aspect_ratio.toLowerCase();
	var logoData = getCacheLogo();
	if(!!logoData){
		for (var int = 0; int < logoData.logos.length; int++) {
			if(		(!operatorName || (!!operatorName &&(logoData.logos[int].operatorId)))&&
					(!api || (!!api &&(logoData.logos[int].apiName===api)))&&
					(!logosize || (!!logosize &&(logoData.logos[int].size===logosize)))&&
					(!aspect_ratio || (!!aspect_ratio && (aspect_ratio===logoData.logos[int].aspectRatio)))&&
					(!bg_color || (!!bg_color && (bg_color===logoData.logos[int].bgColor)))	
				)
				return(logoData.logos[int].url);
		}
	}
	return(null);
}

/**
 * Get cache logo saved
 * @returns {?LogoItemArray}
 */
function getCacheLogo() {
	var logoData = localStorage.getObject('logoData');
	if(!logoData)
		return (null);
	logoData = JSON.parse(logoData);
	return(createALogoItemObject(logoData));
}

/**
 * setSavedLogoData
 * @access private
 * @param logoData {json}
 */
function setSavedLogoData(logoData) {
	if(!!localStorage.getObject('logoData'))
		localStorage.removeItem('logoData');
	localStorage.setObject('logoData',logoData);
}

/**
 * Revome saved data about logo
 */
function clearCacheLogo() {
	if(!!localStorage.getObject('logoData'))
		localStorage.removeItem('logoData');
}

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
