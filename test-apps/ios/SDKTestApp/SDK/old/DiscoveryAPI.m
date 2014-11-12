//
//  DiscoveryAPI.m
//  GSMAIOSSDK
//
//  Created by Hugo Galan on 18/06/14.
//  Copyright (c) 2014 Solaiemes S.L. All rights reserved.
//

//API CALLS
#import "DiscoveryAPI.h"
#import "Utils.h"

@implementation DiscoveryAPI

@synthesize discoveryDelegate;

//API FUNCTIONS


- (void)getDiscoveryActive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn webview: (UIWebView *) webview {
    
    if (!utils) {
        utils = [[Utils alloc]init];
        utils.traceMode=_traceMode;
    }
    
    //UrlRequest
    NSMutableURLRequest *request;
    
    
    // - - URL PARAMETERS - -
    
    NSString* requestUrl = url;
    
    // - MCC MNC -
    NSString* mcc = [utils getMCC];
    NSString* mnc = [utils getMNC];
    
    self.redirectUri=redirectUri;
    self.clientId=clientId;
    self.clientSecret=clientSecret;
    self.discoveryurl=url;
    self.encryption=encryption;
    
    if (mcc && mnc) {
        if (_traceMode) NSLog(@"Found mcc and mnc info in the device.");
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        [utils saveMCCtoCache:mcc];
        [utils saveMNCtoCache:mnc];
    }else{
        if (_traceMode) NSLog(@"Not mcc or mnc info found in device. Looking for it in mem cache...");
        //Not network info available. Cached data?
        mcc = [utils getCachedMCC];
        mnc = [utils getCachedMNC];
        
        if (mcc && mnc) {
            if (_traceMode) NSLog(@"Found mcc and mnc info in mem cache...");
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        }else{
            if (_traceMode) NSLog(@"ERROR obtaining the MCC MNC. Not param will be included in the request");
        }
    }
    
    // - MSISDN -
    if (msisdn) {
        msisdn = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&msisdn=%@", [Utils urlEncode: msisdn ]]];
    }
    
    // - REDIRECT URI -
    if (redirectUri) {
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&redirect_uri=%@", [Utils urlEncode: redirectUri ]]];
    }
    
    @try {
        //Replace first occurrence of '&'
        NSRange firstRange = [requestUrl rangeOfString:@"&"];
        requestUrl = [requestUrl stringByReplacingOccurrencesOfString:@"&" withString:@"?" options:0 range:firstRange];
    }
    @catch (NSException *exception) {
        //NSLog(@"Exception"); //Not params included in the request case
    }
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    // - - REQUEST CREATION - -
    
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"GET"];
    
    // - - HEADERS - -
    
    // Set content-type header
    NSString* encodedString = [Utils getEncryption:encryption client:clientId key:clientSecret traceMode:_traceMode];
    [request setValue:encodedString forHTTPHeaderField:@"Authorization"];
    
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    //IpAdresss
    if (ipAddress) {
        [request setValue:ipAddress forHTTPHeaderField:@"x-source-ip"];
    }
    
    NSString *uaString = [webview stringByEvaluatingJavaScriptFromString:@"navigator.userAgent"];
    [request setValue:uaString forHTTPHeaderField:@"User-agent"];
 
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        int responseCode=(int) [urlResponse statusCode];
        if (_traceMode) NSLog(@"Response code %d data %@",responseCode, data);
        
        //Check request process result
        if (error){//Error case in the request process.
            
            //NSLog(@"Error received from server,%@", error);
            
            if (data==nil) {
                [[self discoveryDelegate] getDiscoveryError:(long)responseCode data:nil];
            } else {
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
                //Error parting JSON?
                if( parseError ) {
                
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    }else{
                        NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil];
                    }
                
                }else{//Not error parsing JSON in error response from server
                
                    if ([urlResponse  statusCode] == 0 ) {
                        NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:jsonResponseObj ];
                    }else{
                        NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                }
            }
            
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    
                }else{
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil ];
                    
                }
                
            }else {
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog(@"%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoverySuccessful:-1 data:jsonResponseObj];
                    
                }else if ([urlResponse  statusCode] == 302 ||[urlResponse  statusCode] == 202 ) {//Redirect URI open broser
                    
                    if (webview!=nil) {
                        @try {
                            NSArray* links = [jsonResponseObj objectForKey:@"links"];
                            if (_traceMode) NSLog(@"Active getDiscovery mode: Redirecting to... %@",[[links firstObject]objectForKey:@"href"]);
                        
                            dispatch_sync(dispatch_get_main_queue(), ^{
                                webview.hidden=FALSE;
                                [webview loadRequest: [NSURLRequest requestWithURL: [NSURL URLWithString:[[links firstObject]objectForKey:@"href"]] ] ];
                            });
                        }
                        @catch (NSException *exception) {
                            NSLog(@"Active mode: Error opening browser, bad json object format.Not redirect href found.");
                        }
                    } else {
                        [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                
                }else{//JSON response body and status code provided
                    
                    if ([urlResponse  statusCode] == 200 ) {
                        //Save dictionaryResponse to persistent mem
                        [utils saveLastDiscoveryResponse:jsonResponseObj];
                    }
                    [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    
                }
            }
            
            
        }
        
    }];
    
}



- (void)getDiscoveryActive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn mcc:(NSString*)mcc mnc:(NSString*)mnc webview: (UIWebView *) webview {

    
    if (!utils) {
        utils = [[Utils alloc]init];
        utils.traceMode=_traceMode;
    }
    
    //UrlRequest
    NSMutableURLRequest *request;
    
    
    // - - URL PARAMETERS - -
    
    NSString* requestUrl = url;
    
    self.redirectUri=redirectUri;
    self.clientId=clientId;
    self.clientSecret=clientSecret;
    self.discoveryurl=url;
    self.encryption=encryption;

    if (mcc && mnc) {
        if (_traceMode) NSLog(@"mcc and mnc provided");
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        [utils saveMCCtoCache:mcc];
        [utils saveMNCtoCache:mnc];
    }else{
        if (_traceMode) NSLog(@"Not mcc or mnc not provided. Looking for it in mem cache...");
        //Not network info available. Cached data?
        mcc = [utils getCachedMCC];
        mnc = [utils getCachedMNC];
        
        if (mcc && mnc) {
            if (_traceMode) NSLog(@"Found mcc and mnc info in mem cache...");
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        }else{
            if (_traceMode) NSLog(@"ERROR obtaining the MCC MNC. Not param will be included in the request");
        }
    }
    
    // - MSISDN -
    if (msisdn) {
        msisdn = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&msisdn=%@", [Utils urlEncode: msisdn] ]];
    }
    
    // - REDIRECT URI -
    if (redirectUri) {
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&redirect_uri=%@", [Utils urlEncode: redirectUri] ]];
    }
    
    @try {
        //Replace first occurrence of '&'
        NSRange firstRange = [requestUrl rangeOfString:@"&"];
        requestUrl = [requestUrl stringByReplacingOccurrencesOfString:@"&" withString:@"?" options:0 range:firstRange];
    }
    @catch (NSException *exception) {
        //NSLog(@"Exception"); //Not params included in the request case
    }
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    // - - REQUEST CREATION - -
    
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"GET"];
    
    // - - HEADERS - -
    
    // Set content-type header
    NSString* encodedString = [Utils getEncryption:encryption client:clientId key:clientSecret traceMode:_traceMode];
    [request setValue:encodedString forHTTPHeaderField:@"Authorization"];
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    //IpAdresss
    if (ipAddress) {
        [request setValue:ipAddress forHTTPHeaderField:@"x-source-ip"];
    }
    
    NSString *uaString = [webview stringByEvaluatingJavaScriptFromString:@"navigator.userAgent"];
    [request setValue:uaString forHTTPHeaderField:@"User-agent"];
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        int responseCode=(int) [urlResponse statusCode];
        if (_traceMode) NSLog(@"Response code %d data %@",responseCode, data);

        
        //Check request process result
        if (error){//Error case in the request process.
            
            if (data==nil) {
                [[self discoveryDelegate] getDiscoveryError:(long)responseCode data:nil];
            } else {
            //NSLog(@"Error received from server,%@", error);
            
            //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            //Error parting JSON?
                if( parseError ) {
                
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil];
                    }
                
                }else{//Not error parsing JSON in error response from server
                
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:jsonResponseObj ];
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                }
            }
            
            
        }else{//Not error in request process
            
            if (data==nil) {
                [[self discoveryDelegate] getDiscoveryError:(long)responseCode data:nil];
            } else {

            // Parsing JSON to NSMutableDictionary object
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
                if( parseError ) {//Error parting JSON
                
                    if ([urlResponse  statusCode] == 0 ) {
                    
                        if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                        [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    
                    }else{
                    
                        if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil ];
                    }
                
                }else {
                
                    if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                        if (_traceMode) NSLog(@"%@", [parseError localizedDescription] );
                        [[self discoveryDelegate] getDiscoverySuccessful:-1 data:jsonResponseObj];
                    
                    }else if ([urlResponse  statusCode] == 302 ||[urlResponse  statusCode] == 202 ) {//Redirect URI open broser
                    
                        if (webview!=nil) {
                            @try {
                                NSArray* links = [jsonResponseObj objectForKey:@"links"];
                                if (_traceMode) NSLog(@"Active getDiscovery mode: Redirecting to... %@",[[links firstObject]objectForKey:@"href"]);
                        
                                dispatch_sync(dispatch_get_main_queue(), ^{
                                    webview.hidden=FALSE;
                                    [webview loadRequest: [NSURLRequest requestWithURL: [NSURL URLWithString:[[links firstObject]objectForKey:@"href"]] ] ];
                                });
                            }
                            @catch (NSException *exception) {
                                if (_traceMode) NSLog(@"Active mode: Error opening browser, bad json object format.Not redirect href found.");
                            }
                        } else {
                            [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                        }
                    
                    }else{//JSON response body and status code provided
                    
                        if ([urlResponse  statusCode] == 200 ) {
                        //Save dictionaryResponse to persistent mem
                            [utils saveLastDiscoveryResponse:jsonResponseObj];
                        }
                        [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                    
                }
            }
            
            
        }
        
    }];


}


- (void)getDiscoveryPassive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress  redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn{

    if (!utils) {
        utils = [[Utils alloc]init];
        utils.traceMode=_traceMode;
    }
    
    //UrlRequest
    NSMutableURLRequest *request;
    
    
    // - - URL PARAMETERS - -
    
    NSString* requestUrl = url;
    
    // - MCC MNC -
    NSString* mcc = [utils getMCC];
    NSString* mnc = [utils getMNC];
    
    if (mcc && mnc) {
        if (_traceMode) NSLog(@"Found mcc and mnc info in the device.");
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        [utils saveMCCtoCache:mcc];
        [utils saveMNCtoCache:mnc];
    }else{
        if (_traceMode) NSLog(@"Not mcc or mnc info found in device. Looking for it in mem cache...");
        //Not network info available. Cached data?
        mcc = [utils getCachedMCC];
        mnc = [utils getCachedMNC];
        
        if (mcc && mnc) {
            if (_traceMode) NSLog(@"Found mcc and mnc info in mem cache...");
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        }else{
            if (_traceMode) NSLog(@"ERROR obtaining the MCC MNC. Not param will be included in the request");
        }
    }

    // - MSISDN -
    if (msisdn) {
        msisdn = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&msisdn=%@", [Utils urlEncode: msisdn ]]];
    }
    
    // - REDIRECT URI -
    if (redirectUri) {
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&redirect_uri=%@", [Utils urlEncode: redirectUri]]];
    }
    
    @try {
        //Replace first occurrence of '&'
        NSRange firstRange = [requestUrl rangeOfString:@"&"];
        requestUrl = [requestUrl stringByReplacingOccurrencesOfString:@"&" withString:@"?" options:0 range:firstRange];
    }
    @catch (NSException *exception) {
        //NSLog(@"Exception"); //Not params included in the request case
    }
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    // - - REQUEST CREATION - -
    
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"GET"];
    
    // - - HEADERS - -
    
    // Set content-type header
    NSString* encodedString = [Utils getEncryption:encryption client:clientId key:clientSecret traceMode:_traceMode];
    [request setValue:encodedString forHTTPHeaderField:@"Authorization"];
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    //IpAdresss
    if (ipAddress) {
        [request setValue:ipAddress forHTTPHeaderField:@"x-source-ip"];
    }
    
    
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;
        
        int responseCode=(int) [urlResponse statusCode];
        if (_traceMode) NSLog(@"Response code %d data %@",responseCode, data);
        
        //Check request process result
        if (error){//Error case in the request process.
            
            if (data==nil) {
                [[self discoveryDelegate] getDiscoveryError:(long)responseCode data:nil];
            } else {
                //NSLog(@"Error received from server,%@", error);
            
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            //Error parting JSON?
                if( parseError ) {
                
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil];
                    }
                
                }else{//Not error parsing JSON in error response from server
                
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:jsonResponseObj ];
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                }
            }
            
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    
                }else{
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil ];
                    
                }
                
//            // Redirection response case
//            }else if ([urlResponse  statusCode] == 302 ||[urlResponse  statusCode] == 202 ) {//Redirect URI open broser
//                
//                @try {
//                    NSArray* links = [jsonResponseObj objectForKey:@"links"];
//                    NSLog(@"Active getDiscovery mode: Redirecting to... %@",[[links firstObject]objectForKey:@"href"]);
//
//                    //Return redirect Url
//                    //**********************//
//                    [NSURL URLWithString:[[links firstObject]objectForKey:@"href"]];
//                }
//                @catch (NSException *exception) {
//                    NSLog(@"Active mode: Error opening browser, bad json object format.Not redirect href found.");
//                }
//                
//                [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
//
                
            }else{
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog(@"%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoverySuccessful:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided
                    if ([urlResponse  statusCode] == 200 ) {
                        //Save dictionaryResponse to persistent mem
                        [utils saveLastDiscoveryResponse:jsonResponseObj];
                    }
                    [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    
                }
            }
            
            
        }
        
    }];
    
}




- (void)getDiscoveryPassive: (NSString*)url clientId:(NSString*)clientId clientSecret:(NSString*)clientSecret encryption:(encryptionTypeEnum)encryption ipAddress:(NSString*)ipAddress  redirectUri:(NSString*)redirectUri msisdn:(NSString*)msisdn  mcc:(NSString*)mcc mnc:(NSString*)mnc{

    
    if (!utils) {
        utils = [[Utils alloc]init];
        utils.traceMode=_traceMode;
    }
    
    //UrlRequest
    NSMutableURLRequest *request;
    
    
    // - - URL PARAMETERS - -
    
    NSString* requestUrl = url;
    
    // - MCC MNC -
    
    if (mcc && mnc) {
        if (_traceMode) NSLog(@"Found mcc and mnc info in the device.");
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        [utils saveMCCtoCache:mcc];
        [utils saveMNCtoCache:mnc];
    }else{
        if (_traceMode) NSLog(@"Not mcc or mnc info found in device. Looking for it in mem cache...");
        //Not network info available. Cached data?
        mcc = [utils getCachedMCC];
        mnc = [utils getCachedMNC];
        
        if (mcc && mnc) {
            if (_traceMode) NSLog(@"Found mcc and mnc info in mem cache...");
            requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&mcc_mnc=%@_%@", [Utils urlEncode:mcc], [Utils urlEncode:mnc]]];
        }else{
            if (_traceMode) NSLog(@"ERROR obtaining the MCC MNC. Not param will be included in the request");
        }
    }
    
    // - MSISDN -
    if (msisdn) {
        msisdn = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&msisdn=%@", [Utils urlEncode: msisdn]]];
    }
    
    // - REDIRECT URI -
    if (redirectUri) {
        requestUrl = [requestUrl stringByAppendingString: [NSString stringWithFormat: @"&redirect_uri=%@", [Utils urlEncode: redirectUri]]];
    }
    
    @try {
        //Replace first occurrence of '&'
        NSRange firstRange = [requestUrl rangeOfString:@"&"];
        requestUrl = [requestUrl stringByReplacingOccurrencesOfString:@"&" withString:@"?" options:0 range:firstRange];
    }
    @catch (NSException *exception) {
        //NSLog(@"Exception"); //Not params included in the request case
    }
    
    if (_traceMode) NSLog(@"Url of the request to send: %@", requestUrl);
    
    // - - REQUEST CREATION - -
    
    request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestUrl] ];
    // Request Type
    [request setHTTPMethod: @"GET"];
    
    // - - HEADERS - -
    
    // Set content-type header
    NSString* encodedString = [Utils getEncryption:encryption client:clientId key:clientSecret traceMode:_traceMode];
    [request setValue:encodedString forHTTPHeaderField:@"Authorization"];
    //Accept header
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    //Content-type
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-type"];
    //IpAdresss
    if (ipAddress) {
        [request setValue:ipAddress forHTTPHeaderField:@"x-source-ip"];
    }
    
    
    // - - REQUEST - -
    
    // Now send a request and get response Asynchronously
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    [NSURLConnection sendAsynchronousRequest:request queue:queue completionHandler:^(NSURLResponse *response, NSData *data, NSError *error){
        
        //Response object
        NSHTTPURLResponse* urlResponse = (NSHTTPURLResponse*)response;

        int responseCode=(int) [urlResponse statusCode];
        if (_traceMode) NSLog(@"Response code %d",responseCode);
        
        //Check request process result
        if (error){//Error case in the request process.
            
            if (data==nil) {
                [[self discoveryDelegate] getDiscoveryError:(long)responseCode data:nil];
            } else {
                //NSLog(@"Error received from server,%@", error);
            
                //Trying to parse json response
                NSError* parseError;
                NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
                //Error parting JSON?
                if( parseError ) {
                
                    if (_traceMode) NSLog(@"Error parsing JSON response: %@", [parseError localizedDescription] );
                
                    if ([urlResponse  statusCode] == 0 ) {//Error parsing server response. Unknown format response
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. Not JSON in response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    }else{
                        if (_traceMode) NSLog(@"Error parsing server response. \n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil];
                    }
                
                }else{//Not error parsing JSON in error response from server
                
                    if ([urlResponse  statusCode] == 0 ) {
                        if (_traceMode) NSLog(@"Error detected in request process. Not statusCode provided by server or bad format. JSON Response:\n%@",[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:-1 data:jsonResponseObj ];
                    }else{
                        if (_traceMode) NSLog(@"Error response from server. Status code: %ld\nJSON Response:\n%@",(long)[urlResponse  statusCode],[[NSString alloc] initWithData:data encoding:NSASCIIStringEncoding]);
                        [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    }
                }
            }
            
            
        }else{//Not error in request process
            
            // Parsing JSON to NSMutableDictionary object
            NSError* parseError;
            NSMutableDictionary* jsonResponseObj = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&parseError];
            
            if( parseError ) {//Error parting JSON
                
                if ([urlResponse  statusCode] == 0 ) {
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoveryError:-1 data:nil ];
                    
                }else{
                    
                    if (_traceMode) NSLog(@"Error parsing JSON response: \n%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoveryError:(long)[urlResponse  statusCode] data:nil ];
                    
                }
                
                //            // Redirection response case
                //            }else if ([urlResponse  statusCode] == 302 ||[urlResponse  statusCode] == 202 ) {//Redirect URI open broser
                //
                //                @try {
                //                    NSArray* links = [jsonResponseObj objectForKey:@"links"];
                //                    NSLog(@"Active getDiscovery mode: Redirecting to... %@",[[links firstObject]objectForKey:@"href"]);
                //
                //                    //Return redirect Url
                //                    //**********************//
                //                    [NSURL URLWithString:[[links firstObject]objectForKey:@"href"]];
                //                }
                //                @catch (NSException *exception) {
                //                    NSLog(@"Active mode: Error opening browser, bad json object format.Not redirect href found.");
                //                }
                //
                //                [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                //
                
            }else{
                
                if ([urlResponse  statusCode] == 0 ) {//JSON response body, but not status code provided/parsed
                    
                    if (_traceMode) NSLog(@"%@", [parseError localizedDescription] );
                    [[self discoveryDelegate] getDiscoverySuccessful:-1 data:jsonResponseObj];
                    
                }else{//JSON response body and status code provided
                    if ([urlResponse  statusCode] == 200 ) {
                        //Save dictionaryResponse to persistent mem
                        [utils saveLastDiscoveryResponse:jsonResponseObj];
                    }
                    [[self discoveryDelegate] getDiscoverySuccessful:(long)[urlResponse  statusCode] data:jsonResponseObj];
                    
                }
            }
            
            
        }
        
    }];
    
    
}


- (NSMutableDictionary*)getCachedDiscoveryItem{
    
    if (!utils) {
        utils = [[Utils alloc]init];
    }
    
    return [utils getLastDiscoveryResponse];

}

- (BOOL)clearCachedDiscoveryItem{
    
    if (!utils) {
        utils = [[Utils alloc]init];
    }
    
    [utils clearMobileNetwork_MCC_MNC];
    
    return [utils clearLastDiscoveryResponse];
    
}


- (NSMutableDictionary*)extractRedirectParameter: (NSString*) url{

    @try {
        
        //Get parameters from url
        NSArray *params = [url componentsSeparatedByString:@"?"];
        url = [params objectAtIndex:1];
        if (_traceMode) NSLog(@"%@",url);
        
        // Create an empty mutable dictionary for the result
        NSMutableDictionary *jobs = [NSMutableDictionary dictionary];
        
        //Split parameters and generate the result
        NSArray *splitResult = [url componentsSeparatedByString:@"&"];
        for(int i=0;i<[splitResult count];i++){
            
            NSString *str=[splitResult objectAtIndex:i];
            NSArray *aa=[str componentsSeparatedByString:@"="];
            
            if ([[aa objectAtIndex:0] compare:@"mcc_mnc" options:NSCaseInsensitiveSearch] == NSOrderedSame ) {
                if (_traceMode) NSLog(@"Found mcc and mnc parameter: %@  --> %@",[aa objectAtIndex:0], [aa objectAtIndex:1]);
                [jobs setObject: [aa objectAtIndex:1] forKey:[aa objectAtIndex:0]];
                //return jobs;
                //NSLog(@"Bingo %@", jobs);
            }
            
        }
    }
    @catch (NSException *exception) {
        if (_traceMode) NSLog(@"Exception ocurred parsing the url.");
    }
    return nil;
}


- (NSString *) responseField: (NSMutableDictionary *) discoveryData field: (NSString *) field {
    NSString *result=nil;
    if (discoveryData!=nil && discoveryData.count>0) {
        NSMutableDictionary *response=[discoveryData objectForKey:@"response"];
        result=[response objectForKey:field];
    }
    return result;
}


- (NSString *) endpoint: (NSMutableDictionary *) discoveryData api: (NSString *) api function: (NSString *) function {
    NSString *result=nil;
    if (discoveryData!=nil && discoveryData.count>0) {
        NSMutableDictionary *response=[discoveryData objectForKey:@"response"];
        
        if (response!=nil && response.count>0) {
            
            NSMutableDictionary *apis=[response objectForKey:@"apis"];
            if (apis!=nil && apis.count>0) {
                for(id apikey in apis) {
                    id apivalue = [apis objectForKey:apikey];
                    if ([apikey isEqualToString:api]) {
                        NSArray *links=[apivalue objectForKey:@"link"];
                        if (links!=nil && links.count>0) {
                            for (int i=0; i<links.count && result==nil; i++) {
                                NSMutableDictionary *linkData=[links objectAtIndex:i];
                                NSString *rel=[linkData objectForKey:@"rel"];
                                NSString *href=[linkData objectForKey:@"href"];
                                if ([rel isEqualToString: function]) {
                                    result=href;
                                }
                            } // Loop around links
                        } // Test for links
                    } // Found API
                    if (result!=nil) break;
                } // Loop around APIs
            } // Test for any APIs
        } // Test for response
    }
    return result;
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType
{
    if (_traceMode) NSLog(@"Loading URL :%@",request.URL.absoluteString);
    BOOL load=YES;
    
    if ([request.URL.absoluteString hasPrefix:self.redirectUri]) {
        NSArray *queryItems=[[[NSURLComponents alloc] initWithString:request.URL.absoluteString] queryItems];
        if (queryItems!=nil && queryItems.count>0) {
            NSString *mcc_mnc=nil;
            for (int i=0; i<queryItems.count && mcc_mnc==nil; i++) {
//                NSLog(@"[%d] = %@", i, [queryItems objectAtIndex:i]);
                NSURLQueryItem *qi=[queryItems objectAtIndex:i];
                if ([qi.name isEqualToString:@"mcc_mnc"]) {
                    mcc_mnc=qi.value;
                }
            }
            if (mcc_mnc!=nil) {
                NSArray* hniArray = [mcc_mnc componentsSeparatedByString: @"_"];
//                NSLog(@"number of elements=%d", ((int) [hniArray count]));
                if ([hniArray count]==2) {
                    NSString* mcc = [hniArray objectAtIndex: 0];
                    NSString* mnc = [hniArray objectAtIndex: 1];
                    if (_traceMode) NSLog(@"Invoking second phase of discovery for mcc = %@ mnc = %@", mcc, mnc);
                    
                    [self getDiscoveryPassive:self.discoveryurl
                                     clientId:self.clientId
                                 clientSecret:self.clientSecret
                                   encryption:self.encryption
                                    ipAddress:nil
                                  redirectUri:self.redirectUri
                                       msisdn:nil
                                          mcc:mcc
                                          mnc:mnc];
                }
            }
        }
        [webView stopLoading];
        webView.hidden=TRUE;
        load=NO;
    }
    
    return load;
}



@end
